package dev.arildo.iris.plugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

internal const val VERSION = "1.0.10"

abstract class IrisMockGradleExtension @Inject constructor(objects: ObjectFactory) {

    val enableOnlyOnDebugVariant: Property<Boolean> =
        objects.property(Boolean::class.java).convention(false)

}
