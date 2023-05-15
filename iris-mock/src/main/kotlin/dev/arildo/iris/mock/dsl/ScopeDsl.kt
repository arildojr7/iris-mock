@file:JvmMultifileClass
@file:Suppress("unused")

package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMockScope
import dev.arildo.iris.mock.util.readRequestBody
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Starts a new `IrisMockScope`, where all DSL functions will be available.
 * @return the result [Response] to be consumed by `OkHttp` interceptor
 */
fun irisMockScope(
    chain: Interceptor.Chain,
    block: IrisMockScope.() -> Unit
): Response = IrisMockScope(chain).also(block).build()

/**
 * Verify if the [value] is included on request body
 * @return true if it's included
 */
fun IrisMockScope.requestBodyContains(value: String, ignoreCase: Boolean = true): Boolean =
    readRequestBody(request).contains(value, ignoreCase)

/**
 * Enable logs for request calls. There is no difference if this function
 * is used at the beginning or at the end of the interceptor.
 */
fun IrisMockScope.logRequests() {
    // TODO improve
    enableLog = true
}
