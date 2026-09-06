package dev.aetherstudioz.lang.kotlin.index

import dev.aetherstudioz.index.IndexExtension
import dev.aetherstudioz.index.IndexId
import dev.aetherstudioz.index.IndexInput
import dev.aetherstudioz.index.IndexOrigin
import dev.aetherstudioz.index.InputFilter
import dev.aetherstudioz.index.KeyDescriptor
import dev.aetherstudioz.index.MatchingMode
import dev.aetherstudioz.index.StringKeyDescriptor
import dev.aetherstudioz.lang.kotlin.symbols.BuiltinsReader
import dev.aetherstudioz.lang.resolve.Modifier

/**
 * `kotlin.builtinCallables` — the persistent, prefix-queryable index for Kotlin's TOP-LEVEL builtin
 * INTRINSICS: `arrayOf`, `intArrayOf`, `charArrayOf`, `booleanArrayOf`, `emptyArray`, … . These are declared
 * in the stdlib's `.kotlin_builtins` protobuf fragments as PACKAGE-level functions, NOT as `.class` files, so
 * [KotlinCallableIndex] (which scans `.class` `@Metadata` facades) never sees them — leaving them missing from
 * name completion and resolution.
 *
 * Same value type + key scheme as [KotlinCallableIndex] ([CallableShape]; `top:` / `ext:` / `name:` keys), so
 * the consumer ([dev.aetherstudioz.lang.kotlin.symbols.KotlinSymbolService]) unions the two indices with identical query
 * code. It stays SEPARATE because the input is a different unit (`.kotlin_builtins`) that only the raw-protobuf
 * [BuiltinsReader] can decode — this index therefore lives in the compiler-carrying module (like
 * [KotlinBuiltinsIndex]), unlike the compiler-free [KotlinCallableIndex].
 */
object KotlinBuiltinCallableIndex : IndexExtension<String, CallableShape> {
    override val id = IndexId("kotlin.builtinCallables")
    // Base 2 folds in CallableShapeExternalizer.FORMAT (the shared codec). Bumped 1→2 for the same reason as
    // kotlin.builtins: the shared CallableShape codec evolved (kotlin.callables reached v9) while this index was
    // left at 1, so an old segment would be read with the newer format and desync mid-value.
    override val version = 2 + CallableShapeExternalizer.FORMAT
    override val keyDescriptor: KeyDescriptor<String> = StringKeyDescriptor
    override val valueExternalizer = CallableShapeExternalizer
    override val matching = MatchingMode.PREFIX_ONLY // queried by prefix on a tagged key, like kotlin.callables
    override val inputFilter = InputFilter {
        (it.origin == IndexOrigin.SDK || it.origin == IndexOrigin.LIBRARY) &&
            it.unitName?.endsWith(".kotlin_builtins") == true
    }

    override fun index(input: IndexInput): Map<String, Collection<CallableShape>> {
        // The package fragment carries no explicit package name we can read cheaply across metadata versions;
        // derive it from the entry path (`kotlin/collections/collections.kotlin_builtins` -> `kotlin.collections`).
        val unit = input.unitName ?: return emptyMap()
        val pkg = unit.substringBeforeLast('/', "").replace('/', '.').ifEmpty { return emptyMap() }
        val bytes = runCatching { input.bytes() }.getOrNull() ?: return emptyMap()
        val callables = BuiltinsReader.callablesFrom(bytes, pkg)
        if (callables.isEmpty()) return emptyMap()
        val out = HashMap<String, MutableList<CallableShape>>()
        callables.forEach { s ->
            // Library/SDK visibility gate: never index a private/internal intrinsic (mirrors KotlinCallableIndex).
            if (Modifier.PRIVATE in s.modifiers || s.isInternal) return@forEach
            // Intrinsics have no facade `.class`, so declaringClassFqn stays null (facade = null).
            val recv = s.receiverTypeFqn
            if (recv == null) {
                out.getOrPut(KotlinCallableIndex.topKey(s.name)) { ArrayList() }.add(CallableShape.from(s, pkg, null))
            } else {
                val shape = CallableShape.from(s, pkg, null)
                out.getOrPut(KotlinCallableIndex.extKey(recv, s.name)) { ArrayList() }.add(shape)
                out.getOrPut(KotlinCallableIndex.nameKey(s.name)) { ArrayList() }.add(shape)
            }
        }
        return out
    }
}
