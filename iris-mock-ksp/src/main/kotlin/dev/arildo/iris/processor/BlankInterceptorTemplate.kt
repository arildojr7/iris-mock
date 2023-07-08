package dev.arildo.iris.processor

internal const val blankInterceptorTemplate = """
package dev.arildo.iris.mock

import okhttp3.Interceptor
import okhttp3.Response

class IrisWrapperInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}

"""
