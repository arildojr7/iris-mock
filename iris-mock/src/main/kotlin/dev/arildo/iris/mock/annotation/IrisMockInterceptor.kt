package dev.arildo.iris.mock.annotation

/**
 * Annotation for defining interceptors that will be auto injected
 * in the OkHttp builder.
 *
 * Important: The auto injection only works when using iris-mock plugin.
 *
 * See more at [official docs](https://irismock.arildo.dev/getting-started/configure-gradle)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class IrisMockInterceptor
