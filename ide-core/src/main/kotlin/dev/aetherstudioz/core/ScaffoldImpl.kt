package dev.aetherstudioz.core

import dev.aetherstudioz.model.LanguageLevel
import dev.aetherstudioz.model.ModuleType
import dev.aetherstudioz.model.Workspace
import dev.aetherstudioz.model.impl.ProjectModelStore
import dev.aetherstudioz.model.template.ProjectScaffold
import dev.aetherstudioz.vfs.VirtualFile
import java.nio.file.Files
import kotlin.io.path.writeText

/**
 * The [ProjectScaffold] a [dev.aetherstudioz.model.template.ProjectTemplate] builds against, backed by a
 * [ProjectModelStore]. Exposes the store's workspace transaction surface and a `java.nio` file writer
 * rooted at the workspace dir — the same write path `SampleProject`/`SampleAndroidProject` use.
 */
internal class ScaffoldImpl(
    private val store: ProjectModelStore,
    override val languageLevel: LanguageLevel,
) : ProjectScaffold {
    override val workspace: Workspace get() = store.workspace
    override val rootDir: VirtualFile get() = store.vfs.root()

    override fun moduleType(id: String): ModuleType = store.moduleTypes.resolve(id)

    override fun writeText(relPath: String, content: String) {
        val file = store.rootPath.resolve(relPath)
        Files.createDirectories(file.parent)
        // trimIndent() drops the leading/trailing blank lines of a triple-quoted literal + common indent.
        file.writeText(content.trimIndent() + "\n")
    }

    override fun writeBytes(relPath: String, bytes: ByteArray) {
        val file = store.rootPath.resolve(relPath)
        Files.createDirectories(file.parent)
        Files.write(file, bytes) // byte-exact: no trim/newline mutation, so binary assets survive
    }
}
