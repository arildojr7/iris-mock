@file:JvmMultifileClass
@file:Suppress("unused", "UnusedReceiverParameter")

package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMock
import dev.arildo.iris.mock.IrisMockScope
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Starts a new `IrisMockScope`, where all DSL functions will be available.
 * @return the result [Response] to be consumed by `OkHttp` interceptor
 */

fun irisMock(
    chain: Interceptor.Chain,
    block: IrisMockScope.() -> Unit
): Response = IrisMockScope(chain).also(block).build()


/**
 * Enable logs for all calls.
 */
fun IrisMockScope.enableLogs() {
    IrisMock.enableLogs = true
}

/**
 * Disable logs for all calls.
 */
fun IrisMockScope.disableLogs() {
    IrisMock.enableLogs = false
}
