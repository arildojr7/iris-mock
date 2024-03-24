package dev.arildo.iris.plugin.codegen

import java.io.File

data class GeneratedFile(
    val file: File,
    val content: String
)
