package dev.arildo.irismock.sample

import dev.arildo.irismock.dsl.addHeaderRequest
import dev.arildo.irismock.dsl.addHeaderResponse
import dev.arildo.irismock.dsl.irisMock
import dev.arildo.irismock.dsl.mockResponse
import dev.arildo.irismock.dsl.onGet
import dev.arildo.irismock.sample.MockHandler.enableMocks
import okhttp3.Interceptor

class SampleInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = irisMock(chain) {
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
