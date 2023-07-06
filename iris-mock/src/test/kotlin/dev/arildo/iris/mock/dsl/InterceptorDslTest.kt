package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.util.HttpCode
import dev.arildo.iris.mock.util.MEDIA_TYPE
import dev.arildo.iris.mock.util.Method
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InterceptorDslTest {
    private val chainMock: Interceptor.Chain = mockk()
    private val expectedResponse = "expectedResponse"

    @BeforeEach
    fun setUp() {
        val blankGetRequest = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.GET.name, null)
            .build()
        every { chainMock.request() } returns blankGetRequest
    }

    @Test
    fun `when url and method match, then intercept call`() {
        val response = irisMockScope(chainMock) {
            onGet("user/me") mockResponse expectedResponse
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertEquals(expectedResponse, response.body()?.string())
    }

    @Test
    fun `when url matches and method does not, then do not intercept call`() {
        val response = irisMockScope(chainMock) {
            onPost("user/me") mockResponse "anyResponse"
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertNull(response.body()?.string())
    }

    @Test
    fun `when url and method do not match, then do not intercept call`() {
        val response = irisMockScope(chainMock) {
            onPost("any") mockResponse "anyResponse"
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertNull(response.body()?.string())
    }

    @Test
    fun `when method matches and url does not, then do not intercept call`() {
        val response = irisMockScope(chainMock) {
            onGet("any") mockResponse "anyResponse"
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertNull(response.body()?.string())
    }

    @Test
    fun `when match onGet conditions, then return the correct response`() {
        val response = irisMockScope(chainMock) {
            onGet("user/me") mockResponse expectedResponse
            onPut("user/me") mockResponse "any"
            onPost("user/me") mockResponse "any"
            onPatch("user/me") mockResponse "any"
            onDelete("user/me") mockResponse "any"
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertEquals(expectedResponse, response.body()?.string())
    }

    @Test
    fun `when match onPut conditions, then return the correct response`() {
        val request = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.PUT.name, RequestBody.create(MediaType.get(MEDIA_TYPE), ""))
            .build()

        every { chainMock.request() } returns request

        val response = irisMockScope(chainMock) {
            onGet("user/me") mockResponse "any"
            onPut("user/me") mockResponse expectedResponse
            onPost("user/me") mockResponse "any"
            onPatch("user/me") mockResponse "any"
            onDelete("user/me") mockResponse "any"
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertEquals(expectedResponse, response.body()?.string())
    }

    @Test
    fun `when match onPost conditions, then return the correct response`() {
        val request = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.POST.name, RequestBody.create(MediaType.get(MEDIA_TYPE), ""))
            .build()

        every { chainMock.request() } returns request

        val response = irisMockScope(chainMock) {
            onGet("user/me") mockResponse "any"
            onPut("user/me") mockResponse "any"
            onPost("user/me") mockResponse expectedResponse
            onPatch("user/me") mockResponse "any"
            onDelete("user/me") mockResponse "any"
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertEquals(expectedResponse, response.body()?.string())
    }

    @Test
    fun `when match onPatch conditions, then return the correct response`() {
        val request = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.PATCH.name, RequestBody.create(MediaType.get(MEDIA_TYPE), ""))
            .build()

        every { chainMock.request() } returns request

        val response = irisMockScope(chainMock) {
            onGet("user/me") mockResponse "any"
            onPut("user/me") mockResponse "any"
            onPost("user/me") mockResponse "any"
            onPatch("user/me") mockResponse expectedResponse
            onDelete("user/me") mockResponse "any"
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertEquals(expectedResponse, response.body()?.string())
    }

    @Test
    fun `when match onDelete conditions, then return the correct response`() {
        val request = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.DELETE.name, RequestBody.create(MediaType.get(MEDIA_TYPE), ""))
            .build()

        every { chainMock.request() } returns request

        val response = irisMockScope(chainMock) {
            onGet("user/me") mockResponse "any"
            onPut("user/me") mockResponse "any"
            onPost("user/me") mockResponse "any"
            onPatch("user/me") mockResponse "any"
            onDelete("user/me") mockResponse expectedResponse
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertEquals(expectedResponse, response.body()?.string())
    }

    @Test
    fun `when use custom response extension, then return correct json response`() {
        val expectedJson = "{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value4\"}"

        val response = irisMockScope(chainMock) {
            onGet("user/me").mockCustomResponse(
                mapOf(
                    "key1" to "value1",
                    "key2" to "value2",
                    "key3" to "value4"
                )
            )
        }

        assertEquals(HttpCode.OK.code, response.code())
        assertEquals(expectedJson, response.body()?.string())
    }
}
