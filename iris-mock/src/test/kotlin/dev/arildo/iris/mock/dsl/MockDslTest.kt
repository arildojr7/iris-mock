package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMock
import dev.arildo.iris.mock.callmodifier.CustomResponseBodyModifier
import dev.arildo.iris.mock.util.Method
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
        every { chainMock.connection()?.protocol() } returns null
    }

    @AfterEach
    fun tearDown() {
        IrisMock.callModifiers.clear()
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
            IrisMock.callModifiers.elementAt(0)
        )
    }
}
