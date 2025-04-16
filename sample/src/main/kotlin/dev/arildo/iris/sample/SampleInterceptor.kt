package dev.arildo.iris.sample

import dev.arildo.iris.mock.annotation.IrisMockInterceptor
import dev.arildo.iris.mock.dsl.addHeaderRequest
import dev.arildo.iris.mock.dsl.addHeaderResponse
import dev.arildo.iris.mock.dsl.enableLogs
import dev.arildo.iris.mock.dsl.irisMock
import dev.arildo.iris.mock.dsl.mockResponse
import dev.arildo.iris.mock.dsl.onGet
import dev.arildo.iris.sample.MockHandler.enableMocks
import okhttp3.Interceptor

@IrisMockInterceptor
class SampleInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = irisMock(chain) {
        enableLogs()

        if (!enableMocks) return@irisMock
        onGet(endsWith = "/public/characters") {
            addHeaderRequest("AddedOne" to "newValue")
            addHeaderResponse("AnotherOne" to "anotherValue")
            addHeaderRequest("NormalHeader" to "anotherValue")
            // try commenting this line
            mockResponse("{\"data\" : \"Intercepted!\"}")
        }
    }
}
