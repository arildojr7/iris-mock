package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.ModifierProcessor
import dev.arildo.iris.mock.callmodifier.CustomResponseBodyModifier
import dev.arildo.iris.mock.util.APPLICATION_JSON
import dev.arildo.iris.mock.util.Method
import dev.arildo.iris.mock.util.createBlankResponse
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RequestMethodDslTest {
    private val chainMock: Interceptor.Chain = mockk()
    private val expectedResponse = "expectedResponse"

    @BeforeEach
    fun setUp() {
        val blankGetRequest = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.GET.name, null)
            .build()
        every { chainMock.request() } returns blankGetRequest
        every { chainMock.proceed(any()) } returns createBlankResponse(chainMock)
        every { chainMock.connection()?.protocol() } returns null
    }

    @AfterEach
    fun tearDown() {
        ModifierProcessor.callModifiers.clear()
    }

    @Test
    fun `when url and method match, then add it to the call modifiers`() {
        irisMock(chainMock) {
            onGet("user/me") mockResponse expectedResponse
        }

        assertEquals(
            CustomResponseBodyModifier(chainMock.hashCode(), expectedResponse),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    @Test
    fun `when url matches and method does not, then do not add it to the call modifiers`() {
        irisMock(chainMock) {
            onPost("user/me") mockResponse "anyResponse"
        }

        assertTrue(ModifierProcessor.callModifiers.isEmpty())
    }

    @Test
    fun `when url and method do not match, then do not add it to the call modifiers`() {
        irisMock(chainMock) {
            onPost("any") mockResponse "anyResponse"
        }

        assertTrue(ModifierProcessor.callModifiers.isEmpty())
    }

    @Test
    fun `when method matches and url does not, then do not add it to the call modifiers`() {
        irisMock(chainMock) {
            onGet("any") mockResponse "anyResponse"
        }

        assertTrue(ModifierProcessor.callModifiers.isEmpty())
    }

    @Test
    fun `when match onGet conditions, then add it to the call modifiers`() {
        irisMock(chainMock) {
            onGet("user/me") mockResponse expectedResponse
            onPut("user/me") mockResponse "any"
            onPost("user/me") mockResponse "any"
            onPatch("user/me") mockResponse "any"
            onDelete("user/me") mockResponse "any"
        }

        assertEquals(
            CustomResponseBodyModifier(chainMock.hashCode(), expectedResponse),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    @Test
    fun `when match onPut conditions, then add it to the call modifiers`() {
        val request = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.PUT.name, RequestBody.create(MediaType.get(APPLICATION_JSON), ""))
            .build()

        every { chainMock.request() } returns request

        irisMock(chainMock) {
            onGet("user/me") mockResponse "any"
            onPut("user/me") mockResponse expectedResponse
            onPost("user/me") mockResponse "any"
            onPatch("user/me") mockResponse "any"
            onDelete("user/me") mockResponse "any"
        }

        assertEquals(
            CustomResponseBodyModifier(chainMock.hashCode(), expectedResponse),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    @Test
    fun `when match onPost conditions, then add it to the call modifiers`() {
        val request = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.POST.name, RequestBody.create(MediaType.get(APPLICATION_JSON), ""))
            .build()

        every { chainMock.request() } returns request

        irisMock(chainMock) {
            onGet("user/me") mockResponse "any"
            onPut("user/me") mockResponse "any"
            onPost("user/me") mockResponse expectedResponse
            onPatch("user/me") mockResponse "any"
            onDelete("user/me") mockResponse "any"
        }

        assertEquals(
            CustomResponseBodyModifier(chainMock.hashCode(), expectedResponse),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    @Test
    fun `when match onPatch conditions, then add it to the call modifiers`() {
        val request = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.PATCH.name, RequestBody.create(MediaType.get(APPLICATION_JSON), ""))
            .build()

        every { chainMock.request() } returns request

        irisMock(chainMock) {
            onGet("user/me") mockResponse "any"
            onPut("user/me") mockResponse "any"
            onPost("user/me") mockResponse "any"
            onPatch("user/me") mockResponse expectedResponse
            onDelete("user/me") mockResponse "any"
        }

        assertEquals(
            CustomResponseBodyModifier(chainMock.hashCode(), expectedResponse),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    @Test
    fun `when match onDelete conditions, then add it to the call modifiers`() {
        val request = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.DELETE.name, RequestBody.create(MediaType.get(APPLICATION_JSON), ""))
            .build()

        every { chainMock.request() } returns request

        irisMock(chainMock) {
            onGet("user/me") mockResponse "any"
            onPut("user/me") mockResponse "any"
            onPost("user/me") mockResponse "any"
            onPatch("user/me") mockResponse "any"
            onDelete("user/me") mockResponse expectedResponse
        }

        assertEquals(
            CustomResponseBodyModifier(chainMock.hashCode(), expectedResponse),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    @Test
    fun `when use custom response extension, then return correct json response`() {
        val expectedJson = "{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value4\"}"

        irisMock(chainMock) {
            onGet("user/me").mockResponse(
                mapOf(
                    "key1" to "value1",
                    "key2" to "value2",
                    "key3" to "value4"
                )
            )
        }

        assertEquals(
            CustomResponseBodyModifier(chainMock.hashCode(), expectedJson),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }
}
