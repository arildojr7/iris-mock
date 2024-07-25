package dev.arildo.iris.sample

import dev.arildo.iris.mock.annotation.IrisMockInterceptor
import dev.arildo.iris.mock.dsl.enableLogs
import dev.arildo.iris.mock.dsl.irisMock
import dev.arildo.iris.mock.dsl.mockResponse
import dev.arildo.iris.mock.dsl.onGet
import okhttp3.Interceptor

@IrisMockInterceptor
class SampleInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = irisMock(chain) {
        enableLogs()

        // try commenting this line
        onGet(endsWith = "/public/characters") mockResponse "{\"data\" : \"Intercepted!\"}"
    }
}
