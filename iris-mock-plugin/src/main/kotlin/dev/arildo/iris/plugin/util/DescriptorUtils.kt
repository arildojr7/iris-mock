@file:Suppress("unused")

package dev.arildo.iris.plugin.util

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.constants.KClassValue.Value.NormalClass
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.isError

// When the Kotlin type is of the form: KClass<OurType>.
public fun KotlinType.argumentType(): KotlinType = arguments.first().type

public fun KotlinType.classDescriptorOrNull(): ClassDescriptor? {
  return TypeUtils.getClassDescriptor(this)
}

public fun KotlinType.classDescriptor(): ClassDescriptor {
  return classDescriptorOrNull()
    ?: throw Exception(
      "Unable to resolve type for $this."
    )
}

public fun ConstantValue<*>.argumentType(module: ModuleDescriptor): KotlinType {
  val argumentType = getType(module).argumentType()
  if (!argumentType.isError) return argumentType

  // Handle inner classes explicitly. When resolving the Kotlin type of inner class from
  // dependencies the compiler might fail. It tries to load my.package.Class$Inner and fails
  // whereas is should load my.package.Class.Inner.
  val normalClass = this.value
  if (normalClass !is NormalClass) return argumentType

  val classId = normalClass.value.classId

  return module
    .findClassAcrossModuleDependencies(
      classId = ClassId(
        classId.packageFqName,
        FqName(classId.relativeClassName.asString().replace('$', '.')),
        false
      )
    )
    ?.defaultType
    ?: throw Exception(
      "Couldn't resolve class across module dependencies for class ID: $classId"
    )
}

/**
 * Assumes that the FqName is a top-level property, e.g. com.squareup.CONSTANT.
 */
public fun FqName.getContributedPropertyOrNull(module: ModuleDescriptor): PropertyDescriptor? {
  return module.getPackage(parent()).memberScope
    .getContributedVariables(shortName(), NoLookupLocation.FROM_BACKEND)
    .singleOrNull()
}
