package dev.arildo.iris.plugin.codegen

import dev.arildo.iris.plugin.utils.IRIS_MOCK_CONTAINER
import dev.arildo.iris.plugin.utils.IRIS_MOCK_PACKAGE

internal fun irisMockContainer(classes: List<String>) =
    """package $IRIS_MOCK_PACKAGE

import okhttp3.Interceptor

private object $IRIS_MOCK_CONTAINER {
    val interceptors = listOf<Interceptor>(
${classes.joinToString(",\n") { "        $it()" }}
    )
}
"""
