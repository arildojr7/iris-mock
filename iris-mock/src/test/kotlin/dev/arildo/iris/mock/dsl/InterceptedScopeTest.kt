package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMockScope
import dev.arildo.iris.mock.util.HttpCode
import dev.arildo.iris.mock.util.Method
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InterceptedScopeTest {
    private val chainMock: Interceptor.Chain = mockk()
    private val expectedHeader = "headerKey" to "headerValue"

    @BeforeEach
    fun setUp() {
        val blankGetRequest = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.GET.name, null)
            .build()
        every { chainMock.request() } returns blankGetRequest
    }

    @Test
    fun `when add header request, then add it to the request`() {
        val response = irisMockScope(chainMock) {
            onGet("user/me") then {
                addHeaderRequest(expectedHeader)
            }
        }

        assertEquals(expectedHeader.second, response.request().header(expectedHeader.first))
    }

    @Test
    fun `when remove header request, then remove it from request`() {
        val response = irisMockScope(chainMock) {
            onGet("user/me") then {
                addHeaderRequest(expectedHeader)
                removeHeaderRequest(expectedHeader.first)
            }
        }

        assertNull(response.request().header(expectedHeader.first))
    }

    @Test
    fun `when add header response, then add it to the response`() {
        val response = IrisMockScope(chainMock).apply {
            onGet("user/me") then {
                addHeaderResponse(expectedHeader)
                createCustomResponse(HttpCode.OK)
            }
        }.build()

        assertEquals(expectedHeader.second, response.header(expectedHeader.first))
    }

    @Test
    fun `when remove header response, then remove it from response`() {
        val response = IrisMockScope(chainMock).apply {
            onGet("user/me") then {
                addHeaderResponse(expectedHeader)
                removeHeaderResponse(expectedHeader.first)
                createCustomResponse(HttpCode.OK)
            }
        }.build()

        assertNull(response.header(expectedHeader.first))
    }
}
