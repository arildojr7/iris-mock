package dev.arildo.iris.mock

import dev.arildo.iris.mock.util.CONTENT_TYPE
import dev.arildo.iris.mock.util.HttpCode
import dev.arildo.iris.mock.util.IRIS_HEADER_IGNORE
import dev.arildo.iris.mock.util.IRIS_HEADER_KEY
import dev.arildo.iris.mock.util.IRIS_MOCK_TAG
import dev.arildo.iris.mock.util.MEDIA_TYPE
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import org.jetbrains.annotations.VisibleForTesting
import java.util.logging.Logger

class IrisMockScope internal constructor(chain: Interceptor.Chain) {

    @VisibleForTesting
    internal var useCustomResponse = false

    @VisibleForTesting
    internal var useOriginalResponse = false

    private val originalResponse by lazy { chain.proceed(chain.request()) }

    internal val customResponse = Response.Builder()
    internal val customRequest = chain.request().newBuilder()
    internal val url = chain.request().url().toString()

    internal fun proceedOriginalResponse() {
        useOriginalResponse = true
    }

    internal fun createCustomResponse(httpCode: HttpCode, body: String = "") {
        val request = customRequest.build()
        useCustomResponse = true
        customResponse
            .code(httpCode.code)
            .protocol(Protocol.HTTP_1_1)
            .request(request)
            .message(body)
            .body(ResponseBody.create(request.body()?.contentType(), body))
            .addHeader(CONTENT_TYPE, MEDIA_TYPE)
    }

    private fun createBlankResponse() = Response.Builder()
        .request(customRequest.build())
        .protocol(Protocol.HTTP_1_1)
        .code(HttpCode.OK.code)
        .message("")
        .addHeader(IRIS_HEADER_KEY, IRIS_HEADER_IGNORE)
        .build()

    internal fun build(): Response = when {
        useOriginalResponse -> originalResponse
        useCustomResponse -> customResponse.build()
        else -> createBlankResponse()
    }

    companion object {
        internal val logger = Logger.getLogger(IRIS_MOCK_TAG)
    }
}
