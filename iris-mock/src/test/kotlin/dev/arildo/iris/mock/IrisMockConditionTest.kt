package dev.arildo.iris.mock

import dev.arildo.iris.mock.dsl.onIntercept
import dev.arildo.iris.mock.util.Method
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IrisMockConditionTest {
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
    fun `when endsWith and contains are empty, then throw exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            IrisMockScope(chainMock).apply {
                onIntercept(" ", " ", Method.GET)
            }
        }
    }

    @Test
    fun `when endsWith and contains are not empty, then throw exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            IrisMockScope(chainMock).apply {
                onIntercept("any", "any", Method.GET)
            }
        }
    }

    @Test
    fun `when endsWith matches url, then shouldIntercept must be true`() {
        IrisMockScope(chainMock).apply {
            assertTrue(onIntercept("user/me", "", Method.GET).shouldIntercept)
        }
    }

    @Test
    fun `when contains matches url, then shouldIntercept must be true`() {
        IrisMockScope(chainMock).apply {
            assertTrue(onIntercept("", "user/me", Method.GET).shouldIntercept)
        }
    }

    @Test
    fun `when endsWith not matches url, then shouldIntercept must be true`() {
        IrisMockScope(chainMock).apply {
            assertFalse(onIntercept("any", "", Method.GET).shouldIntercept)
        }
    }

    @Test
    fun `when contains not matches url, then shouldIntercept must be true`() {
        IrisMockScope(chainMock).apply {
            assertFalse(onIntercept("", "any", Method.GET).shouldIntercept)
        }
    }
}
