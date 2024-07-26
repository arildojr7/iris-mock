@file:JvmMultifileClass

package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.InterceptedRequest
import dev.arildo.iris.mock.IrisMockScope
import dev.arildo.iris.mock.interceptCall
import dev.arildo.iris.mock.util.Method

/**
 * Intercept GET call when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMock(chain) {
 *     onGet("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return an [InterceptedRequest] as receiver for DSL functions.
 */

fun IrisMockScope.onGet(
    endsWith: String = "",
    contains: String = "",
    block: InterceptedRequest.() -> Unit = {}
) = interceptCall(endsWith, contains, Method.GET).also { if (it.shouldIntercept) block(it) }

/**
 * Intercept POST call when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMock(chain) {
 *     onPost("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return an [InterceptedRequest] as receiver for DSL functions.
 */
fun IrisMockScope.onPost(
    endsWith: String = "",
    contains: String = "",
    block: InterceptedRequest.() -> Unit = {}
) = interceptCall(endsWith, contains, Method.POST).also { if (it.shouldIntercept) block(it) }

/**
 * Intercept DELETE call when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMock(chain) {
 *     onDelete("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return an [InterceptedRequest] as receiver for DSL functions.
 */
fun IrisMockScope.onDelete(
    endsWith: String = "",
    contains: String = "",
    block: InterceptedRequest.() -> Unit = {}
) = interceptCall(endsWith, contains, Method.DELETE).also { if (it.shouldIntercept) block(it) }

/**
 * Intercept PUT call when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMock(chain) {
 *     onPut("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return an [InterceptedRequest] as receiver for DSL functions.
 */
fun IrisMockScope.onPut(
    endsWith: String = "",
    contains: String = "",
    block: InterceptedRequest.() -> Unit = {}
) = interceptCall(endsWith, contains, Method.PUT).also { if (it.shouldIntercept) block(it) }

/**
 * Intercept PATCH call when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMock(chain) {
 *     onPatch("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return an [InterceptedRequest] as receiver for DSL functions.
 */
fun IrisMockScope.onPatch(
    endsWith: String = "",
    contains: String = "",
    block: InterceptedRequest.() -> Unit = {}
) = interceptCall(endsWith, contains, Method.PATCH).also { if (it.shouldIntercept) block(it) }
