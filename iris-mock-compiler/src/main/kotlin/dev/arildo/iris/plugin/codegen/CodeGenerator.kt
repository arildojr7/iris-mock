@file:Suppress("UnusedReceiverParameter")

package dev.arildo.iris.plugin.codegen

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

interface CodeGenerator {
    fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    )

    fun CodeGenerator.generateIrisMockContainer(
        codeGenDir: File,
        packageName: String,
        fileName: String,
        content: String
    ): GeneratedFile {
        val directory = File(codeGenDir, packageName.replace('.', File.separatorChar))
        val file = File(directory, "$fileName.kt")
        check(file.parentFile.exists() || file.parentFile.mkdirs()) {
            "Could not generate package directory: ${file.parentFile}"
        }
        file.writeText(content)

        return GeneratedFile(file, content)
    }
}
