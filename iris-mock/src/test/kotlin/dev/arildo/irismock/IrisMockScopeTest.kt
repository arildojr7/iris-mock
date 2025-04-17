package dev.arildo.irismock

import dev.arildo.irismock.dsl.irisMock
import dev.arildo.irismock.dsl.mockResponse
import dev.arildo.irismock.dsl.onGet
import dev.arildo.irismock.util.Method
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.jupiter.api.AfterEach
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
        every { chainMock.connection()?.protocol() } returns null
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        ModifierProcessor.callModifiers.clear()
    }

    @Test
    fun `when intercepting a call, then irisMock should return the processed response`() {
        val expectedValue = "irisMock"

        val response = irisMock(chainMock) {
            onGet("user/me") mockResponse mapOf("key" to expectedValue)
        }

        assertTrue(response.peekBody(Long.MAX_VALUE).string().contains(expectedValue))
    }
}
