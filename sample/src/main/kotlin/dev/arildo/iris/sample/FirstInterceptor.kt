package dev.arildo.iris.sample

import okhttp3.Interceptor
import okhttp3.Response

class FirstInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

}
