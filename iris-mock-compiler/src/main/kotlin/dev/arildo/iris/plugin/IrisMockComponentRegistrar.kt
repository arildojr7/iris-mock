package dev.arildo.iris.plugin

import dev.arildo.iris.plugin.codegen.CodeGenerationExtension
import dev.arildo.iris.plugin.codegen.CodeGenerator
import dev.arildo.iris.plugin.fir.SimplePluginRegistrar
import dev.arildo.iris.plugin.ir.SimpleIrGenerationExtension
//import dev.arildo.iris.plugin.ir.GeneratedDeclarationsIrBodyFiller
import dev.arildo.iris.plugin.utils.srcGenDirKey
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.resolve.extensions.AnalysisHandlerExtension
import java.io.File
import java.util.ServiceLoader

class IrisMockComponentRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {

        IrGenerationExtension.registerExtension(
            IrisIrGenerationExtension(
                codeGenDir = File(configuration.getNotNull(srcGenDirKey)),
                codeGenerator = loadCodeGenerator()
            )
        )
//        FirExtensionRegistrarAdapter.registerExtension(SimplePluginRegistrar())
//        IrGenerationExtension.registerExtension(SimpleIrGenerationExtension())
    }

    private fun loadCodeGenerator() = ServiceLoader.load(
        CodeGenerator::class.java,
        CodeGenerator::class.java.classLoader
    ).firstOrNull()
}
