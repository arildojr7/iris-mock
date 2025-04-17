package dev.arildo.irismock.dsl

import dev.arildo.irismock.ModifierProcessor
import dev.arildo.irismock.Utils.createBlankResponse
import dev.arildo.irismock.callmodifier.AddHeaderRequestModifier
import dev.arildo.irismock.callmodifier.AddHeaderResponseModifier
import dev.arildo.irismock.callmodifier.RemoveHeaderRequestModifier
import dev.arildo.irismock.callmodifier.RemoveHeaderResponseModifier
import dev.arildo.irismock.util.Method
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HeaderDslTest {
    private val chainMock: Interceptor.Chain = mockk()
    private val expectedHeaderKey = "headerKey"
    private val expectedHeaderValue = "headerValue"
    private val expectedHeader = expectedHeaderKey to expectedHeaderValue

    @BeforeEach
    fun setUp() {
        val blankGetRequest = Request.Builder()
            .url("https://www.test.com/user/me")
            .method(Method.GET.name, null)
            .build()
        every { chainMock.request() } returns blankGetRequest
        every { chainMock.proceed(any()) } returns createBlankResponse(chainMock)
    }

    @AfterEach
    fun tearDown() {
        ModifierProcessor.callModifiers.clear()
    }

    @Test
    fun `when add header request, then add it to the call modifiers`() {
        irisMock(chainMock) {
            onGet("user/me") {
                addHeaderRequest(expectedHeader)
            }
        }

        assertEquals(
            AddHeaderRequestModifier(chainMock.hashCode(), expectedHeader),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    @Test
    fun `when remove header request, then add it to the call modifiers`() {
        irisMock(chainMock) {
            onGet("user/me") {
                removeHeaderRequest(expectedHeaderKey)
            }
        }

        assertEquals(
            RemoveHeaderRequestModifier(chainMock.hashCode(), expectedHeaderKey),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }


    @Test
    fun `when add header response, then add it to the call modifiers`() {
        irisMock(chainMock) {
            onGet("user/me") {
                addHeaderResponse(expectedHeader)
            }
        }

        assertEquals(
            AddHeaderResponseModifier(chainMock.hashCode(), expectedHeader),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }

    @Test
    fun `when remove header response, then add it to the call modifiers`() {
        irisMock(chainMock) {
            onGet("user/me") {
                removeHeaderResponse(expectedHeaderKey)
            }
        }

        assertEquals(
            RemoveHeaderResponseModifier(chainMock.hashCode(), expectedHeaderKey),
            ModifierProcessor.callModifiers.elementAt(0)
        )
    }
}
