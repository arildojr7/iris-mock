@file:JvmMultifileClass
@file:Suppress("unused", "UnusedReceiverParameter")

package dev.arildo.irismock.dsl

import dev.arildo.irismock.IrisMock
import dev.arildo.irismock.IrisMockSetupScope
import okhttp3.Interceptor
import kotlin.reflect.KFunction0

/**
 * Entry point for IrisMock setup.
 * This is usually called on `Application.onCreate()`
 */
fun startIrisMock(block: IrisMockSetupScope.() -> Unit) {
    IrisMockSetupScope().also(block)
}

/**
 * Enable logs for all calls.
 */
fun IrisMockSetupScope.enableLogs() {
    IrisMock.enableLogs = true
}

/**
 * Add the interceptors
 * ```
 * // sample
 * startIrisMock {
 *     interceptors(
 *         ::SampleInterceptor1,
 *         ::SampleInterceptor2,
 *     )
 * }
 * ```
 */
fun IrisMockSetupScope.interceptors(vararg interceptors: () -> Interceptor) {
    IrisMock.interceptors.addAll(interceptors)
}
