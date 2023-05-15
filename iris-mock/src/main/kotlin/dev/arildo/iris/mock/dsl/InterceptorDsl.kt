@file:JvmMultifileClass
@file:Suppress("unused")
package dev.arildo.iris.mock.dsl

import dev.arildo.iris.mock.IrisMockCondition
import dev.arildo.iris.mock.IrisMockScope
import dev.arildo.iris.mock.createCondition
import dev.arildo.iris.mock.util.METHOD_DELETE
import dev.arildo.iris.mock.util.METHOD_GET
import dev.arildo.iris.mock.util.METHOD_PATCH
import dev.arildo.iris.mock.util.METHOD_POST
import dev.arildo.iris.mock.util.METHOD_PUT

/**
 * Intercept GET calls when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onGet("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return a [IrisMockCondition] to be used as receiver for DSL functions.
 */
fun IrisMockScope.onGet(endsWith: String = "", contains: String = "") =
    onIntercept(endsWith, contains, METHOD_GET)

/**
 * Intercept POST calls when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onPost("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return a [IrisMockCondition] to be used as receiver for DSL functions.
 */
fun IrisMockScope.onPost(endsWith: String = "", contains: String = "") =
    onIntercept(endsWith, contains, METHOD_POST)

/**
 * Intercept DELETE calls when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onDelete("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return a [IrisMockCondition] to be used as receiver for DSL functions.
 */
fun IrisMockScope.onDelete(endsWith: String = "", contains: String = "") =
    onIntercept(endsWith, contains, METHOD_DELETE)

/**
 * Intercept PUT calls when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onPut("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return a [IrisMockCondition] to be used as receiver for DSL functions.
 */
fun IrisMockScope.onPut(endsWith: String = "", contains: String = "") =
    onIntercept(endsWith, contains, METHOD_PUT)

/**
 * Intercept PATCH calls when [endsWith] OR [contains] are true.
 * ```
 * // sample
 * override fun intercept(chain: Chain) = irisMockScope(chain) {
 *     onPatch("/user/profile") mockResponse "userProfileJson"
 * }
 * ```
 * @return a [IrisMockCondition] to be used as receiver for DSL functions.
 */
fun IrisMockScope.onPatch(endsWith: String = "", contains: String = "") =
    onIntercept(endsWith, contains, METHOD_PATCH)

private fun IrisMockScope.onIntercept(
    endsWith: String,
    contains: String,
    method: String
): IrisMockCondition {
    return createCondition(request, contains, endsWith, method).also {
        if (it.shouldIntercept) IrisMockScope.logger.info("Intercepting: [$method] $url")
    }
}
