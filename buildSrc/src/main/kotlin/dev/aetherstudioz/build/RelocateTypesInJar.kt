package dev.aetherstudioz.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Gradle task to relocate type references within a JAR file.
 *
 * This task reads a JAR file, remaps specified class names/package prefixes,
 * and writes a new JAR file with the relocated types.
 */
abstract class RelocateTypesInJar : DefaultTask() {

    @get:InputFile
    abstract val inputJar: RegularFileProperty

    @get:OutputFile
    abstract val outputJar: RegularFileProperty

    @get:Input
    abstract val renames: MapProperty<String, String>

    @TaskAction
    fun relocate() {
        val input = inputJar.get().asFile
        val output = outputJar.get().asFile
        val renamesMap = renames.get()

        // Ensure output directory exists
        output.parentFile.mkdirs()

        // Temporary file to avoid corruption if process fails
        val tempOutput = File(output.parentFile, output.name + ".tmp")

        try {
            JarFile(input).use { inputJar ->
                JarOutputStream(tempOutput.outputStream().buffered()).use { outputJar ->
                    inputJar.entries().asSequence().forEach { entry ->
                        val newEntry = ZipEntry(entry)
                        val entryName = entry.name

                        // Process class files
                        if (entryName.endsWith(".class")) {
                            val bytes = inputJar.getInputStream(entry).readAllBytes()
                            val relocatedBytes = relocateClassFile(bytes, renamesMap)
                            outputJar.putNextEntry(newEntry)
                            outputJar.write(relocatedBytes)
                            outputJar.closeEntry()
                        } else {
                            // Copy non-class files as-is
                            outputJar.putNextEntry(newEntry)
                            inputJar.getInputStream(entry).copyTo(outputJar)
                            outputJar.closeEntry()
                        }
                    }
                }
            }

            // Replace original output with temp file
            Files.move(tempOutput.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING)
            logger.lifecycle("RelocateTypesInJar: relocated ${renamesMap.size} type(s) in ${input.name} → ${output.name}")
        } finally {
            // Clean up temp file if it still exists
            if (tempOutput.exists()) {
                tempOutput.delete()
            }
        }
    }

    /**
     * Relocate class references within a class file bytecode.
     *
     * This performs a simple string replacement of class name references in the
     * constant pool and internal type references.
     */
    private fun relocateClassFile(bytes: ByteArray, renames: Map<String, String>): ByteArray {
        var result = bytes
        
        // Replace all renames in the bytecode
        for ((oldName, newName) in renames) {
            result = relocateInBytecode(result, oldName, newName)
        }
        
        return result
    }

    /**
     * Performs simple string-based relocation in bytecode.
     *
     * This is a basic implementation that handles the most common cases.
     * For full relocation, a bytecode library like ASM would be ideal.
     */
    private fun relocateInBytecode(bytes: ByteArray, oldName: String, newName: String): ByteArray {
        val oldUtf = oldName.toByteArray(Charsets.UTF_8)
        val newUtf = newName.toByteArray(Charsets.UTF_8)
        
        // Simple case: if lengths differ, this is a complex relocation
        // For now, we'll handle same-length replacements
        if (oldUtf.size != newUtf.size) {
            logger.warn("RelocateTypesInJar: skipping '$oldName' → '$newName' (different lengths)")
            return bytes
        }
        
        val result = bytes.copyOf()
        var offset = 0
        while (offset < result.size - oldUtf.size) {
            if (matches(result, offset, oldUtf)) {
                System.arraycopy(newUtf, 0, result, offset, newUtf.size)
                offset += newUtf.size
            } else {
                offset++
            }
        }
        
        return result
    }

    private fun matches(data: ByteArray, offset: Int, pattern: ByteArray): Boolean {
        if (offset + pattern.size > data.size) return false
        for (i in pattern.indices) {
            if (data[offset + i] != pattern[i]) return false
        }
        return true
    }
}
