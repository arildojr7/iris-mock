package dev.arildo.iris.plugin.util

import org.jetbrains.kotlin.name.FqName

fun String.fqn(): FqName = FqName("dev.arildo.iris.mock.annotation.$this")
