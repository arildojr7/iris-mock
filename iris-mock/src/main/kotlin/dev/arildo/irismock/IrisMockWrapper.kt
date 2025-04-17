@file:Suppress("UNUSED")

package dev.arildo.irismock

import okhttp3.Interceptor
import okhttp3.Response

/*
 * When using IrisMock plugin to inject interceptors, this class is called
 * to iterate over them, granting all modifiers are processed.
 *
 * When using only the IrisMock runtime dependency, this class is not called at all,
 * since the interceptor is being added manually to the OkHttp builder.
 */
internal class IrisMockWrapper : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        IrisMock.interceptors.forEach { it().intercept(chain) }

        return ModifierProcessor.process(chain)
    }
}
