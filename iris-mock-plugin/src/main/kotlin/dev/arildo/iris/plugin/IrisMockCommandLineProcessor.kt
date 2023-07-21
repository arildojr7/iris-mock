package dev.arildo.iris.plugin

import com.google.auto.service.AutoService
import dev.arildo.iris.plugin.BuildConfig.PLUGIN_ID
import dev.arildo.iris.plugin.util.srcGenDirKey
import dev.arildo.iris.plugin.util.srcGenDirName
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(CommandLineProcessor::class)
class IrisMockCommandLineProcessor : CommandLineProcessor {

    override val pluginId: String = PLUGIN_ID

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = srcGenDirName,
            valueDescription = "<file-path>",
            description = "Path to directory in which Iris Mock code should be generated",
            required = false,
            allowMultipleOccurrences = false
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        when (option.optionName) {
            srcGenDirName -> configuration.put(srcGenDirKey, value)
        }
    }
}
