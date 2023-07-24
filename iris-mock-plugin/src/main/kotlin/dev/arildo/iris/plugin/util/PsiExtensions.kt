package dev.arildo.iris.plugin.util

import dev.arildo.iris.plugin.codegen.ClassReference
import dev.arildo.iris.plugin.codegen.canResolveFqName
import dev.arildo.iris.plugin.codegen.toClassReference
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtPureElement
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf

/**
 * Adapted from https://github.com/square/anvil
 */
fun KtNamedDeclaration.requireFqName(): FqName = requireNotNull(fqName) {
    "fqName was null for $this, $nameAsSafeName"
}

fun PsiElement.ktFile(): KtFile {
    return if (this is KtPureElement) {
        containingKtFile
    } else {
        parentsWithSelf
            .filterIsInstance<KtPureElement>()
            .first()
            .containingKtFile
    }
}

fun PsiElement.requireFqName(module: ModuleDescriptor): FqName {
    val containingKtFile = ktFile()

    fun failTypeHandling(): Nothing = throw Exception("Don't know how to handle Psi element: $text")

    val classReference = when (this) {
        // If a fully qualified name is used, then we're done and don't need to do anything further.
        // An inner class reference like Abc.Inner is also considered a KtDotQualifiedExpression in
        // some cases.
        is KtDotQualifiedExpression -> {
            FqName(text).takeIf { it.canResolveFqName(module) }
                ?.let { return it }
                ?: text
        }

        is KtNameReferenceExpression -> getReferencedName()
        is KtUserType -> {
            // For a simple expression like `String` or `Lazy<Abc>` the qualifier will be null.
            // If the qualifier exists, then it may refer to the package and the referencedName refers to
            // the class name, e.g. a KtUserType "abc.def.GenericType<String>" has three children: a
            // qualifier "abc.def", the referencedName "GenericType" and the KtTypeArgumentList.
            // It's also possible for the qualifier text to be a back-tick-wrapped package such as
            // com.squareup.`impl`, so we remove any backticks since they will cause issues with resolving
            // the FqName.
            val qualifierText = qualifier?.text?.replace("`", "")
            // Prefer `referencedName` instead of just `text` even if this is just a simple name without a
            // qualifier, because `referencedName` will automatically remove any wrapping backticks, and
            // backticks must be removed before resolving with an FqName via ModuleDescriptor.
            val className = referencedName

            when {
                qualifierText != null -> {
                    // The KtUserType might be fully qualified. Try to resolve it and return early.
                    FqName("$qualifierText.$className")
                        .takeIf { it.canResolveFqName(module) }
                        ?.let { return it }

                    // If the name isn't fully qualified, then it's something like "Outer.Inner".
                    // We can't use `text` here because that includes the type parameter(s).
                    "$qualifierText.$className"
                }

                className != null -> className
                // If there are type arguments, it's a generic type.  In this case, the `text` would
                // include type parameters and can't be resolved as a name.
                children.any { it is KtTypeArgumentList } -> failTypeHandling()
                // If className is somehow null, and it's not a generic type, try resolving the text.
                else -> text
            }
        }

        is KtTypeReference -> {
            val children = children
            if (children.size == 1) {
                try {
                    // Could be a KtNullableType or KtUserType.
                    return children[0].requireFqName(module)
                } catch (e: Exception) {
                    // Fallback to the text representation.
                    text
                }
            } else if (children.size == 2 && children[0] is KtDeclarationModifierList) {
                // This could be a case of a KtUserType with a modifier like `@receiver:Qualifier`
                try {
                    return children[1].requireFqName(module)
                } catch (e: Exception) {
                    // Fallback to text
                    text
                }
            } else {
                text
            }
        }

        is KtNullableType -> return innerType?.requireFqName(module) ?: failTypeHandling()
        is KtAnnotationEntry -> return typeReference?.requireFqName(module) ?: failTypeHandling()
        is KtClassLiteralExpression -> {
            // Returns "Abc" for "Abc::class".
            val element = children.singleOrNull() ?: throw Exception(
                "Expected a single child, but there were ${children.size} instead: $text"
            )
            return element.requireFqName(module)
        }

        is KtSuperTypeListEntry -> return typeReference?.requireFqName(module) ?: failTypeHandling()
        is KtFunctionType -> {
            // KtFunctionType is a lambda. The compiler will translate lambdas to one of the function
            // interfaces. More details are available here:
            // https://github.com/JetBrains/kotlin/blob/master/spec-docs/function-types.md
            val parameterCount = parameters.size
            if (parameterCount !in 0..22) {
                throw Exception("Couldn't find function type for $parameterCount parameters.")
            }
            return FqName("kotlin.jvm.functions.Function$parameterCount")
        }

        else -> failTypeHandling()
    }

    // E.g. OuterClass.InnerClass
    val classReferenceOuter = classReference.substringBefore(".")

    val importPaths = containingKtFile.importDirectives.mapNotNull { it.importPath }

    // First look in the imports for the reference name. If the class is imported, then we know the
    // fully qualified name.
    importPaths
        .filter { it.alias == null && it.fqName.shortName().asString() == classReference }
        .also { matchingImportPaths ->
            when {
                matchingImportPaths.size == 1 ->
                    matchingImportPaths[0].fqName
                        .takeIf { it.canResolveFqName(module) }
                        ?.let { return it }

                matchingImportPaths.size > 1 ->
                    return matchingImportPaths.first { importPath ->
                        importPath.fqName.canResolveFqName(module)
                    }.fqName
            }
        }

    importPaths
        .filter { it.alias == null && it.fqName.shortName().asString() == classReferenceOuter }
        .also { matchingImportPaths ->
            when {
                matchingImportPaths.size == 1 ->
                    FqName("${matchingImportPaths[0].fqName.parent()}.$classReference")
                        .takeIf { it.canResolveFqName(module) }
                        ?.let { return it }

                matchingImportPaths.size > 1 ->
                    return matchingImportPaths.first { importPath ->
                        // Note that we must use the parent of the import FqName. An import is `com.abc.A` and
                        // the classReference `A.Inner`, so we must try to resolve `com.abc.A.Inner` and not
                        // `com.abc.A.A.Inner`.
                        importPath.fqName.parent()
                            .descendant(classReference)
                            .canResolveFqName(module)
                    }.fqName
            }
        }

    // If there's an import alias, then we know the FqName.
    importPaths
        .singleOrNull { it.alias?.asString() == classReference }
        ?.takeIf { it.fqName.canResolveFqName(module) }
        ?.let { return it.fqName }

    containingKtFile.importDirectives
        .asSequence()
        .filter { it.isAllUnder }
        .mapNotNull {
            // This fqName is everything in front of the star, e.g. for "import java.io.*" it
            // returns "java.io".
            it.importPath?.fqName
        }
        .forEach { importFqName ->
            if (importFqName.asString() == "java.util") {
                // If there's a star import for java.util.* and the import is a Collection type, then
                // the Kotlin compiler overrides these with Kotlin types.
                FqName("kotlin.collections.$classReference")
                    .takeIf { it.canResolveFqName(module) }
                    ?.let { return it }
            }

            importFqName.descendant(classReference)
                .takeIf { it.canResolveFqName(module) }
                ?.let { return it }
        }

    // If there is no import, then try to resolve the class with the same package as this file.
    containingKtFile.packageFqName.descendant(classReference)
        .takeIf { it.canResolveFqName(module) }
        ?.let { return it }

    // If the referenced type is declared within the same scope, it doesn't need to be imported.
    parents
        .filterIsInstance<KtClassOrObject>()
        .flatMap { it.collectDescendantsOfType<KtClassOrObject>() }
        .firstOrNull { it.nameAsSafeName.asString() == classReference }
        ?.let { return it.requireFqName() }

    // If this doesn't work, then maybe a class from the Kotlin package is used.
    FqName("kotlin.$classReference")
        .takeIf { it.canResolveFqName(module) }
        ?.let { return it }

    // If this doesn't work, then maybe a class from the Kotlin collection package is used.
    FqName("kotlin.collections.$classReference")
        .takeIf { it.canResolveFqName(module) }
        ?.let { return it }

    // If this doesn't work, then maybe a class from the Kotlin annotation package is used.
    FqName("kotlin.annotation.$classReference")
        .takeIf { it.canResolveFqName(module) }
        ?.let { return it }

    // If this doesn't work, then maybe a class from the Kotlin jvm package is used.
    FqName("kotlin.jvm.$classReference")
        .takeIf { it.canResolveFqName(module) }
        ?.let { return it }

    // Or java.lang.
    FqName("java.lang.$classReference")
        .takeIf { it.canResolveFqName(module) }
        ?.let { return it }

    // Check if it's an inner class in the hierarchy.
    parents.filterIsInstance<KtClassOrObject>()
        .map { FqName("${it.requireFqName()}.$classReference") }
        .firstOrNull { it.canResolveFqName(module) }
        ?.let { return it }

    // Check if it's a named import.
    containingKtFile.importDirectives
        .firstOrNull { classReference == it.importPath?.importedName?.asString() }
        ?.importedFqName
        ?.let { return it }

    // Everything else isn't supported.
    throw Exception("Couldn't resolve FqName $classReference for Psi element: $text")
}

fun PsiElement?.fq(module: ModuleDescriptor): ClassReference? {
    return this?.requireFqName(module)?.toClassReference(module)
}
