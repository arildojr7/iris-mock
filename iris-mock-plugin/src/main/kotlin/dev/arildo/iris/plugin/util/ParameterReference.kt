package dev.arildo.iris.plugin.util

import com.squareup.kotlinpoet.TypeName
import dev.arildo.iris.plugin.util.ParameterReference.Descriptor
import dev.arildo.iris.plugin.util.ParameterReference.Psi
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.KtParameter
import kotlin.LazyThreadSafetyMode.NONE

public sealed class ParameterReference : AnnotatedReference {

  public abstract val name: String
  public abstract val declaringFunction: FunctionReference
  public val module: AnvilModuleDescriptor get() = declaringFunction.module

  protected abstract val type: TypeReference?

  protected abstract val declaringClass: ClassReference?

  public fun type(): TypeReference = type
    ?: throw AnvilCompilationExceptionParameterReference(
      parameterReference = this,
      message = "Unable to get type for the parameter with name $name of " +
        "function ${declaringFunction.fqName}"
    )

  override fun toString(): String {
    return "${this::class.qualifiedName}(${declaringFunction.fqName}(.., $name,..))"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ParameterReference) return false

    if (name != other.name) return false
    if (declaringFunction != other.declaringFunction) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + declaringFunction.hashCode()
    return result
  }

  public class Psi(
    public val parameter: KtParameter,
    override val declaringFunction: FunctionReference.Psi
  ) : ParameterReference() {
    override val name: String = parameter.nameAsSafeName.asString()

    override val annotations: List<AnnotationReference.Psi>
      get() = parameter.annotationEntries.map {
        it.toAnnotationReference(declaringClass = null, module)
      }

    override val type: TypeReference.Psi? by lazy(NONE) {
      parameter.typeReference?.toTypeReference(declaringClass, module)
    }

    override val declaringClass: ClassReference.Psi?
      get() = when (declaringFunction) {
        is TopLevelFunctionReference.Psi -> null
        is MemberFunctionReference.Psi -> declaringFunction.declaringClass
        else -> null
      }
  }

  public class Descriptor(
    public val parameter: ValueParameterDescriptor,
    override val declaringFunction: FunctionReference.Descriptor
  ) : ParameterReference() {
    override val name: String = parameter.name.asString()

    override val annotations: List<AnnotationReference.Descriptor>
      get() = parameter.annotations.map {
        it.toAnnotationReference(declaringClass = null, module)
      }

    override val type: TypeReference.Descriptor? by lazy(NONE) {
      parameter.type.toTypeReference(declaringClass, module)
    }

    override val declaringClass: ClassReference.Descriptor?
      get() = when (declaringFunction) {
        is TopLevelFunctionReference.Descriptor -> null
        is MemberFunctionReference.Descriptor -> declaringFunction.declaringClass
        else -> null
      }
  }
}

public fun KtParameter.toParameterReference(
  declaringFunction: FunctionReference.Psi
): Psi {
  return Psi(this, declaringFunction)
}

public fun ValueParameterDescriptor.toParameterReference(
  declaringFunction: FunctionReference.Descriptor
): Descriptor {
  return Descriptor(this, declaringFunction)
}

@Suppress("FunctionName")
public fun AnvilCompilationExceptionParameterReference(
  parameterReference: ParameterReference,
  message: String,
  cause: Throwable? = null
): Exception = Exception()
