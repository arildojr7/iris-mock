package dev.arildo.iris.plugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

class SimpleIrGenerationExtension: IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val transformers = listOf(SimpleIrBodyGenerator(pluginContext))
        for (transformer in transformers) {
            moduleFragment.acceptChildrenVoid(transformer)
        }
    }
}