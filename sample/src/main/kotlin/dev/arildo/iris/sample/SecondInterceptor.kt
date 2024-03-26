package dev.arildo.iris.sample

import dev.arildo.iris.mock.annotation.IrisMockInterceptor
import dev.arildo.iris.mock.dsl.enableLogs
import dev.arildo.iris.mock.dsl.irisMockScope
import dev.arildo.iris.mock.dsl.mockResponse
import dev.arildo.iris.mock.dsl.onGet
import okhttp3.Interceptor

@IrisMockInterceptor
class SecondInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = irisMockScope(chain) {
        enableLogs()
        onGet(endsWith = "/public/characters") mockResponse "{\"data\" : \"aham!\"}"
    }
}
