package dev.arildo.iris.mock

import dev.arildo.iris.mock.dsl.IrisMockDslMarker
import dev.arildo.iris.mock.util.HttpCode
import dev.arildo.iris.mock.util.IRIS_MOCK_TAG
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import java.util.logging.Logger

@IrisMockDslMarker
class IrisMockScope internal constructor(internal val chain: Interceptor.Chain) {

    // as [Interceptor] requires a non null Response, let's build a blank one to return
    // since the real party it's happening on [IrisMockWrapper]
    internal fun build() = Response.Builder()
        .request(chain.request())
        .protocol(Protocol.HTTP_1_1)
        .code(HttpCode.OK.code)
        .message("")
        .build()

}
