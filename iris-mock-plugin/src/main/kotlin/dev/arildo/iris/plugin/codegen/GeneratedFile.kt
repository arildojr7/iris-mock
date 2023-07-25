package dev.arildo.iris.plugin.codegen

import java.io.File

/**
 * Adapted from https://github.com/square/anvil
 */
data class GeneratedFile(
    val file: File,
    val content: String
)
