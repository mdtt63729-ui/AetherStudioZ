package dev.aetherstudioz.android.support.tasks

import dev.aetherstudioz.android.support.gms.GoogleServices
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
 * `process<Variant>GoogleServices`: parse the module's `google-services.json` and write the generated
 * `values.xml` (`google_app_id`, `gcm_defaultSenderId`, `project_id`, …) into [outResDir], which the
 * resource merge then picks up. The on-device counterpart of the Google Services Gradle plugin. Runs only
 * when a `google-services.json` is present; a present-but-mismatched file fails the build (as the plugin does).
 */
internal class ProcessGoogleServicesTask(
    override val name: TaskName,
    private val googleServicesJson: Path,
    private val applicationId: String,
    private val fallbackPackage: String,
    private val outResDir: Path,
) : Task {
    override val inputs: TaskInputs
        get() = TaskInputsImpl().apply {
            filePaths("json", listOf(googleServicesJson).filter { Files.exists(it) })
            property("applicationId", applicationId)
            property("fallbackPackage", fallbackPackage)
        }
    override val outputs: TaskOutputs get() = TaskOutputsImpl().apply { dirPath("res", outResDir) }

    override suspend fun execute(ctx: TaskContext): TaskResult {
        ctx.checkCanceled()
        val text = runCatching { Files.readAllBytes(googleServicesJson).toString(Charsets.UTF_8) }
            .getOrElse { return TaskResult.Failed("cannot read ${googleServicesJson.fileName}: ${it.message}") }

        val valuesFile = outResDir.resolve("values").resolve("values.xml")
        return when (val outcome = GoogleServices.process(text, applicationId, fallbackPackage)) {
            is GoogleServices.Outcome.Failure -> {
                ctx.reportToolDiagnostics("google-services", listOf(outcome.message), DiagnosticKind.RESOURCE)
                TaskResult.Failed(outcome.message)
            }
            is GoogleServices.Outcome.Success -> {
                outcome.messages.forEach(ctx.logger())
                Files.createDirectories(valuesFile.parent)
                Files.write(valuesFile, GoogleServices.valuesXml(outcome.resources).toByteArray(Charsets.UTF_8))
                ctx.logger()("processGoogleServices -> ${outcome.resources.size} resource(s) for ${outcome.matchedPackage}")
                TaskResult.Success
            }
        }
    }
}
