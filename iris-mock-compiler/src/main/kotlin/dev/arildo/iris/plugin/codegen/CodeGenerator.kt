package dev.arildo.iris.plugin.codegen

import org.jetbrains.kotlin.ir.declarations.IrClass
import java.io.File

interface CodeGenerator {
    fun generateCode(
        codeGenDir: File,
        annotatedClasses: List<IrClass>,
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
