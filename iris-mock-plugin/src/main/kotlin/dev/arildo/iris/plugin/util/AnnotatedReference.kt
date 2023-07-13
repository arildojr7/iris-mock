package dev.arildo.iris.plugin.util

import org.jetbrains.kotlin.name.FqName

/**
 * This marks any code reference that can be annotated, such as a [ClassReference] or
 * [MemberFunctionReference].
 */
public interface AnnotatedReference {
  public val annotations: List<AnnotationReference>

  public fun isAnnotatedWith(fqName: FqName): Boolean = annotations.any { it.fqName == fqName }
}
