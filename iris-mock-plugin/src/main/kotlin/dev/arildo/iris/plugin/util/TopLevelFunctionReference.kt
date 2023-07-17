package dev.arildo.iris.plugin.util

import dev.arildo.iris.plugin.util.TopLevelFunctionReference.Descriptor
import dev.arildo.iris.plugin.util.TopLevelFunctionReference.Psi
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import kotlin.LazyThreadSafetyMode.NONE

public sealed class TopLevelFunctionReference : AnnotatedReference, FunctionReference {

  protected abstract val returnType: TypeReference?

  public override fun returnTypeOrNull(): TypeReference? = returnType

  override fun toString(): String = "$fqName()"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is TopLevelFunctionReference) return false

    if (fqName != other.fqName) return false

    return true
  }

  override fun hashCode(): Int {
    return fqName.hashCode()
  }

  public class Psi internal constructor(
    public override val function: KtFunction,
    override val fqName: FqName,
    override val module: AnvilModuleDescriptor,
  ) : TopLevelFunctionReference(), FunctionReference.Psi {

    override val annotations: List<AnnotationReference.Psi>
      get() = function.annotationEntries.map {
        it.toAnnotationReference(declaringClass = null, module)
      }

    override val returnType: TypeReference.Psi? by lazy(NONE) {
      function.typeReference?.toTypeReference(declaringClass = null, module)
    }

    override val parameters: List<ParameterReference.Psi> by lazy(NONE) {
      function.valueParameters.map { it.toParameterReference(this) }
    }

    override fun visibility(): Visibility {
      return when (val visibility = function.visibilityModifierTypeOrDefault()) {
        KtTokens.PUBLIC_KEYWORD -> Visibility.PUBLIC
        KtTokens.INTERNAL_KEYWORD -> Visibility.INTERNAL
        KtTokens.PROTECTED_KEYWORD -> Visibility.PROTECTED
        KtTokens.PRIVATE_KEYWORD -> Visibility.PRIVATE
        else -> throw AnvilCompilationExceptionFunctionReference(
          functionReference = this,
          message = "Couldn't get visibility $visibility for function $fqName."
        )
      }
    }
  }

  public class Descriptor internal constructor(
    public override val function: FunctionDescriptor,
    override val fqName: FqName = function.fqNameSafe,
    override val module: AnvilModuleDescriptor,
  ) : TopLevelFunctionReference(), FunctionReference.Descriptor {

    override val annotations: List<AnnotationReference.Descriptor>
      get() = function.annotations.map {
        it.toAnnotationReference(declaringClass = null, module)
      }

    override val parameters: List<ParameterReference.Descriptor> by lazy(NONE) {
      function.valueParameters.map { it.toParameterReference(this) }
    }

    override val returnType: TypeReference.Descriptor? by lazy(NONE) {
      function.returnType?.toTypeReference(declaringClass = null, module)
    }

    override fun visibility(): Visibility {
      return when (val visibility = function.visibility) {
        DescriptorVisibilities.PUBLIC -> Visibility.PUBLIC
        DescriptorVisibilities.INTERNAL -> Visibility.INTERNAL
        DescriptorVisibilities.PROTECTED -> Visibility.PROTECTED
        DescriptorVisibilities.PRIVATE -> Visibility.PRIVATE
        else -> throw AnvilCompilationExceptionFunctionReference(
          functionReference = this,
          message = "Couldn't get visibility $visibility for function $fqName."
        )
      }
    }
  }
}

public fun KtFunction.toTopLevelFunctionReference(
  module: AnvilModuleDescriptor,
): Psi {
  return Psi(function = this, fqName = requireFqName(), module = module)
}

