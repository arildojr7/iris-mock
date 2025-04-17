package dev.arildo.irismock

import dev.arildo.irismock.dsl.IrisMockDslMarker
import dev.arildo.irismock.util.log
import okhttp3.Interceptor
import okhttp3.Response

@IrisMockDslMarker
class IrisMockScope internal constructor(internal val chain: Interceptor.Chain) {
    internal fun build(): Response {
        return ModifierProcessor.process(chain).also { response ->
            if (IrisMock.enableLogs) response.log()
        }
    }
}
