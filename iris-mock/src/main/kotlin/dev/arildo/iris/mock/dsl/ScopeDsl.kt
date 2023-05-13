@file:JvmMultifileClass
@file:Suppress("unused")

package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMockScope
import dev.arildo.iris.mock.util.readRequestBody
import okhttp3.Interceptor
import okhttp3.Response

fun irisMockScope(
    chain: Interceptor.Chain,
    block: IrisMockScope.() -> Unit
): Response = IrisMockScope(chain).also(block).build()

fun IrisMockScope.requestContains(value: String): Boolean = readRequestBody(request).contains(value)

fun IrisMockScope.startLogger() {
    // TODO improve
    enableLog = true
}
