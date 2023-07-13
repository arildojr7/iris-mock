package dev.arildo.iris.plugin

import com.google.auto.service.AutoService
import dev.arildo.iris.plugin.util.CodeGenerator
import dev.arildo.iris.plugin.util.GeneratedFile
import dev.arildo.iris.plugin.util.createGeneratedFile
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@AutoService(CodeGenerator::class)
class WrapperCodeGenerator : CodeGenerator {
    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFile> {
        val annotatedClasses = projectFiles.filter {
            it.annotations.any { it.name == "dev.arildo.iris.mock.annotation.IrisMockInterceptor" }
        }

        annotatedClasses.forEach {
            println("@@@ ${it.name}")
        }

        @Language("kotlin")
        val generatedClass = wrapperInterceptorFactory(annotatedClasses)


        createGeneratedFile(
            codeGenDir = codeGenDir,
            packageName = PACKAGE,
            fileName = "MeuExemplo",
            content = generatedClass
        )
        return emptyList()
    }

}