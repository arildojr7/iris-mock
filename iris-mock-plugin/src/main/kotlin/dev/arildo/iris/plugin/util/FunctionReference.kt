package dev.arildo.iris.plugin.util

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFunction

public interface FunctionReference {  // TODO IT WAS SEALED
  public val fqName: FqName
  public val name: String get() = fqName.shortName().asString()

  public val module: AnvilModuleDescriptor

  public val parameters: List<ParameterReference>

  public fun returnTypeOrNull(): TypeReference?
  public fun returnType(): TypeReference = returnTypeOrNull()
    ?: throw AnvilCompilationExceptionFunctionReference(
      functionReference = this,
      message = "Unable to get the return type for function $fqName."
    )

  public fun visibility(): Visibility

  public interface Psi : FunctionReference { // TODO IT WAS SEALED
    public val function: KtFunction
  }

  public interface Descriptor : FunctionReference { // TODO IT WAS SEALED
    public val function: FunctionDescriptor
  }
}

@Suppress("FunctionName")
public fun AnvilCompilationExceptionFunctionReference(
  functionReference: FunctionReference,
  message: String,
  cause: Throwable? = null
) :Exception = Exception()
