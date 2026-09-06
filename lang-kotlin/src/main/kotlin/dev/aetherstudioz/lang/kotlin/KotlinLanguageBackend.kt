package dev.aetherstudioz.lang.kotlin

import dev.aetherstudioz.lang.BackendCapability
import dev.aetherstudioz.lang.CompilationContext
import dev.aetherstudioz.lang.LanguageBackend
import dev.aetherstudioz.lang.LanguageId
import dev.aetherstudioz.lang.SourceAnalyzer

/**
 * The Kotlin [LanguageBackend], providing editor-time code intelligence.
 *
 * It parses with the Kotlin PSI parser but discards the compiler's resolution/FIR, building its own
 * symbol table, inference, and completion on the neutral DOM.
 *
 * Capabilities: ERROR_RECOVERY (the tolerant PSI parse), BINDINGS, and COMPLETION are provided by the
 * resolve/completion packages.
 *
 * Host wiring: ide-core registers it on `LANGUAGE_BACKEND_EP`, maps `.kt -> kotlin` in `languageFor`, and
 * injects the index service in `analyzerFor` via `is KotlinSourceAnalyzer -> it.indexService = indexService`,
 * mirroring the JDT/XML backends.
 */
class KotlinLanguageBackend : LanguageBackend {
    override val id: String = "kotlin"
    override val languages: Set<LanguageId> = setOf(LANGUAGE_ID)
    override val capabilities: Set<BackendCapability> = setOf(
        BackendCapability.ERROR_RECOVERY,
        BackendCapability.BINDINGS,
        BackendCapability.COMPLETION,
        BackendCapability.INLAY_HINTS,
        BackendCapability.SIGNATURE_HELP,
        BackendCapability.SEMANTIC_HIGHLIGHT,
        BackendCapability.CODE_FOLDING,
        BackendCapability.FORMAT,
        BackendCapability.ORGANIZE_IMPORTS,
    )

    override fun createAnalyzer(ctx: CompilationContext): SourceAnalyzer = KotlinSourceAnalyzer(ctx)

    companion object {
        val LANGUAGE_ID = LanguageId("kotlin")
    }
}
