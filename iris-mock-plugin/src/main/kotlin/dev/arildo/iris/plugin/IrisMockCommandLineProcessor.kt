package dev.arildo.iris.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

internal const val ENABLE_ONLY_ON_DEBUG_VARIANT = "enableOnlyOnDebugVariant"
val ARG_ENABLE_ONLY_ON_DEBUG_VARIANT =
    CompilerConfigurationKey<Boolean>(ENABLE_ONLY_ON_DEBUG_VARIANT)

@AutoService(CommandLineProcessor::class)
class IrisMockCommandLineProcessor : CommandLineProcessor {

    override val pluginId: String = "dev.arildo.iris-mock-plugin"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = ENABLE_ONLY_ON_DEBUG_VARIANT,
            valueDescription = "<true|false>",
            description = "If true, Iris Mock injection and DSL will be available only on debug build variants",
            required = false,
        ),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        when (option.optionName) {
            ENABLE_ONLY_ON_DEBUG_VARIANT -> configuration.put(
                ARG_ENABLE_ONLY_ON_DEBUG_VARIANT,
                value.toBoolean()
            )
        }
    }
}