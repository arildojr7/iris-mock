package dev.arildo.iris.plugin.utils

import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.symbols.impl.ConeClassLikeLookupTagImpl
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import kotlin.reflect.KClass

internal const val srcGenDirName = "src-gen-dir"
internal val srcGenDirKey = CompilerConfigurationKey.create<String>("$srcGenDirName")

internal const val IRIS_MOCK_ANNOTATION = "IrisMockInterceptor"
internal const val IRIS_MOCK_CONTAINER = "IrisMockContainer"
internal const val IRIS_MOCK_PACKAGE = "dev.arildo.iris.mock"

object Holder {
    val list = mutableListOf<String>()
}

inline fun FqName.toClassId(): ClassId = ClassId(parent(), shortName())

inline fun <reified T> classId(): ClassId = fqName<T>().toClassId()

inline fun String.toFqName(): FqName = FqName(this)

inline fun <reified T> fqName(): FqName = T::class.toFqName()

inline fun <reified T> packageFqName(): FqName = T::class.toFqName().parent()

inline fun KClass<*>.toFqName(): FqName = FqName(requireNotNull(qualifiedName))

fun ClassId.toConeType(typeArguments: Array<ConeTypeProjection>): ConeClassLikeType {
    val lookupTag = ConeClassLikeLookupTagImpl(this)
    return ConeClassLikeTypeImpl(lookupTag, typeArguments, isNullable = false)
}
