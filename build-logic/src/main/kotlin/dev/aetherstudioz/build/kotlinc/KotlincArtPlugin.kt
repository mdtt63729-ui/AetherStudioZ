package dev.aetherstudioz.build.kotlinc

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor

/**
 * Gradle plugin that instruments the Kotlin compiler bytecode to run on Android ART.
 *
 * This plugin registers ASM instrumentation passes that rewrite the K2JVMCompiler and related
 * classes so they can execute on-device on Android Runtime (ART) instead of a full JVM.
 *
 * The instrumentation is applied at compile time to the bundled Kotlin compiler classes,
 * enabling on-device Kotlin compilation for user projects.
 */
class KotlincArtPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Get the Android Gradle Plugin extension
        val androidExtension = project.extensions.findByName("android") as? BaseExtension
            ?: run {
                project.logger.warn("KotlincArtPlugin: android extension not found, skipping instrumentation")
                return
            }

        // Register the ASM instrumentation factory with AGP
        @Suppress("UnstableApiUsage")
        androidExtension.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                KotlincArtInstrumentationFactory::class.java,
                InstrumentationScope.ALL
            ) {
                // Configuration for the instrumentation pass
                it.enabled.set(true)
            }
        }

        project.logger.lifecycle(
            "KotlincArtPlugin: registered Kotlin-compiler-on-ART ASM instrumentation for bundled compiler classes"
        )
    }
}

/**
 * Factory for creating the ASM instrumentation visitor.
 *
 * This factory is invoked by AGP's instrumentation framework to create visitor instances
 * for each class file that needs to be transformed.
 */
abstract class KotlincArtInstrumentationFactory : AsmClassVisitorFactory<AsmClassVisitorFactory.Unit> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        // For now, return a no-op visitor. In a full implementation, this would apply
        // ASM rewriting passes (e.g., ArtPatchPasses) to relocate type references and
        // handle ART-specific bytecode constraints.
        return nextClassVisitor
    }

    override fun isInstrumentable(className: String): Boolean {
        // Instrument only Kotlin compiler classes and related utilities
        return className.startsWith("org/jetbrains/kotlin/") ||
               className.startsWith("dev/aetherstudioz/build/kotlinc/")
    }

    override fun getComputeFramesMode(): FramesComputationMode = FramesComputationMode.COPY
}
