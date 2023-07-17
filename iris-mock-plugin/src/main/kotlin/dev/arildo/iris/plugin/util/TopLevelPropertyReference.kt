package dev.arildo.iris.plugin.util

import dev.arildo.iris.plugin.util.TopLevelPropertyReference.Descriptor
import dev.arildo.iris.plugin.util.TopLevelPropertyReference.Psi
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

public sealed class TopLevelPropertyReference : AnnotatedReference, PropertyReference {

  protected abstract val type: TypeReference?

  public override fun typeOrNull(): TypeReference? = type

  override fun toString(): String = "$fqName"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is TopLevelPropertyReference) return false

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
    override val fqName: FqName,
    override val name: String,
    override val module: AnvilModuleDescriptor,
  ) : TopLevelPropertyReference(), PropertyReference.Psi {

    override val annotations: List<AnnotationReference.Psi>
      get() = property.annotationEntries
        .filter {
          val annotationUseSiteTarget = it.useSiteTarget?.getAnnotationUseSiteTarget()
          annotationUseSiteTarget != PROPERTY_SETTER && annotationUseSiteTarget != PROPERTY_GETTER
        }
        .map { it.toAnnotationReference(null, module) }
        .plus(setterAnnotations)
        .plus(getterAnnotations)

    override val type: TypeReference? by lazy(NONE) {
      property.typeReference?.toTypeReference(null, module)
    }

    override val setterAnnotations: List<AnnotationReference.Psi>
      get() = property.annotationEntries
        .filter { it.useSiteTarget?.getAnnotationUseSiteTarget() == PROPERTY_SETTER }
        .plus((property as? KtProperty)?.setter?.annotationEntries ?: emptyList())
        .map { it.toAnnotationReference(null, module) }

    override val getterAnnotations: List<AnnotationReference.Psi>
      get() = property.annotationEntries
        .filter { it.useSiteTarget?.getAnnotationUseSiteTarget() == PROPERTY_GETTER }
        .plus((property as? KtProperty)?.getter?.annotationEntries ?: emptyList())
        .map { it.toAnnotationReference(null, module) }

    override fun visibility(): Visibility {
      return when (val visibility = property.visibilityModifierTypeOrDefault()) {
        KtTokens.PUBLIC_KEYWORD -> Visibility.PUBLIC
        KtTokens.INTERNAL_KEYWORD -> Visibility.INTERNAL
        KtTokens.PROTECTED_KEYWORD -> Visibility.PROTECTED
        KtTokens.PRIVATE_KEYWORD -> Visibility.PRIVATE
        else -> throw AnvilCompilationExceptionPropertyReference(
          propertyReference = this,
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
        fqName: FqName = property.requireFqName(),
        name: String = fqName.shortName().asString(),
        module: AnvilModuleDescriptor,
      ): Psi where T : KtCallableDeclaration,
                   T : KtValVarKeywordOwner = Psi(
        property = property,
        fqName = fqName,
        name = name,
        module = module
      )
    }
  }

  public class Descriptor internal constructor(
    public override val property: PropertyDescriptor,
    override val fqName: FqName = property.fqNameSafe,
    override val name: String = fqName.shortName().asString(),
    override val module: AnvilModuleDescriptor,
  ) : TopLevelPropertyReference(), PropertyReference.Descriptor {

    override val annotations: List<AnnotationReference.Descriptor>
      get() = property.annotations
        .plus(property.backingField?.annotations ?: emptyList())
        .map { it.toAnnotationReference(null, module) }
        .plus(setterAnnotations)
        .plus(getterAnnotations)

    override val setterAnnotations: List<AnnotationReference.Descriptor>
      get() = property.setter
        ?.annotations
        ?.map { it.toAnnotationReference(null, module) }
        .orEmpty()

    override val getterAnnotations: List<AnnotationReference.Descriptor>
      get() = property.getter
        ?.annotations
        ?.map { it.toAnnotationReference(null, module) }
        .orEmpty()

    override val type: TypeReference by lazy(NONE) {
      property.type.toTypeReference(null, module)
    }

    override fun visibility(): Visibility {
      return when (val visibility = property.visibility) {
        DescriptorVisibilities.PUBLIC -> Visibility.PUBLIC
        DescriptorVisibilities.INTERNAL -> Visibility.INTERNAL
        DescriptorVisibilities.PROTECTED -> Visibility.PROTECTED
        DescriptorVisibilities.PRIVATE -> Visibility.PRIVATE
        else -> throw AnvilCompilationExceptionPropertyReference(
          propertyReference = this,
          message = "Couldn't get visibility $visibility for property $fqName."
        )
      }
    }

    override fun isLateinit(): Boolean = property.isLateInit
  }
}

public fun KtProperty.toTopLevelPropertyReference(
  module: AnvilModuleDescriptor,
): Psi = Psi(property = this, module = module)

