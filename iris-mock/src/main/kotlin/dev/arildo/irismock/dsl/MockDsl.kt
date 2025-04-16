@file:JvmMultifileClass
@file:Suppress("UnusedReceiverParameter")

package dev.arildo.irismock.dsl

import com.google.gson.Gson
import dev.arildo.irismock.InterceptedRequest
import dev.arildo.irismock.IrisMock
import dev.arildo.irismock.callmodifier.CustomResponseBodyModifier

/**
 * Creates a success response - code 200 - based on [response] string
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMock(chain) {
 *     onGet("/products") mockResponse jsonResponse
 *     // or
 *     onGet("/products") {
 *         mockResponse(jsonResponse)
 *     }
 * }
 * ```
 * @param response used as response body
 */

infix fun InterceptedRequest.mockResponse(response: String) {
    if (shouldIntercept) {
        addModifier(CustomResponseBodyModifier(irisMockScope.chain.hashCode(), response))
    }
}

/**
 * Creates a success response - code 200 - based on [response] map
 * It converts Map internally to a Json object
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMock(chain) {
 *     onGet("/image") mockResponse MyModel()
 *     // or
 *     onGet("/image") mockResponse mapOf("imageUrl" to "https://imageurl.com")
 * }
 * ```
 *  @param response used as response body
 */
infix fun InterceptedRequest.mockResponse(response: Any) {
    if (shouldIntercept) {
        IrisMock.logger.info("Mocking Response: [$method] $url")

        val jsonResponse = Gson().toJson(response)
        addModifier(CustomResponseBodyModifier(irisMockScope.chain.hashCode(), jsonResponse))
    }
}
