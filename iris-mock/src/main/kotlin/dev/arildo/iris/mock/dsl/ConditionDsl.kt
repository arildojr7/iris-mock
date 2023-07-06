@file:JvmMultifileClass
@file:Suppress("unused", "UnusedReceiverParameter")

package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.InterceptedScope
import dev.arildo.iris.mock.IrisMockCondition
import dev.arildo.iris.mock.IrisMockScope
import dev.arildo.iris.mock.util.HttpCode
import dev.arildo.iris.mock.util.HttpCode.OK
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Creates a success response - code 200 - based on [response] string
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onGet("/products") mockResponse "jsonResponse"
 * }
 * ```
 * @param response used as response body
 */
infix fun IrisMockCondition.mockResponse(response: String) {
    if (shouldIntercept) {
        IrisMockScope.logger.info("Mocking Response: [$method] ${irisMockScope.url}")
        irisMockScope.createCustomResponse(OK, response)
    }
}

/**
 * Creates a success response - code 200 - based on [response] map
 * It converts Map internally to a Json object
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onGet("/image") mockResponse mapOf(
 *         "imageId" to 1234,
 *         "imageUrl" to "https://imageurl.com"
 *     )
 * }
 * ```
 *  @param response used as response body
 */
infix fun IrisMockCondition.mockResponse(response: Map<String, Any?>) {
    if (shouldIntercept) {
        IrisMockScope.logger.info("Mocking Response: [$method] ${irisMockScope.url}")

        val jsonResponse = Json.encodeToJsonElement(response).toString()

        irisMockScope.createCustomResponse(OK, jsonResponse)
    }
}

/**
 * Creates a custom response based on [response] map
 * It converts Map internally to a Json object
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onGet("/image") mockResponse mapOf(
 *         "imageId" to 1234,
 *         "imageUrl" to "https://imageurl.com"
 *     )
 * }
 * ```
 *  @param response used as response body
 */
fun IrisMockCondition.mockCustomResponse(response: Map<String, String?>, httpCode: HttpCode = OK) {
    if (shouldIntercept) {
        IrisMockScope.logger.info("Mocking Response: [$method] ${irisMockScope.url}")

        val jsonResponse = Json.encodeToJsonElement(response).toString()

        irisMockScope.createCustomResponse(httpCode, jsonResponse)
    }
}

/**
 * Creates a block to run custom logic
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onGet("/products") then {
 *         if (condition) {
 *             mockEmptyJsonObject()
 *         }
 *     }
 * }
 * ```
 */
infix fun IrisMockCondition.then(block: suspend InterceptedScope.() -> Unit) = runBlocking(IO) {
    if (shouldIntercept) block(InterceptedScope(method, shouldIntercept = true, irisMockScope))
}

/**
 * Creates a simple empty json object
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onGet("/products") then {
 *         if (condition) {
 *             mockEmptyJsonObject()
 *         }
 *     }
 * }
 * ```
 * @return an empty json object response
 */
fun IrisMockCondition.mockEmptyJsonObject() = "{}"

/**
 * Creates a simple empty json array
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onGet("/products") then {
 *         if (productList == 0) {
 *             mockEmptyJsonArray()
 *         }
 *     }
 * }
 * ```
 * @return an empty json array response
 */
fun IrisMockCondition.mockEmptyJsonArray() = "[]"
