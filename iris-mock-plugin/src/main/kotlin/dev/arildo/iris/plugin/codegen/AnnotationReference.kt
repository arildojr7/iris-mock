package dev.arildo.iris.plugin.codegen

import dev.arildo.iris.plugin.util.requireFqName
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.descriptorUtil.annotationClass

/**
 * Adapted from https://github.com/square/anvil
 * Used to create a common type between [KtAnnotationEntry] class references and
 * [AnnotationDescriptor] references, to streamline parsing.
 */
sealed class AnnotationReference {

    /**
     * Refers to the annotation class itself and not the annotated class.
     */
    abstract val classReference: ClassReference

    /**
     * Refers to the class that is annotated with this annotation reference. Note that annotations
     * can be used at different places, e.g. properties, constructors, functions, etc., therefore
     * this field must be nullable.
     */
    protected abstract val declaringClass: ClassReference?

    val fqName: FqName get() = classReference.fqName

    override fun toString(): String = "@$fqName"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnnotationReference) return false

        if (fqName != other.fqName) return false

        return true
    }

    override fun hashCode(): Int = fqName.hashCode() * 31

    class Psi internal constructor(
        val annotation: KtAnnotationEntry,
        override val classReference: ClassReference,
        override val declaringClass: ClassReference.Psi?
    ) : AnnotationReference()

    class Descriptor internal constructor(
        override val classReference: ClassReference,
        override val declaringClass: ClassReference.Descriptor?
    ) : AnnotationReference()
}

fun KtAnnotationEntry.toAnnotationReference(
    declaringClass: ClassReference.Psi?,
    module: ModuleDescriptor
): AnnotationReference.Psi {
    return AnnotationReference.Psi(
        annotation = this,
        classReference = requireFqName(module).toClassReference(module),
        declaringClass = declaringClass
    )
}

fun AnnotationDescriptor.toAnnotationReference(
    declaringClass: ClassReference.Descriptor?,
    module: ModuleDescriptor
): AnnotationReference.Descriptor {
    val annotationClass =
        annotationClass ?: throw Exception("Couldn't find the annotation class for $fqName")

    return AnnotationReference.Descriptor(
        classReference = annotationClass.toClassReference(module),
        declaringClass = declaringClass
    )
}
