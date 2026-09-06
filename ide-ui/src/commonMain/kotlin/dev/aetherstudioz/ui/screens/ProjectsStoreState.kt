package dev.aetherstudioz.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.aetherstudioz.ui.backend.IdeBackend
import dev.aetherstudioz.ui.backend.UiStoreCatalog
import dev.aetherstudioz.ui.backend.UiStoreItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * State and intents for the Projects Store: the catalog it browses, and the debounced search that replaces
 * the browse view while a query or category is active.
 */
@Stable
internal class ProjectsStoreState(
    private val backend: IdeBackend,
    private val scope: CoroutineScope,
) {
    var catalog: UiStoreCatalog by mutableStateOf(UiStoreCatalog())
        private set
    var query: String by mutableStateOf("")
        private set
    var category: String? by mutableStateOf(null)
        private set
    var results: List<UiStoreItem> by mutableStateOf(emptyList())
        private set

    /** True while a query or category narrows the view, so the results list replaces the browse sections. */
    val filtering: Boolean get() = query.isNotBlank() || category != null

    init {
        scope.launch { catalog = runCatching { backend.store.catalog() }.getOrDefault(UiStoreCatalog()) }
        scope.launch {
            snapshotFlow { Triple(query, category, filtering) }.collectLatest { (text, cat, active) ->
                if (!active) {
                    results = emptyList()
                    return@collectLatest
                }
                delay(180)
                results = runCatching { backend.store.search(text, cat) }.getOrDefault(emptyList())
            }
        }
    }

    fun updateQuery(value: String) { query = value }

    fun selectCategory(value: String?) { category = value }
}

@Composable
internal fun rememberProjectsStoreState(
    backend: IdeBackend,
    scope: CoroutineScope = rememberCoroutineScope(),
): ProjectsStoreState = remember(backend, scope) { ProjectsStoreState(backend, scope) }
