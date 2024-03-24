package dev.arildo.iris.plugin

import com.google.auto.service.AutoService
import dev.arildo.iris.plugin.codegen.CodeGenerationExtension
import dev.arildo.iris.plugin.codegen.CodeGenerator
import dev.arildo.iris.plugin.utils.srcGenDirKey
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.extensions.AnalysisHandlerExtension
import java.io.File
import java.util.ServiceLoader

@AutoService(CompilerPluginRegistrar::class)
class IrisMockComponentRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true // TODO review it

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        AnalysisHandlerExtension.registerExtension(
            CodeGenerationExtension(
                codeGenDir = File(configuration.getNotNull(srcGenDirKey)),
                codeGenerator = loadCodeGenerator()
            )
        )
    }

    private fun loadCodeGenerator() = ServiceLoader.load(
        CodeGenerator::class.java,
        CodeGenerator::class.java.classLoader
    ).firstOrNull()
}
