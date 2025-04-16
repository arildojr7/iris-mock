package dev.arildo.irismock.dsl

import dev.arildo.irismock.ModifierProcessor
import dev.arildo.irismock.callmodifier.CustomResponseBodyModifier
import dev.arildo.irismock.util.Method
import dev.arildo.irismock.util.createBlankResponse
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MockDslTest {
    private val chainMock: Interceptor.Chain = mockk()

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
    fun `when use mockResponse map, then return correct json response`() {
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

    @Test
    fun `when use mockResponse string, then return correct json response`() {
        val expectedJson = "{\"id\":2,\"name\":\"Arildo\"}"

        irisMock(chainMock) {
            onGet("user/me").mockResponse(expectedJson)
        }

        assertEquals(
            CustomResponseBodyModifier(chainMock.hashCode(), expectedJson),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    @Test
    fun `when use mockResponse model class, then return correct json response`() {
        val expectedJson = "{\"id\":2,\"name\":\"Arildo\"}"

        irisMock(chainMock) {
            onGet("user/me").mockResponse(ModelTest(id = 2, name = "Arildo"))
        }

        assertEquals(
            CustomResponseBodyModifier(chainMock.hashCode(), expectedJson),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    private data class ModelTest(val id: Int, val name: String)
}
