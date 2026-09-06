package dev.aetherstudioz.lang.kotlin

import dev.aetherstudioz.lang.CompilationContext
import dev.aetherstudioz.lang.completion.CompletionRequest
import dev.aetherstudioz.lang.completion.complete
import dev.aetherstudioz.lang.completion.CompletionResult
import dev.aetherstudioz.lang.completion.CompletionTrigger
import dev.aetherstudioz.testkit.DiskVirtualFile
import dev.aetherstudioz.testkit.TestDocument
import dev.aetherstudioz.testkit.TestJars
import dev.aetherstudioz.testkit.caret
import dev.aetherstudioz.testkit.compilationContext
import dev.aetherstudioz.testkit.writeSource
import java.nio.file.Files
import java.nio.file.Path

/** A [dev.aetherstudioz.vfs.VirtualFile] backed by a real filesystem path — for source-root walking + classpath reads. */
typealias DiskFile = DiskVirtualFile

/** A [dev.aetherstudioz.lang.incremental.DocumentSnapshot] over an in-memory snippet. */
typealias SnippetDoc = TestDocument

/** The kotlin-stdlib jar on the test classpath (the one carrying `kotlin/Pair.class`). */
fun stdlibJarPath(): Path = TestJars.kotlinStdlib()

/** A minimal [CompilationContext]: a source dir + library jars (stdlib by default). */
fun fakeContext(srcDir: Path, libJars: List<Path> = listOf(stdlibJarPath())): CompilationContext =
    compilationContext(sourceRoots = listOf(srcDir), libraries = libJars)

/** Write [files] (name -> content) into a fresh temp source dir and return it. */
fun tempProject(files: Map<String, String>): Path {
    val dir = Files.createTempDirectory("lang-kotlin-test")
    files.forEach { (name, content) -> dir.writeSource(name, content, trim = false) }
    return dir
}

/** Run completion on [code] with the caret at the FIRST occurrence of the `|` marker (which is stripped). */
suspend fun KotlinSourceAnalyzer.completeAtCaret(srcDir: Path, fileName: String, code: String): CompletionResult {
    val (clean, offset) = caret(code)
    val doc = SnippetDoc(clean, DiskFile(srcDir.resolve(fileName)))
    return complete(CompletionRequest(doc, offset, CompletionTrigger.TypedChar('.')))
}
