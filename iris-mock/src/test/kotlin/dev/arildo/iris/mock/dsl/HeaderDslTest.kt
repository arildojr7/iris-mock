package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMock
import dev.arildo.iris.mock.callmodifier.AddHeaderRequestModifier
import dev.arildo.iris.mock.callmodifier.AddHeaderResponseModifier
import dev.arildo.iris.mock.callmodifier.RemoveHeaderRequestModifier
import dev.arildo.iris.mock.callmodifier.RemoveHeaderResponseModifier
import dev.arildo.iris.mock.util.Method
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
    }

    @AfterEach
    fun tearDown() {
        IrisMock.callModifiers.clear()
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
            IrisMock.callModifiers.elementAt(0)
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
            IrisMock.callModifiers.elementAt(0)
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
            IrisMock.callModifiers.elementAt(0)
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
            IrisMock.callModifiers.elementAt(0)
        )
    }
}
