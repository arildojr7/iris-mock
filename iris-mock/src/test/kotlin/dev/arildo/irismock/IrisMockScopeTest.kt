package dev.arildo.irismock

import dev.arildo.irismock.dsl.irisMock
import dev.arildo.irismock.dsl.mockResponse
import dev.arildo.irismock.dsl.onGet
import dev.arildo.irismock.util.Method
import dev.arildo.irismock.util.isUsingIrisMockPlugin
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IrisMockScopeTest {
    private val chainMock: Interceptor.Chain = mockk()

    @BeforeEach
    fun setUp() {
        mockkStatic(::isUsingIrisMockPlugin)

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
    fun `when plugin is applied, then irisMock should return a blank response`() {
        every { ::isUsingIrisMockPlugin.invoke() } returns true
        val unexpectedValue = "irisMock"

        val response = irisMock(chainMock) {
            onGet("user/me") mockResponse mapOf("key" to unexpectedValue)
        }

        assertFalse(response.peekBody(Long.MAX_VALUE).string().contains(unexpectedValue))
    }

    @Test
    fun `when plugin is not applied, then irisMock should return the processed response`() {
        every { ::isUsingIrisMockPlugin.invoke() } returns false
        val expectedValue = "irisMock"

        val response = irisMock(chainMock) {
            onGet("user/me") mockResponse mapOf("key" to expectedValue)
        }

        assertTrue(response.peekBody(Long.MAX_VALUE).string().contains(expectedValue))
    }
}
