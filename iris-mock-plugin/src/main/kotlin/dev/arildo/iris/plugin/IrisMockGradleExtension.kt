package dev.arildo.iris.plugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

internal const val DEFAULT_ANNOTATION = "dev/arildo/iris/mock/IrisWrapperInterceptor"
internal const val VERSION = "1.0.10"

abstract class IrisMockGradleExtension @Inject constructor(objects: ObjectFactory) {
    /**
     * Define a custom redacted marker annotation. The -annotations artifact won't be automatically
     * added to dependencies if you define your own!
     *
     * Note that this must be in the format of a string where packages are delimited by '/' and
     * classes by '.', e.g. "kotlin/Map.Entry"
     */
    val redactedAnnotation: Property<String> =
        objects.property(String::class.java).convention(DEFAULT_ANNOTATION)

    val enabled: Property<Boolean> =
        objects.property(Boolean::class.javaObjectType).convention(true)

    val replacementString: Property<String> =
        objects.property(String::class.java).convention("██")

    // TODO create a property to choose between implementation and debugImplementation
}
