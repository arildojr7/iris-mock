package dev.arildo.iris.plugin.util

import com.squareup.kotlinpoet.MemberName
import dev.arildo.iris.plugin.util.MemberPropertyReference.Descriptor
import dev.arildo.iris.plugin.util.MemberPropertyReference.Psi
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationUseSiteTarget.PROPERTY_GETTER
import org.jetbrains.kotlin.descriptors.annotations.AnnotationUseSiteTarget.PROPERTY_SETTER
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValVarKeywordOwner
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import kotlin.LazyThreadSafetyMode.NONE

public sealed class MemberPropertyReference : AnnotatedReference, PropertyReference {

  public abstract val declaringClass: ClassReference

  public override val module: AnvilModuleDescriptor get() = declaringClass.module

  protected abstract val type: TypeReference?

  public override fun typeOrNull(): TypeReference? = type

  override fun toString(): String = "$fqName"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is MemberPropertyReference) return false

    if (fqName != other.fqName) return false

    return true
  }

  override fun hashCode(): Int {
    return fqName.hashCode()
  }

  /**
   * @param property In practice, this is either a [KtProperty], or a [KtParameter] for which
   *   [KtParameter.isPropertyParameter()] is true. [KtCallableDeclaration] is the most applicable
   *   common interface, but it's also implemented by other types like `KtConstructor`.
   */
  public class Psi private constructor(
    public override val property: KtCallableDeclaration,
    override val declaringClass: ClassReference.Psi,
    override val fqName: FqName,
    override val name: String
  ) : MemberPropertyReference(), PropertyReference.Psi {

    override val annotations: List<AnnotationReference.Psi>
      get() = property.annotationEntries
        .filter {
          val annotationUseSiteTarget = it.useSiteTarget?.getAnnotationUseSiteTarget()
          annotationUseSiteTarget != PROPERTY_SETTER && annotationUseSiteTarget != PROPERTY_GETTER
        }
        .map { it.toAnnotationReference(declaringClass, module) }
        .plus(setterAnnotations)
        .plus(getterAnnotations)

    override val type: TypeReference? by lazy(NONE) {
      property.typeReference?.toTypeReference(declaringClass, module)
    }

    override val setterAnnotations: List<AnnotationReference.Psi>
      get() = property.annotationEntries
        .filter { it.useSiteTarget?.getAnnotationUseSiteTarget() == PROPERTY_SETTER }
        .plus((property as? KtProperty)?.setter?.annotationEntries ?: emptyList())
        .map { it.toAnnotationReference(declaringClass, module) }

    override val getterAnnotations: List<AnnotationReference.Psi>
      get() = property.annotationEntries
        .filter { it.useSiteTarget?.getAnnotationUseSiteTarget() == PROPERTY_GETTER }
        .plus((property as? KtProperty)?.getter?.annotationEntries ?: emptyList())
        .map { it.toAnnotationReference(declaringClass, module) }

    override fun visibility(): Visibility {
      return when (val visibility = property.visibilityModifierTypeOrDefault()) {
        KtTokens.PUBLIC_KEYWORD -> Visibility.PUBLIC
        KtTokens.INTERNAL_KEYWORD -> Visibility.INTERNAL
        KtTokens.PROTECTED_KEYWORD -> Visibility.PROTECTED
        KtTokens.PRIVATE_KEYWORD -> Visibility.PRIVATE
        else -> throw AnvilCompilationExceptionClassReference(
          classReference = declaringClass,
          message = "Couldn't get visibility $visibility for property $fqName."
        )
      }
    }

    override fun isLateinit(): Boolean {
      return property.modifierList?.hasModifier(KtTokens.LATEINIT_KEYWORD) ?: false
    }

    internal companion object {
      // There's no single applicable type for a PSI property. The multiple generic bounds prevent
      // us from creating a property out of a function, constructor, or destructuring declaration.
      internal operator fun <T> invoke(
        property: T,
        declaringClass: ClassReference.Psi,
        fqName: FqName = property.requireFqName(),
        name: String = fqName.shortName().asString()
      ): Psi where T : KtCallableDeclaration,
                   T : KtValVarKeywordOwner = Psi(
        property = property,
        declaringClass = declaringClass,
        fqName = fqName,
        name = name
      )
    }
  }

  public class Descriptor internal constructor(
    public override val property: PropertyDescriptor,
    override val declaringClass: ClassReference.Descriptor,
    override val fqName: FqName = property.fqNameSafe,
    override val name: String = fqName.shortName().asString()
  ) : MemberPropertyReference(), PropertyReference.Descriptor {

    override val annotations: List<AnnotationReference.Descriptor>
      get() = property.annotations
        .plus(property.backingField?.annotations ?: emptyList())
        .map { it.toAnnotationReference(declaringClass, module) }
        .plus(setterAnnotations)
        .plus(getterAnnotations)

    override val setterAnnotations: List<AnnotationReference.Descriptor>
      get() = property.setter
        ?.annotations
        ?.map { it.toAnnotationReference(declaringClass, module) }
        .orEmpty()

    override val getterAnnotations: List<AnnotationReference.Descriptor>
      get() = property.getter
        ?.annotations
        ?.map { it.toAnnotationReference(declaringClass, module) }
        .orEmpty()

    override val type: TypeReference by lazy(NONE) {
      property.type.toTypeReference(declaringClass, module)
    }

    override fun visibility(): Visibility {
      return when (val visibility = property.visibility) {
        DescriptorVisibilities.PUBLIC -> Visibility.PUBLIC
        DescriptorVisibilities.INTERNAL -> Visibility.INTERNAL
        DescriptorVisibilities.PROTECTED -> Visibility.PROTECTED
        DescriptorVisibilities.PRIVATE -> Visibility.PRIVATE
        else -> throw AnvilCompilationExceptionClassReference(
          classReference = declaringClass,
          message = "Couldn't get visibility $visibility for property $fqName."
        )
      }
    }

    override fun isLateinit(): Boolean = property.isLateInit
  }
}

public fun KtParameter.toPropertyReference(
  declaringClass: ClassReference.Psi
): Psi {
  if (!isPropertyParameter()) {
    throw Exception("A KtParameter may only be turned into a PropertyReference if it's a val or var.")
  }
  return Psi(this, declaringClass)
}

public fun KtProperty.toPropertyReference(
  declaringClass: ClassReference.Psi
): Psi = Psi(this, declaringClass)

public fun PropertyDescriptor.toPropertyReference(
  declaringClass: ClassReference.Descriptor
): Descriptor = Descriptor(this, declaringClass)
