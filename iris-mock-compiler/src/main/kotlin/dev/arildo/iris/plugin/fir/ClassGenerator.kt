package dev.arildo.iris.plugin.fir

import dev.arildo.iris.plugin.IrisIrGenerationExtension
import dev.arildo.iris.plugin.utils.toClassId
import dev.arildo.iris.plugin.utils.toConeType
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.jvm.ir.IrArrayBuilder
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.runtime.structure.classId
import org.jetbrains.kotlin.fir.FirImplementationDetail
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.MutableOrEmptyList
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createTopLevelClass
import org.jetbrains.kotlin.fir.symbols.impl.ConeClassLikeLookupTagImpl
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.fir.types.impl.FirResolvedTypeRefImpl
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.*
import kotlin.reflect.KClass

/*
 * Generates top level class
 *
 * public final class foo.bar.MyClass {
 *     fun foo(): String = "Hello world"
 * }
 */
class ClassGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
    companion object {
        val MY_CLASS_ID = ClassId(FqName.fromSegments(listOf("dev", "arildo", "iris", "mock")), Name.identifier("IrisMockContainer"))

        val FOO_ID = CallableId(MY_CLASS_ID, Name.identifier("foo"))
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
        if (classId != MY_CLASS_ID) return null
        val klass = createTopLevelClass(MY_CLASS_ID, Key, ClassKind.CLASS)
        return klass.symbol
    }


    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val classId = context.owner.classId
        require(classId == MY_CLASS_ID)
        val constructor = createConstructor(context.owner, Key, /*generateDelegatedNoArgConstructorCall = true*/)
        return listOf(constructor.symbol)
    }

    @OptIn(FirImplementationDetail::class)
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val owner = context?.owner ?: return emptyList()
//        val type = FqName("okhttp3.Interceptor").toClassId().toConeType(emptyArray())

        val type = FqName("okhttp3.Interceptor").toClassId().toConeType(emptyArray())



//        val function2 = buildSimpleFunction {
//            this.name = Name.identifier("myCustomFunction")
//            this.returnTypeRef = FirResolvedTypeRefImpl(null, MutableOrEmptyList.empty(), type, null)
//
//        }

        val function = createMemberFunction(owner, Key, callableId.callableName, returnType = type)
        return listOf(function.symbol)
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        return if (classSymbol.classId == MY_CLASS_ID) {
            setOf(FOO_ID.callableName, SpecialNames.INIT)
        } else {
            emptySet()
        }
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelClassIds(): Set<ClassId> {
        return setOf(MY_CLASS_ID)
    }

    override fun hasPackage(packageFqName: FqName): Boolean {
        return packageFqName == MY_CLASS_ID.packageFqName
    }

    object Key : GeneratedDeclarationKey()
}
class SimplePluginRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::ClassGenerator
    }
}