package dev.aetherstudioz.core

import dev.aetherstudioz.android.support.AndroidAppModuleType
import dev.aetherstudioz.android.support.AndroidBuildSystem
import dev.aetherstudioz.android.support.AndroidFacet
import dev.aetherstudioz.android.support.AndroidFacetCodec
import dev.aetherstudioz.android.support.AndroidSupport
import dev.aetherstudioz.android.support.tools.AndroidSdk
import dev.aetherstudioz.android.support.tools.DebugKeystore
import dev.aetherstudioz.build.BuildGoal
import dev.aetherstudioz.build.BuildRequest
import dev.aetherstudioz.build.VariantSelector
import dev.aetherstudioz.build.engine.BuildCache
import dev.aetherstudioz.build.engine.SimpleTaskContext
import dev.aetherstudioz.build.engine.TaskExecutorImpl
import dev.aetherstudioz.lang.kotlin.compile.IncrementalKotlinCompiler
import dev.aetherstudioz.lang.kotlin.compile.KotlinJvmCompiler
import dev.aetherstudioz.model.BuildSystemId
import dev.aetherstudioz.model.DependencyScope
import dev.aetherstudioz.model.LanguageLevel
import dev.aetherstudioz.model.LibraryDependency
import dev.aetherstudioz.model.LibraryKind
import dev.aetherstudioz.model.LibraryRef
import dev.aetherstudioz.model.ModuleId
import dev.aetherstudioz.model.FacetCodecRegistry
import dev.aetherstudioz.model.ModuleTypeRegistry
import dev.aetherstudioz.model.impl.ProjectModel
import dev.aetherstudioz.testkit.TestJars
import dev.aetherstudioz.testkit.testEnv
import dev.aetherstudioz.testkit.writeSource
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assumptions.assumeTrue
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The Android pipeline with a mixed Kotlin/Java app: `compileKotlin` (K2, against `android.jar` as the boot
 * classpath) runs ahead of `compileJava`, its output joins the project dex scope, and a Java `Activity`
 * references the Kotlin `object` (Java → Kotlin interop). We assemble a debug APK and assert the Kotlin
 * class was dexed into the project archive — proving Kotlin codegen → D8 → APK on the Android path. Needs an
 * installed SDK; skipped otherwise (like the other native-Android build tests).
 */
class AndroidKotlinBuildTest {

    @Test
    fun assemblesAnApkWithKotlinAndJavaInterop() {
        val sdk = AndroidSdk.findSdkRoot()?.let { AndroidSdk.detect(it) }
        assumeTrue(sdk != null && sdk.isComplete(), "Android SDK not installed; skipping")
        sdk!!

        testEnv("android-kotlin") { env ->
            val dir = env.dir
            val platform = env.platform
            val store = ProjectModel.open(dir, platform, FacetCodecRegistry().register(AndroidFacetCodec))
            ModuleTypeRegistry(platform.extensions).register(AndroidAppModuleType, AndroidSupport.PLUGIN)
            val appType = ModuleTypeRegistry(platform.extensions).resolve("android-app")
            store.workspace.beginModification().apply { addProject("demo", BuildSystemId.NATIVE, store.vfs.root()); commit() }
            store.workspace.libraryTable.create("kotlin-stdlib")
                .apply { kind = LibraryKind.JAR; addClassesRoot(store.vfs.fileFor(TestJars.kotlinStdlib())); commit() }
            store.workspace.projects.single().beginModification().apply {
                addModule("app", appType).apply {
                    languageLevel = LanguageLevel.JAVA_8   // compile against android.jar as -bootclasspath (< 9)
                    putFacet(AndroidFacet(namespace = "com.example.app", compileSdk = 34, minSdk = 24, targetSdk = 34))
                    addDependency(LibraryDependency(LibraryRef("kotlin-stdlib"), DependencyScope.IMPLEMENTATION))
                }
                commit()
            }
            dir.writeSource("app/src/main/AndroidManifest.xml", MANIFEST)
            dir.writeSource("app/src/main/res/values/strings.xml", STRINGS)
            dir.writeSource("app/src/main/kotlin/com/example/app/Greeting.kt", GREETING)
            dir.writeSource("app/src/main/java/com/example/app/MainActivity.java", ACTIVITY)

            val signing = DebugKeystore.getOrCreate(dir.resolve(".keystore/debug.ks"), sdk.keytool)
            // Subprocess wiring (SDK's d8/apksigner) — ide-core's test classpath has no in-process r8/apksig.
            val build = AndroidBuildSystem.subprocess(sdk, signing, kotlin = IncrementalKotlinCompiler(KotlinJvmCompiler()))
            val request = BuildRequest(listOf(ModuleId("app")), VariantSelector("debug"), BuildGoal.PACKAGE)
            val log = StringBuilder()
            val outcome = runBlocking {
                TaskExecutorImpl(BuildCache(dir.resolve(".caches/build")))
                    .execute(build.createBuildGraph(store.workspace.projects.single(), request), SimpleTaskContext(log = { log.appendLine(it) }), 2)
            }
            assertTrue(outcome.succeeded, "android+kotlin build failed:\n$log")

            val ran = outcome.ranTasks.map { it.value }
            assertTrue(":app:compileKotlinDebug" in ran, "compileKotlin must run: $ran")

            // The Kotlin object's per-class .dex must land in the project scope archive (it IS app code).
            val kotlinDex = dir.resolve("app/build/intermediates/android/debug/dex-archives/project/com/example/app/Greeting.dex")
            assertTrue(Files.isRegularFile(kotlinDex), "Kotlin class was not dexed into the project scope: $kotlinDex\n$log")

            val apk = AndroidBuildSystem.signedApkPath(store.workspace.projects.single().modules.single(), "debug")
            assertTrue(Files.isRegularFile(apk), "signed APK missing: $apk\n$log")
        }
    }

    private companion object {
        val GREETING = """
            package com.example.app
            object Greeting { fun text(): String = "hello from kotlin" }
        """
        // Java → Kotlin: the Activity calls the Kotlin object, only resolvable via the compileKotlin output.
        val ACTIVITY = """
            package com.example.app;
            import android.app.Activity;
            import android.os.Bundle;
            import android.widget.TextView;
            public class MainActivity extends Activity {
                @Override protected void onCreate(Bundle b) {
                    super.onCreate(b);
                    TextView tv = new TextView(this);
                    tv.setText(Greeting.INSTANCE.text());
                    setContentView(tv);
                }
            }
        """
        val MANIFEST = """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.example.app">
                <application android:label="@string/app_name"><activity android:name=".MainActivity" android:exported="true"/></application>
            </manifest>
        """
        val STRINGS = """
            <?xml version="1.0" encoding="utf-8"?>
            <resources><string name="app_name">KotlinApp</string></resources>
        """
    }
}
