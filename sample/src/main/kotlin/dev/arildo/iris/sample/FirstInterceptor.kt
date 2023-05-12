package dev.arildo.iris.sample

import dev.arildo.iris.mock.annotation.IrisMockInterceptor
import okhttp3.Interceptor
import okhttp3.Response

@IrisMockInterceptor
class FirstInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

}
