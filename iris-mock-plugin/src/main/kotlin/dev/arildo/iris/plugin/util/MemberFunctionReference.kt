package dev.arildo.iris.plugin.util

import dev.arildo.iris.plugin.util.MemberFunctionReference.Descriptor
import dev.arildo.iris.plugin.util.MemberFunctionReference.Psi
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality.ABSTRACT
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.lexer.KtTokens.ABSTRACT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.PUBLIC_KEYWORD
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Used to create a common type between [KtNamedFunction] class references and
 * [FunctionDescriptor] references, to streamline parsing.
 */
public sealed class MemberFunctionReference : AnnotatedReference, FunctionReference {

  public abstract val declaringClass: ClassReference

  public override val module: AnvilModuleDescriptor get() = declaringClass.module

  protected abstract val returnType: TypeReference?

  public override fun returnTypeOrNull(): TypeReference? = returnType

  public abstract fun isAbstract(): Boolean
  public abstract fun isConstructor(): Boolean
  public fun resolveGenericReturnTypeOrNull(
    implementingClass: ClassReference
  ): ClassReference? {
    return returnType
      ?.resolveGenericTypeOrNull(implementingClass)
      ?.asClassReferenceOrNull()
  }

  public fun resolveGenericReturnType(implementingClass: ClassReference): ClassReference =
    resolveGenericReturnTypeOrNull(implementingClass)
      ?: throw AnvilCompilationExceptionFunctionReference(
        functionReference = this,
        message = "Unable to resolve return type for function $fqName with the implementing " +
          "class ${implementingClass.fqName}."
      )

  override fun toString(): String = "$fqName()"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is MemberFunctionReference) return false

    if (fqName != other.fqName) return false

    return true
  }

  override fun hashCode(): Int {
    return fqName.hashCode()
  }

  public class Psi internal constructor(
    public override val function: KtFunction,
    override val declaringClass: ClassReference.Psi,
    override val fqName: FqName
  ) : MemberFunctionReference(), FunctionReference.Psi {

    override val annotations: List<AnnotationReference.Psi>
      get() = function.annotationEntries.map {
        it.toAnnotationReference(declaringClass = null, module)
      }

    override val returnType: TypeReference.Psi? by lazy(NONE) {
      function.typeReference?.toTypeReference(declaringClass, module)
    }

    override val parameters: List<ParameterReference.Psi> by lazy(NONE) {
      function.valueParameters.map { it.toParameterReference(this) }
    }

    override fun isAbstract(): Boolean =
      function.hasModifier(ABSTRACT_KEYWORD) ||
        (declaringClass.isInterface() && !function.hasBody())

    override fun isConstructor(): Boolean = function is KtConstructor<*>

    override fun visibility(): Visibility {
      return when (val visibility = function.visibilityModifierTypeOrDefault()) {
        PUBLIC_KEYWORD -> Visibility.PUBLIC
        KtTokens.INTERNAL_KEYWORD -> Visibility.INTERNAL
        KtTokens.PROTECTED_KEYWORD -> Visibility.PROTECTED
        KtTokens.PRIVATE_KEYWORD -> Visibility.PRIVATE
        else -> throw AnvilCompilationExceptionClassReference(
          classReference = declaringClass,
          message = "Couldn't get visibility $visibility for function $fqName."
        )
      }
    }
  }

  public class Descriptor internal constructor(
    public override val function: FunctionDescriptor,
    override val declaringClass: ClassReference.Descriptor,
    override val fqName: FqName = function.fqNameSafe
  ) : MemberFunctionReference(), FunctionReference.Descriptor {

    override val annotations: List<AnnotationReference.Descriptor>
      get() = function.annotations.map {
        it.toAnnotationReference(declaringClass = null, module)
      }

    override val parameters: List<ParameterReference.Descriptor> by lazy(NONE) {
      function.valueParameters.map { it.toParameterReference(this) }
    }

    override val returnType: TypeReference.Descriptor? by lazy(NONE) {
      function.returnType?.toTypeReference(declaringClass, module)
    }

    override fun isAbstract(): Boolean = function.modality == ABSTRACT

    override fun isConstructor(): Boolean = function is ClassConstructorDescriptor

    override fun visibility(): Visibility {
      return when (val visibility = function.visibility) {
        DescriptorVisibilities.PUBLIC -> Visibility.PUBLIC
        DescriptorVisibilities.INTERNAL -> Visibility.INTERNAL
        DescriptorVisibilities.PROTECTED -> Visibility.PROTECTED
        DescriptorVisibilities.PRIVATE -> Visibility.PRIVATE
        else -> throw AnvilCompilationExceptionClassReference(
          classReference = declaringClass,
          message = "Couldn't get visibility $visibility for function $fqName."
        )
      }
    }
  }
}

public fun KtFunction.toFunctionReference(
  declaringClass: ClassReference.Psi
): Psi {
  val fqName = if (this is KtConstructor<*>) {
    declaringClass.fqName.child(Name.identifier("<init>"))
  } else {
    requireFqName()
  }

  return Psi(this, declaringClass, fqName)
}

public fun FunctionDescriptor.toFunctionReference(
  declaringClass: ClassReference.Descriptor,
): Descriptor {
  return Descriptor(this, declaringClass)
}
