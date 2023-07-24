package dev.arildo.iris.plugin

import com.google.auto.service.AutoService
import dev.arildo.iris.plugin.codegen.CodeGenerationExtension
import dev.arildo.iris.plugin.codegen.CodeGenerator
import dev.arildo.iris.plugin.codegen.IrisMockModuleDescriptorImpl
import dev.arildo.iris.plugin.util.srcGenDirKey
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.extensions.AnalysisHandlerExtension
import java.io.File
import java.util.ServiceLoader

@AutoService(CompilerPluginRegistrar::class)
class IrisMockComponentRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = false // TODO review it

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val logger =
            configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        AnalysisHandlerExtension.registerExtension(
            CodeGenerationExtension(
                codeGenDir = File(configuration.getNotNull(srcGenDirKey)),
                codeGenerators = loadCodeGenerators(),
                moduleDescriptorFactory = IrisMockModuleDescriptorImpl.Factory()
            )
        )
    }

    private fun loadCodeGenerators(): List<CodeGenerator> {
        return ServiceLoader.load(CodeGenerator::class.java, CodeGenerator::class.java.classLoader)
            .toList()
    }
}
