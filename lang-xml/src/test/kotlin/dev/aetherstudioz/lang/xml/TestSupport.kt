package dev.aetherstudioz.lang.xml

import dev.aetherstudioz.lang.incremental.DocumentSnapshot
import dev.aetherstudioz.testkit.InMemoryVirtualFile
import dev.aetherstudioz.testkit.TestDocument
import dev.aetherstudioz.vfs.VirtualFile

/** A bare [VirtualFile] backed only by a path — enough for parser/completion tests. */
typealias FakeFile = InMemoryVirtualFile

/** A [DocumentSnapshot] whose file defaults to a layout path for one-liner parse/completion tests. */
class TestDoc(
    text: CharSequence,
    file: VirtualFile = FakeFile("res/layout/test.xml"),
    version: Long = 1,
) : DocumentSnapshot by TestDocument(text, file, version)

fun parse(xml: String): XmlParsedFile =
    XmlIncrementalParser().parseFull(TestDoc(xml)) as XmlParsedFile
