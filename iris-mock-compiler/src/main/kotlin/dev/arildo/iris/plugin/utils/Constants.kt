package dev.arildo.iris.plugin.utils

import org.jetbrains.kotlin.config.CompilerConfigurationKey

internal const val srcGenDirName = "src-gen-dir"
internal val srcGenDirKey = CompilerConfigurationKey.create<String>("$srcGenDirName")

internal const val IRIS_MOCK_ANNOTATION = "IrisMockInterceptor"
internal const val IRIS_MOCK_CONTAINER = "IrisMockContainer"
internal const val IRIS_MOCK_PACKAGE = "dev.arildo.iris.mock"
