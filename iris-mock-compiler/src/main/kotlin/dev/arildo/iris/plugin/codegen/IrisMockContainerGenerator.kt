package dev.arildo.iris.plugin.codegen

import dev.arildo.iris.plugin.utils.IRIS_MOCK_CONTAINER
import dev.arildo.iris.plugin.utils.IRIS_MOCK_PACKAGE
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.packageFqName
import java.io.File

internal class IrisMockContainerGenerator : CodeGenerator {
    override fun generateCode(codeGenDir: File, annotatedClasses: List<IrClass>) {
        generateIrisMockContainer(
            codeGenDir = codeGenDir,
            packageName = IRIS_MOCK_PACKAGE,
            fileName = IRIS_MOCK_CONTAINER,
            content = irisMockContainer(annotatedClasses.map { it.qualifiedName })
        )
        println("@@@ generateCode")

    }

    private val IrClass.qualifiedName: String get() = packageFqName?.asString() + "." + name.asString()
}
