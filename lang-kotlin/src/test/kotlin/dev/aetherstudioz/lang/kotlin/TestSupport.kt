package dev.aetherstudioz.lang.kotlin

import dev.aetherstudioz.lang.dom.DomNode
import dev.aetherstudioz.lang.incremental.DocumentSnapshot
import dev.aetherstudioz.lang.kotlin.parse.KotlinIncrementalParser
import dev.aetherstudioz.lang.kotlin.parse.KotlinParsedFile
import dev.aetherstudioz.testkit.InMemoryVirtualFile
import dev.aetherstudioz.testkit.TestDocument
import dev.aetherstudioz.vfs.VirtualFile

/** A bare [VirtualFile] backed only by a path — enough for parser/completion tests. */
typealias FakeFile = InMemoryVirtualFile

/** A [DocumentSnapshot] whose file defaults to `src/Main.kt` for one-liner parse/completion tests. */
class TestDoc(
    text: CharSequence,
    file: VirtualFile = FakeFile("src/Main.kt"),
    version: Long = 1,
) : DocumentSnapshot by TestDocument(text, file, version)

fun parse(kotlin: String, path: String = "src/Main.kt"): KotlinParsedFile =
    KotlinIncrementalParser().parseFull(TestDoc(kotlin, FakeFile(path))) as KotlinParsedFile

/** Pre-order flatten of the neutral DOM, for assertions. */
fun DomNode.flatten(): List<DomNode> = buildList {
    fun walk(n: DomNode) { add(n); n.children.forEach(::walk) }
    walk(this@flatten)
}
