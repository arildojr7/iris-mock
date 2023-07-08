package dev.arildo.iris.sample

import dev.arildo.iris.mock.annotation.IrisMockInterceptor
import dev.arildo.iris.mock.dsl.enableLogs
import dev.arildo.iris.mock.dsl.irisMockScope
import dev.arildo.iris.mock.dsl.mockResponse
import dev.arildo.iris.mock.dsl.onGet
import okhttp3.Interceptor

@IrisMockInterceptor
class FirstInterceptor : Interceptor {

    init {
    }

    override fun intercept(chain: Interceptor.Chain) = irisMockScope(chain) {
        enableLogs()

        // just to switch between mock and real response on app sample
        if (!AppState.shouldIntercept) return@irisMockScope
        onGet(endsWith = "/public/characters").mockResponse("{\"data\" : \"Intercepted!\"}")
    }
}
