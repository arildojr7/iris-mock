package dev.arildo.iris.plugin.codegen

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter

/**
 * Adapted from https://github.com/square/anvil
 * Used to create a common type between [KtClassOrObject] class references and [ClassDescriptor]
 * references, to streamline parsing.
 *
 * @see toClassReference
 */
sealed class ClassReference : Comparable<ClassReference>, AnnotatedReference {

    abstract val classId: ClassId
    abstract val fqName: FqName
    abstract val module: IrisMockModuleDescriptor
    protected abstract val innerClassesAndObjects: List<ClassReference>
    abstract fun isCompanion(): Boolean

    /**
     * Returns all outer classes including this class. Imagine the inner class `Outer.Middle.Inner`,
     * then the returned list would contain `[Outer, Middle, Inner]` in that order.
     */
    abstract fun enclosingClassesWithSelf(): List<ClassReference>

    private fun enclosingClass(): ClassReference? {
        val classes = enclosingClassesWithSelf()
        val index = classes.indexOf(this)
        return if (index == 0) null else classes[index - 1]
    }

    open fun innerClasses(): List<ClassReference> =
        innerClassesAndObjects.filterNot { it.isCompanion() }

    open fun companionObjects(): List<ClassReference> =
        innerClassesAndObjects.filter { it.isCompanion() && it.enclosingClass() == this }

    override fun toString(): String {
        return "${this::class.qualifiedName}($fqName)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassReference) return false

        if (fqName != other.fqName) return false

        return true
    }

    override fun hashCode(): Int {
        return fqName.hashCode()
    }

    override fun compareTo(other: ClassReference): Int {
        return fqName.asString().compareTo(other.fqName.asString())
    }

    class Psi(
        val clazz: KtClassOrObject,
        override val classId: ClassId,
        override val module: IrisMockModuleDescriptor
    ) : ClassReference() {
        override val fqName: FqName = classId.asSingleFqName()

        override val annotations: List<AnnotationReference.Psi>
            get() = clazz.annotationEntries.map { it.toAnnotationReference(this, module) }

        private val enclosingClassesWithSelf
            get() = clazz.parents
                .filterIsInstance<KtClassOrObject>()
                .map { it.toClassReference(module) }
                .toList()
                .reversed()
                .plus(this)

        override val innerClassesAndObjects: List<Psi>
            get() = generateSequence(clazz.declarations.filterIsInstance<KtClassOrObject>()) { classes ->
                classes
                    .flatMap { it.declarations }
                    .filterIsInstance<KtClassOrObject>()
                    .ifEmpty { null }
            }
                .flatten()
                .map { it.toClassReference(module) }
                .toList()

        override fun isCompanion(): Boolean = clazz is KtObjectDeclaration && clazz.isCompanion()

        override fun enclosingClassesWithSelf(): List<Psi> = enclosingClassesWithSelf

        @Suppress("UNCHECKED_CAST")
        override fun innerClasses(): List<Psi> =
            super.innerClasses() as List<Psi>

        @Suppress("UNCHECKED_CAST")
        override fun companionObjects(): List<Psi> =
            super.companionObjects() as List<Psi>
    }

    class Descriptor(
        private val clazz: ClassDescriptor,
        override val classId: ClassId,
        override val module: IrisMockModuleDescriptor
    ) : ClassReference() {
        override val fqName: FqName = classId.asSingleFqName()

        override val annotations: List<AnnotationReference.Descriptor>
            get() = clazz.annotations.map { it.toAnnotationReference(this, module) }

        private val enclosingClassesWithSelf
            get() = clazz.parents
                .filterIsInstance<ClassDescriptor>()
                .map { it.toClassReference(module) }
                .toList()
                .reversed()
                .plus(this)

        override val innerClassesAndObjects: List<Descriptor>
            get() = clazz.unsubstitutedMemberScope
                .getContributedDescriptors(kindFilter = DescriptorKindFilter.CLASSIFIERS)
                .filterIsInstance<ClassDescriptor>()
                .map { it.toClassReference(module) }

        override fun isCompanion(): Boolean = DescriptorUtils.isCompanionObject(clazz)

        override fun enclosingClassesWithSelf(): List<Descriptor> = enclosingClassesWithSelf

        @Suppress("UNCHECKED_CAST")
        override fun innerClasses(): List<Descriptor> =
            super.innerClasses() as List<Descriptor>

        @Suppress("UNCHECKED_CAST")
        override fun companionObjects(): List<Descriptor> =
            super.companionObjects() as List<Descriptor>
    }
}

fun ClassDescriptor.toClassReference(module: ModuleDescriptor): ClassReference.Descriptor =
    module.asIrisMockModuleDescriptor().getClassReference(this)

fun KtClassOrObject.toClassReference(module: ModuleDescriptor): ClassReference.Psi =
    module.asIrisMockModuleDescriptor().getClassReference(this)

/**
 * Attempts to find the [KtClassOrObject] for the [FqName] first, then falls back to the
 * [ClassDescriptor] if the Psi element cannot be found. This will happen if the class for
 * [FqName] is not part of this compilation unit.
 */
fun FqName.toClassReferenceOrNull(module: ModuleDescriptor): ClassReference? =
    module.asIrisMockModuleDescriptor().getClassReferenceOrNull(this)

fun FqName.toClassReference(module: ModuleDescriptor): ClassReference {
    return toClassReferenceOrNull(module)
        ?: throw Exception("Couldn't resolve ClassReference for $this.")
}
