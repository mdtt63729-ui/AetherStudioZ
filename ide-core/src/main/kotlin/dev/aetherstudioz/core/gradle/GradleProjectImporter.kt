package dev.aetherstudioz.core.gradle

import dev.aetherstudioz.android.support.AndroidApiLevels
import dev.aetherstudioz.android.support.AndroidFacet
import dev.aetherstudioz.android.support.AndroidFacetCodec
import dev.aetherstudioz.android.support.BuildFeatures
import dev.aetherstudioz.android.support.BuildType
import dev.aetherstudioz.android.support.ProductFlavor
import dev.aetherstudioz.core.GradleImport
import dev.aetherstudioz.model.BuildSystemId
import dev.aetherstudioz.model.ContentRole
import dev.aetherstudioz.model.Coordinate
import dev.aetherstudioz.model.DependencyScope
import dev.aetherstudioz.model.sync.Detection
import dev.aetherstudioz.model.sync.ExternalDependency
import dev.aetherstudioz.model.sync.ExternalFacet
import dev.aetherstudioz.model.sync.ExternalLibrary
import dev.aetherstudioz.model.sync.ExternalModule
import dev.aetherstudioz.model.sync.ExternalModuleRef
import dev.aetherstudioz.model.sync.ExternalPlatform
import dev.aetherstudioz.model.sync.ExternalProjectModel
import dev.aetherstudioz.model.sync.ExternalRepository
import dev.aetherstudioz.model.sync.ExternalSourceSet
import dev.aetherstudioz.model.sync.ModelOwnership
import dev.aetherstudioz.model.sync.ProjectImporter
import dev.aetherstudioz.model.sync.SyncMessage
import dev.aetherstudioz.model.sync.SyncOutcome
import dev.aetherstudioz.model.sync.SyncRequest
import dev.aetherstudioz.model.sync.SyncSeverity
import java.nio.file.Path

/**
 * The [ProjectImporter] for Gradle projects: it reads the scripts with [GradleImport] (a tolerant,
 * non-evaluating reader) and maps what it found onto an [ExternalProjectModel]. The scripts stay the source
 * of truth, so every sync re-derives the model from them.
 *
 * The mapping is all that lives here. Reading the scripts belongs to [GradleImport], applying the snapshot to
 * the model belongs to [dev.aetherstudioz.model.impl.ExternalModelApplier], and the Android facet crosses the boundary
 * as the `[android]` table's values, encoded by the codec that also persists it.
 */
class GradleProjectImporter : ProjectImporter {

    override val id: BuildSystemId = BuildSystemId.GRADLE_COMPAT

    override val displayName: String = "Gradle"

    override val ownership: ModelOwnership = ModelOwnership.EXTERNAL

    override fun detect(root: Path): Detection? {
        if (!GradleImport.isGradleProject(root)) return null
        return Detection(
            name = root.fileName?.toString() ?: "gradle-project",
            markers = GradleImport.buildFiles(root),
            confidence = 10,
        )
    }

    override fun syncFiles(): List<String> = listOf(
        "settings.gradle", "settings.gradle.kts",
        "build.gradle", "build.gradle.kts",
        "**/build.gradle", "**/build.gradle.kts",
        "gradle.properties", "**/gradle.properties",
        "gradle/libs.versions.toml", "libs.versions.toml",
        // Convention plugins: their scripts and constants feed the parse, so a change there changes the model.
        "buildSrc/**/*.kt", "buildSrc/**/*.gradle.kts",
        "build-logic/**/*.kt", "build-logic/**/*.gradle.kts",
    )

    override suspend fun resolve(request: SyncRequest): SyncOutcome {
        request.progress.report(-1.0, "Reading the Gradle build scripts")
        val spec = GradleImport.parse(request.root)
            ?: return SyncOutcome.failed("No Gradle build scripts were found to sync from.")
        val messages = spec.report.notes.map { SyncMessage(SyncSeverity.WARNING, it) }
        return SyncOutcome(model(spec), messages)
    }

    /** The parsed scripts as a snapshot. */
    private fun model(spec: GradleImport.ProjectSpec): ExternalProjectModel = ExternalProjectModel(
        name = spec.name,
        buildSystemId = id,
        modules = spec.modules.map { module(it) },
        repositories = spec.customRepos.map { ExternalRepository(it.name, it.url) },
    )

    private fun module(spec: GradleImport.ModuleSpec): ExternalModule = ExternalModule(
        name = spec.name,
        dirRelPath = spec.dirRel.ifBlank { spec.name },
        typeId = typeIdFor(spec.kind),
        sourceSets = sourceSets(spec),
        dependencies = dependencies(spec),
        facets = facets(spec),
    )

    private fun typeIdFor(kind: GradleImport.Kind): String = when (kind) {
        GradleImport.Kind.ANDROID_APP -> "android-app"
        GradleImport.Kind.ANDROID_LIB -> "android-lib"
        GradleImport.Kind.JAVA -> "java-lib"
    }

    /** Android module types supply their own `src/main/{java,kotlin,res,assets}` sets, so only a plain
     *  JVM module needs its Gradle-convention source roots spelled out. */
    private fun sourceSets(spec: GradleImport.ModuleSpec): List<ExternalSourceSet> =
        if (spec.kind != GradleImport.Kind.JAVA) emptyList()
        else listOf(
            ExternalSourceSet(
                name = "main",
                scope = DependencyScope.IMPLEMENTATION,
                roots = linkedMapOf(
                    "src/main/java" to setOf(ContentRole.SOURCE),
                    "src/main/kotlin" to setOf(ContentRole.SOURCE),
                    "src/main/resources" to setOf(ContentRole.RESOURCE),
                ),
            )
        )

    private fun dependencies(spec: GradleImport.ModuleSpec): List<ExternalDependency> = buildList {
        for (d in spec.moduleDeps) add(ExternalModuleRef(d.name, d.scope, d.variant))
        for (d in spec.platformDeps) {
            coordinateOrNull(d.coordinate)?.let { add(ExternalPlatform(it, d.scope, d.variant)) }
        }
        for (d in spec.mavenDeps) add(ExternalLibrary(d.coordinate, d.scope, d.variant))
    }

    /** The `android { }` block as the `[android]` facet table, absent for a plain JVM module. */
    private fun facets(spec: GradleImport.ModuleSpec): List<ExternalFacet> =
        if (spec.kind == GradleImport.Kind.JAVA) emptyList()
        else listOf(ExternalFacet(AndroidFacetCodec.tomlTable, AndroidFacetCodec.encode(androidFacet(spec))))

    private fun androidFacet(spec: GradleImport.ModuleSpec): AndroidFacet = AndroidFacet(
        namespace = spec.namespace ?: "com.example.${spec.name}",
        compileSdk = spec.compileSdk ?: DEFAULT_COMPILE_SDK,
        minSdk = spec.minSdk ?: DEFAULT_MIN_SDK,
        targetSdk = spec.targetSdk ?: spec.minSdk ?: DEFAULT_MIN_SDK,
        // Only override the facet defaults when actually parsed, so an unset value keeps deferring to the
        // manifest (AndroidFacet's DSL-wins rule).
        versionCode = spec.versionCode ?: AndroidFacet.DEFAULT_VERSION_CODE,
        versionName = spec.versionName ?: AndroidFacet.DEFAULT_VERSION_NAME,
        manifestPlaceholders = spec.manifestPlaceholders,
        isApplication = spec.kind == GradleImport.Kind.ANDROID_APP,
        flavorDimensions = spec.flavorDimensions,
        buildTypes = if (spec.buildTypes.isEmpty()) AndroidFacet.DEFAULT_BUILD_TYPES
        else spec.buildTypes.map {
            BuildType(
                it.name,
                debuggable = it.debuggable ?: (it.name == "debug"),
                minifyEnabled = it.minifyEnabled,
                shrinkResources = it.shrinkResources,
                proguardFiles = it.proguardFiles,
                applicationIdSuffix = it.applicationIdSuffix,
                versionNameSuffix = it.versionNameSuffix,
                manifestPlaceholders = it.manifestPlaceholders,
            )
        },
        productFlavors = spec.productFlavors.map {
            ProductFlavor(it.name, dimension = it.dimension, manifestPlaceholders = it.manifestPlaceholders)
        },
        buildFeatures = BuildFeatures(
            viewBinding = spec.viewBinding,
            compose = spec.isCompose,
            parcelize = spec.parcelize,
            serialization = spec.serialization,
            kspProcessors = spec.kspProcessors,
        ),
    )

    private fun coordinateOrNull(coordinate: String): Coordinate? {
        val parts = coordinate.split(":")
        return when (parts.size) {
            2 -> Coordinate(parts[0], parts[1], "")
            3 -> Coordinate(parts[0], parts[1], parts[2])
            else -> null
        }
    }

    private companion object {
        /** Only a fallback: it applies when the scripts declare no `compileSdk` at all (an unreadable
         *  convention plugin), so it tracks the newest level the IDE supports rather than lagging it. */
        const val DEFAULT_COMPILE_SDK = AndroidApiLevels.LATEST
        const val DEFAULT_MIN_SDK = 21
    }
}
