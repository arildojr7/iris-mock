package dev.arildo.iris.plugin

import com.google.auto.service.AutoService
import dev.arildo.iris.plugin.codegen.ClassReference
import dev.arildo.iris.plugin.codegen.CodeGenerator
import dev.arildo.iris.plugin.codegen.classAndInnerClassReferences
import dev.arildo.iris.plugin.codegen.generateIrisMockWrapper
import dev.arildo.iris.plugin.codegen.irisMockWrapperFactory
import dev.arildo.iris.plugin.util.IRIS_MOCK_INTERCEPTOR
import dev.arildo.iris.plugin.util.IRIS_WRAPPER_NAME
import dev.arildo.iris.plugin.util.IRIS_WRAPPER_PACKAGE
import dev.arildo.iris.plugin.util.fq
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@AutoService(CodeGenerator::class)
internal class WrapperCodeGenerator : CodeGenerator {

    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ) {
        val annotatedClasses = projectFiles
            .classAndInnerClassReferences(module)
            .filter { it.isAnnotatedWithIrisMock() }

        generateIrisMockWrapper(
            codeGenDir = codeGenDir,
            packageName = IRIS_WRAPPER_PACKAGE,
            fileName = IRIS_WRAPPER_NAME,
            content = irisMockWrapperFactory(annotatedClasses)
        )
    }
}

private fun ClassReference.Psi.isAnnotatedWithIrisMock() = annotations.any {
    it.annotation.fq(module)?.fqName?.asString()?.contains(IRIS_MOCK_INTERCEPTOR) ?: false
}
