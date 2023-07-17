package dev.arildo.iris.plugin.util

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.MemberName
import dev.arildo.iris.plugin.util.AnnotationReference.Descriptor
import dev.arildo.iris.plugin.util.AnnotationReference.Psi
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.annotationClass

private const val DEFAULT_SCOPE_INDEX = 0

/**
 * Used to create a common type between [KtAnnotationEntry] class references and
 * [AnnotationDescriptor] references, to streamline parsing.
 */
public sealed class AnnotationReference {

  /**
   * Refers to the annotation class itself and not the annotated class.
   */
  public abstract val classReference: ClassReference

  /**
   * Refers to the class that is annotated with this annotation reference. Note that annotations
   * can be used at different places, e.g. properties, constructors, functions, etc., therefore
   * this field must be nullable.
   */
  protected abstract val declaringClass: ClassReference?

  public val fqName: FqName get() = classReference.fqName
  public val shortName: String get() = fqName.shortName().asString()
  public val module: AnvilModuleDescriptor get() = classReference.module

  public abstract val arguments: List<AnnotationArgumentReference>

  public open fun declaringClass(): ClassReference = declaringClass
    ?: throw AnvilCompilationExceptionAnnotationReference(
      annotationReference = this,
      message = "The declaring class was null, this means the annotation wasn't used on a class."
    )

  public abstract fun scopeOrNull(parameterIndex: Int = DEFAULT_SCOPE_INDEX): ClassReference?

  override fun toString(): String = "@$fqName"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AnnotationReference) return false

    if (fqName != other.fqName) return false
    if (arguments != other.arguments) return false

    return true
  }

  override fun hashCode(): Int {
    var result = fqName.hashCode()
    result = 31 * result + arguments.hashCode()
    return result
  }

  public class Psi internal constructor(
    public val annotation: KtAnnotationEntry,
    override val classReference: ClassReference,
    override val declaringClass: ClassReference.Psi?,
  ) : AnnotationReference() {

    override val arguments: List<AnnotationArgumentReference.Psi>
      get() = annotation.valueArguments
        .filterIsInstance<KtValueArgument>()
        .mapIndexed { index, argument ->
          argument.toAnnotationArgumentReference(this, index)
        }

    private val defaultScope: ClassReference?
      get() = computeScope(DEFAULT_SCOPE_INDEX)

    // We need the scope so often that it's better to cache the value. Since the index could be
    // potentially different, only cache the value for the default index.
    override fun scopeOrNull(parameterIndex: Int): ClassReference? =
      if (parameterIndex == DEFAULT_SCOPE_INDEX) defaultScope else computeScope(parameterIndex)

    private fun computeScope(parameterIndex: Int): ClassReference? {
      return argumentAt("scope", parameterIndex)?.value()
    }
  }

  public class Descriptor internal constructor(
    public val annotation: AnnotationDescriptor,
    override val classReference: ClassReference,
    override val declaringClass: ClassReference.Descriptor?,
  ) : AnnotationReference() {

    override val arguments: List<AnnotationArgumentReference.Descriptor>
      get() = annotation.allValueArguments.toList().map { it.toAnnotationArgumentReference(this) }

    private val scope: ClassReference?
      get() = arguments.singleOrNull { it.name == "scope" }?.value<ClassReference>()

    override fun declaringClass(): ClassReference.Descriptor {
      return super.declaringClass() as ClassReference.Descriptor
    }

    override fun scopeOrNull(parameterIndex: Int): ClassReference? = scope
  }
}

public fun KtAnnotationEntry.toAnnotationReference(
  declaringClass: ClassReference.Psi?,
  module: ModuleDescriptor
): Psi {
  return Psi(
    annotation = this,
    classReference = requireFqName(module).toClassReference(module),
    declaringClass = declaringClass
  )
}

public fun AnnotationDescriptor.toAnnotationReference(
  declaringClass: ClassReference.Descriptor?,
  module: ModuleDescriptor
): Descriptor {
  val annotationClass = annotationClass ?: throw Exception("Couldn't find the annotation class for $fqName")

  return Descriptor(
    annotation = this,
    classReference = annotationClass.toClassReference(module),
    declaringClass = declaringClass
  )
}

public fun AnnotationReference.argumentAt(
  name: String,
  index: Int
): AnnotationArgumentReference? {
  return arguments.singleOrNull { it.name == name }
    ?: arguments.elementAtOrNull(index)?.takeIf { it.name == null }
}

@Suppress("FunctionName")
public fun AnvilCompilationExceptionAnnotationReference(
  annotationReference: AnnotationReference,
  message: String,
  cause: Throwable? = null
): Exception = Exception(cause)

