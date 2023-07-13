package dev.arildo.iris.plugin.util

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration

public interface PropertyReference { // TODO IT WAS SEALED

  public val fqName: FqName

  public val module: AnvilModuleDescriptor

  public val name: String

  public val setterAnnotations: List<AnnotationReference>
  public val getterAnnotations: List<AnnotationReference>

  public fun visibility(): Visibility
  public fun isLateinit(): Boolean

  public fun typeOrNull(): TypeReference?
  public fun type(): TypeReference = typeOrNull()
    ?: throw AnvilCompilationExceptionPropertyReference(
      propertyReference = this,
      message = "Unable to get type for property $fqName."
    )

  public interface Psi : PropertyReference { // TODO IT WAS SEALED
    public val property: KtCallableDeclaration
  }

  public interface Descriptor : PropertyReference { // TODO IT WAS SEALED
    public val property: PropertyDescriptor
  }
}

@Suppress("FunctionName")
public fun AnvilCompilationExceptionPropertyReference(
  propertyReference: PropertyReference,
  message: String,
  cause: Throwable? = null
): Exception = Exception()

