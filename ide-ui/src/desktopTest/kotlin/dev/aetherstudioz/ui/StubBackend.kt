package dev.aetherstudioz.ui

import dev.aetherstudioz.ui.backend.ActionService
import dev.aetherstudioz.ui.backend.BlockService
import dev.aetherstudioz.ui.backend.BuildService
import dev.aetherstudioz.ui.backend.BuildState
import dev.aetherstudioz.ui.backend.DependencyService
import dev.aetherstudioz.ui.backend.DiagnosticsService
import dev.aetherstudioz.ui.backend.EditorService
import dev.aetherstudioz.ui.backend.FileService
import dev.aetherstudioz.ui.backend.IdeBackend
import dev.aetherstudioz.ui.backend.IndexUiStatus
import dev.aetherstudioz.ui.backend.SigningService
import dev.aetherstudioz.ui.backend.ModuleService
import dev.aetherstudioz.ui.backend.NodeKind
import dev.aetherstudioz.ui.backend.PreviewService
import dev.aetherstudioz.ui.backend.ProjectInfo
import dev.aetherstudioz.ui.backend.ProjectService
import dev.aetherstudioz.ui.backend.SdkService
import dev.aetherstudioz.ui.backend.SearchService
import dev.aetherstudioz.ui.backend.SettingsService
import dev.aetherstudioz.ui.backend.SymbolHit
import dev.aetherstudioz.ui.backend.TreeNode
import dev.aetherstudioz.ui.backend.TreeViewMode
import dev.aetherstudioz.ui.backend.UiCompletionResult
import dev.aetherstudioz.ui.backend.UiDiagnostic
import kotlinx.coroutines.flow.MutableStateFlow
import dev.aetherstudioz.ui.backend.IconService
import kotlinx.coroutines.flow.StateFlow

/**
 * A no-op [IdeBackend] for tests: it implements every concern service (via `get() = this`) and stubs the
 * abstract members, so a test fake only overrides what it exercises. All members are `open`.
 */
internal open class StubBackend : IdeBackend,
    FileService, EditorService, BlockService, PreviewService, SearchService, BuildService,
    DependencyService, ModuleService, SigningService, ProjectService, SdkService, SettingsService, ActionService,
    DiagnosticsService, IconService {

    override val files: FileService get() = this
    override val editor: EditorService get() = this
    override val blocks: BlockService get() = this
    override val preview: PreviewService get() = this
    override val search: SearchService get() = this
    override val build: BuildService get() = this
    override val deps: DependencyService get() = this
    override val modules: ModuleService get() = this
    override val signing: SigningService get() = this
    override val projects: ProjectService get() = this
    override val sdk: SdkService get() = this
    override val settings: SettingsService get() = this
    override val actions: ActionService get() = this
    override val diagnostics: DiagnosticsService get() = this
    override val icons: IconService get() = this

    override val project: ProjectInfo = ProjectInfo("stub", "/stub", 0)

    // FileService (abstract)
    override fun fileTree(mode: TreeViewMode): TreeNode = TreeNode("root", "stub", NodeKind.Workspace, null)
    override fun readFile(path: String): String = ""
    override fun moduleNameForFile(path: String): String? = null

    // EditorService (abstract)
    override fun updateDocument(path: String, text: String) {}
    override fun saveFile(path: String, text: String) {}
    override suspend fun complete(path: String, text: String, offset: Int): UiCompletionResult =
        UiCompletionResult(emptyList(), offset, offset)
    override suspend fun analyze(path: String, text: String): List<UiDiagnostic> = emptyList()

    // SearchService (abstract)
    override val indexStatus: StateFlow<IndexUiStatus> = MutableStateFlow(IndexUiStatus())
    override suspend fun searchSymbols(query: String, limit: Int): List<SymbolHit> = emptyList()
    override suspend fun searchMembers(query: String, limit: Int): List<SymbolHit> = emptyList()

    // BuildService (abstract)
    override val buildState: StateFlow<BuildState> = MutableStateFlow(BuildState())
    override fun runBuild() {}
    override fun stopBuild() {}
}
