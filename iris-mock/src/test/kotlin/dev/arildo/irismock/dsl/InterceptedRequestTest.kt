package dev.arildo.irismock.dsl

import dev.arildo.irismock.IrisMockScope
import dev.arildo.irismock.interceptCall
import dev.arildo.irismock.util.Method
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InterceptedRequestTest1 {
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

    @Test
    fun `when endsWith and contains are empty, then throw exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            IrisMockScope(chainMock).apply {
                interceptCall(" ", " ", Method.GET)
            }
        }
    }

    @Test
    fun `when endsWith and contains are not empty, then throw exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            IrisMockScope(chainMock).apply {
                interceptCall("any", "any", Method.GET)
            }
        }
    }

    @Test
    fun `when endsWith matches url, then shouldIntercept must be true`() {
        IrisMockScope(chainMock).apply {
            assertTrue(interceptCall("user/me", "", Method.GET).shouldIntercept)
        }
    }

    @Test
    fun `when contains matches url, then shouldIntercept must be true`() {
        IrisMockScope(chainMock).apply {
            assertTrue(interceptCall("", "user/me", Method.GET).shouldIntercept)
        }
    }

    @Test
    fun `when endsWith not matches url, then shouldIntercept must be true`() {
        IrisMockScope(chainMock).apply {
            assertFalse(interceptCall("any", "", Method.GET).shouldIntercept)
        }
    }

    @Test
    fun `when contains not matches url, then shouldIntercept must be true`() {
        IrisMockScope(chainMock).apply {
            assertFalse(interceptCall("", "any", Method.GET).shouldIntercept)
        }
    }
}
