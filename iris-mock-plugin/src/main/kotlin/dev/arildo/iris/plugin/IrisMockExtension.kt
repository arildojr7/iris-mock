package dev.arildo.iris.plugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class IrisMockExtension @Inject constructor(objects: ObjectFactory) {

    val enabled: Property<Boolean> =
        objects.property(Boolean::class.javaObjectType).convention(true)

    val configuration: Property<String> =
        objects.property(String::class.java).convention("implementation")
}
