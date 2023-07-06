package dev.arildo.iris.mock.util

import io.mockk.mockk
import io.mockk.verify
import okhttp3.Response
import org.junit.jupiter.api.Test

class UtilsTest {

    @Test
    fun `when log response, then ensure that Response newBuilder is called`() {
        val responseMock = mockk<Response>(relaxed = true)

        responseMock.log()

        verify(exactly = 1) { responseMock.newBuilder() }
    }
}
