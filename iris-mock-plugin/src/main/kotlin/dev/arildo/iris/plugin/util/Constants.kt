package dev.arildo.iris.plugin.util

import org.jetbrains.kotlin.config.CompilerConfigurationKey

internal const val LIST_DESCRIPTOR = "Ljava/util/List;"
internal const val LIST_OWNER = "java/util/List"
internal const val OBJECT = "(Ljava/lang/Object;)Z"
internal const val CONSTRUCTOR = "()V"
internal const val OKHTTP_BUILDER_DESCRIPTOR = "okhttp3/OkHttpClient\$Builder"
internal const val OKHTTP_BUILDER = "okhttp3.OkHttpClient\$Builder"

internal const val INIT = "<init>"
internal const val ADD = "add"
internal const val BUILD = "build"
internal const val NETWORK_INTERCEPTORS = "networkInterceptors"

internal const val IRIS_MOCK_INTERCEPTOR = "IrisMockInterceptor"
internal const val IRIS_WRAPPER_INTERCEPTOR = "dev/arildo/iris/mock/IrisMockWrapper"
internal const val IRIS_WRAPPER_NAME = "IrisMockWrapper"
internal const val IRIS_WRAPPER_PACKAGE = "dev.arildo.iris.mock"

internal const val srcGenDirName = "src-gen-dir"
internal val srcGenDirKey = CompilerConfigurationKey.create<String>("$srcGenDirName")
