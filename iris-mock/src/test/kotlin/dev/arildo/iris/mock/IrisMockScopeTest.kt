package dev.arildo.iris.mock

import dev.arildo.iris.mock.util.HttpCode
import dev.arildo.iris.mock.util.IRIS_HEADER_IGNORE
import dev.arildo.iris.mock.util.IRIS_HEADER_KEY
import dev.arildo.iris.mock.util.Method
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IrisMockScopeTest {
    private val chainMock: Interceptor.Chain = mockk()

    @BeforeEach
    fun setUp() {
        val blankGetRequest = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.GET.name, null)
            .build()
        every { chainMock.request() } returns blankGetRequest
    }

    @Test
    fun `when create custom response, then change isCustomResponse to true`() {
        val irisMock = IrisMockScope(chainMock)
        assertFalse(irisMock.useCustomResponse)

        irisMock.createCustomResponse(HttpCode.OK)
        assertTrue(irisMock.useCustomResponse)
    }

    @Test
    fun `when intercept a call, then return the custom response`() {
        val response = IrisMockScope(chainMock).apply {
            createCustomResponse(HttpCode.OK)
        }.build()

        assertNull(response.header(IRIS_HEADER_KEY))
    }

    @Test
    fun `when create custom response, then add body and code correctly`() {
        val expectedBody = "expectedBody"

        val response = IrisMockScope(chainMock).apply {
            createCustomResponse(HttpCode.BAD_REQUEST, expectedBody)
        }.build()

        assertEquals(expectedBody, response.body()?.string())
        assertEquals(HttpCode.BAD_REQUEST.code, response.code())
    }

    @Test
    fun `when not intercept the call, then return a blank response`() {
        val response = IrisMockScope(chainMock).build()

        assertEquals(IRIS_HEADER_IGNORE, response.header(IRIS_HEADER_KEY))
    }

    @Test
    fun `when proceed original call, then return original response`() {
        val expectedResponse = mockk<Response>()
        val request = chainMock.request()
        every { chainMock.proceed(request) } returns expectedResponse

        val irisMockScope = IrisMockScope(chainMock)
        assertFalse(irisMockScope.useOriginalResponse)

        irisMockScope.proceedOriginalResponse()
        assertTrue(irisMockScope.useOriginalResponse)

        assertEquals(expectedResponse, irisMockScope.build())
    }
}
