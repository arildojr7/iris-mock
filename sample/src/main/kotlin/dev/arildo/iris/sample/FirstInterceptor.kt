package dev.arildo.iris.sample

import dev.arildo.iris.mock.annotation.IrisMockInterceptor
import dev.arildo.iris.mock.dsl.irisMockScope
import dev.arildo.iris.mock.dsl.mockResponse
import dev.arildo.iris.mock.dsl.onPost
import dev.arildo.iris.mock.dsl.startLogger
import dev.arildo.iris.mock.dsl.then
import kotlinx.coroutines.delay
import okhttp3.Interceptor

@IrisMockInterceptor
class FirstInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = irisMockScope(chain) {
        onPost(endsWith = "you") then {
            delay(2000)
            mockResponse("{\"data\" : \"Intercepted!\"}")
        }
        startLogger()
    }

}
