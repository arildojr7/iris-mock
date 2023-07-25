package dev.arildo.iris.plugin

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class IrisMockGradleExtension @Inject constructor(objects: ObjectFactory) {

//    val enableOnlyOnDebugVariant: Property<Boolean> =
//        objects.property(Boolean::class.java).convention(false)
}
