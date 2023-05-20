package dev.arildo.iris.mock

import dev.arildo.iris.mock.util.CONTENT_TYPE
import dev.arildo.iris.mock.util.HttpCode
import dev.arildo.iris.mock.util.IRIS_HEADER_IGNORE
import dev.arildo.iris.mock.util.IRIS_HEADER_KEY
import dev.arildo.iris.mock.util.IRIS_MOCK_TAG
import dev.arildo.iris.mock.util.MEDIA_TYPE
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.util.logging.Logger

class IrisMockScope internal constructor(chain: Interceptor.Chain) {

    private var customResponse: Response? = null

    internal val request: Request = chain.request()
    internal val url: String = request.url().toString()

    internal fun createCustomResponse(httpCode: HttpCode, body: String = "") {
        customResponse = Response.Builder()
            .code(httpCode.code)
            .protocol(Protocol.HTTP_1_1)
            .request(request)
            .message(body)
            .body(ResponseBody.create(request.body()?.contentType(), body))
            .addHeader(CONTENT_TYPE, MEDIA_TYPE)
            .build()
    }

    private fun createBlankResponse() = Response.Builder()
        .request(request)
        .addHeader(IRIS_HEADER_KEY, IRIS_HEADER_IGNORE)
        .build()

    internal fun build(): Response = customResponse ?: createBlankResponse()

    companion object {
        internal val logger = Logger.getLogger(IRIS_MOCK_TAG)
    }
}
