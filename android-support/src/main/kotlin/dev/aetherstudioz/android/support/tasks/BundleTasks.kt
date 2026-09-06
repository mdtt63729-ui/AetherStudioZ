package dev.aetherstudioz.android.support.tasks

import dev.aetherstudioz.android.support.tools.BundleRequest
import dev.aetherstudioz.android.support.tools.BundleSigner
import dev.aetherstudioz.android.support.tools.Bundler
import dev.aetherstudioz.android.support.tools.SigningConfig
import dev.aetherstudioz.build.DiagnosticKind
import dev.aetherstudioz.build.Task
import dev.aetherstudioz.build.TaskContext
import dev.aetherstudioz.build.TaskInputs
import dev.aetherstudioz.build.TaskInputsImpl
import dev.aetherstudioz.build.TaskName
import dev.aetherstudioz.build.TaskOutputs
import dev.aetherstudioz.build.TaskOutputsImpl
import dev.aetherstudioz.build.TaskResult
import dev.aetherstudioz.build.engine.reportToolDiagnostics
import java.nio.file.Files
import java.nio.file.Path

/**
 * `package<Variant>Bundle`: assemble the bundletool base module zip ([BundlePackaging]) from the proto
 * resources + dex + assets + native libs, then run `bundletool build-bundle` to produce the unsigned `.aab`.
 */
internal class BundleTask(
    override val name: TaskName,
    private val protoAp: Path,
    private val dexDirs: List<Path>,
    private val assetsDirs: List<Path>,
    private val jniLibDirs: List<Path>,
    private val outAab: Path,
    private val baseModuleZip: Path,
    private val bundler: Bundler,
    private val javaResJars: List<Path> = emptyList(),
) : Task {
    override val inputs: TaskInputs
        get() = TaskInputsImpl().apply {
            filePaths("ap", listOf(protoAp).filter { Files.exists(it) })
            dirPaths("dex", dexDirs)
            dirPaths("assets", assetsDirs)
            dirPaths("jni", jniLibDirs)
            filePaths("javaRes", javaResJars)
        }
    override val outputs: TaskOutputs get() = TaskOutputsImpl().apply { filePath("aab", outAab) }

    override suspend fun execute(ctx: TaskContext): TaskResult {
        ctx.checkCanceled()
        if (!Files.isRegularFile(protoAp))
            return TaskResult.Failed("proto resources missing (aapt2 --proto-format link did not run): $protoAp")
        return runCatching {
            val entries = BundlePackaging.buildBaseModuleZip(protoAp, dexDirs, assetsDirs, jniLibDirs, baseModuleZip, javaResJars)
            ctx.logger()("bundle module -> base.zip (${entries.size} entries)")
            val r = bundler.bundle(BundleRequest(listOf(baseModuleZip), outAab))
            r.log.forEach(ctx.logger())
            ctx.reportToolDiagnostics("bundletool", r.log, DiagnosticKind.PACKAGING)
            if (r.success) TaskResult.Success as TaskResult
            else TaskResult.Failed("bundletool build-bundle failed")
        }.getOrElse { TaskResult.Failed("bundle failed: ${it.message}", it) }
    }
}

/** `sign<Variant>Bundle`: JAR (v1) sign the `.aab` with the project keystore. */
internal class SignBundleTask(
    override val name: TaskName,
    private val unsignedAab: Path,
    private val signedAab: Path,
    private val config: SigningConfig,
    private val minApi: Int,
    private val signer: BundleSigner,
) : Task {
    override val inputs: TaskInputs
        get() = TaskInputsImpl().apply {
            filePaths("unsigned", listOf(unsignedAab).filter { Files.exists(it) })
            property("keystore", config.keystore.toString())
            property("alias", config.keyAlias)
        }
    override val outputs: TaskOutputs get() = TaskOutputsImpl().apply { filePath("aab", signedAab) }

    override suspend fun execute(ctx: TaskContext): TaskResult {
        ctx.checkCanceled()
        if (!Files.isRegularFile(unsignedAab)) return TaskResult.Failed("unsigned .aab missing: $unsignedAab")
        val r = signer.sign(unsignedAab, signedAab, config, minApi)
        r.log.forEach(ctx.logger())
        ctx.reportToolDiagnostics("bundle-signer", r.log, DiagnosticKind.PACKAGING)
        return if (r.success) TaskResult.Success else TaskResult.Failed("bundle signing failed")
    }
}
