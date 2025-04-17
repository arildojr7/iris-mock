@file:JvmMultifileClass
@file:Suppress("unused", "UnusedReceiverParameter")

package dev.arildo.irismock.dsl

import dev.arildo.irismock.IrisMockScope
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
