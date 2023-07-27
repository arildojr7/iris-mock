package dev.arildo.iris.plugin.codegen

import dev.arildo.iris.plugin.codegen.ClassReference.Descriptor
import dev.arildo.iris.plugin.codegen.ClassReference.Psi
import dev.arildo.iris.plugin.codegen.IrisMockModuleDescriptorImpl.ClassReferenceCacheKey.Companion.toClassReferenceCacheKey
import dev.arildo.iris.plugin.utils.requireFqName
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

/**
 * Adapted from https://github.com/square/anvil
 */
class IrisMockModuleDescriptorImpl private constructor(delegate: ModuleDescriptor) :
    IrisMockModuleDescriptor, ModuleDescriptor by delegate {

    private val ktFileToClassReferenceMap = mutableMapOf<String, List<Psi>>()
    private val allPsiClassReferences: Sequence<Psi>
        get() = ktFileToClassReferenceMap.values.asSequence().flatten()

    private val resolveDescriptorCache = mutableMapOf<FqName, ClassDescriptor?>()
    private val resolveClassIdCache = mutableMapOf<ClassId, FqName?>()
    private val classReferenceCache = mutableMapOf<ClassReferenceCacheKey, ClassReference>()

    fun addFiles(files: Collection<KtFile>) {
        files.forEach { ktFile ->
            val classReferences = ktFile.classesAndInnerClasses().map { ktClass ->
                Psi(ktClass, ktClass.toClassId(), this)
            }
            ktFileToClassReferenceMap[ktFile.identifier] = classReferences
        }
    }

    override fun getClassAndInnerClassReferences(ktFile: KtFile): List<Psi> {
        return ktFileToClassReferenceMap.getOrPut(ktFile.identifier) {
            ktFile.classesAndInnerClasses().map { getClassReference(it) }
        }
    }

    override fun resolveClassIdOrNull(classId: ClassId): FqName? =
        resolveClassIdCache.getOrPut(classId) {
            val fqName = classId.asSingleFqName()

            resolveFqNameOrNull(fqName)?.fqNameSafe
                ?: allPsiClassReferences.firstOrNull { it.fqName == fqName }?.fqName
        }

    override fun resolveFqNameOrNull(
        fqName: FqName,
        lookupLocation: LookupLocation
    ): ClassDescriptor? {
        return resolveDescriptorCache.getOrPut(fqName) {
            resolveClassByFqName(fqName, lookupLocation)
        }
    }

    override fun getClassReference(clazz: KtClassOrObject): Psi {
        return classReferenceCache.getOrPut(clazz.toClassReferenceCacheKey()) {
            Psi(clazz, clazz.toClassId(), this)
        } as Psi
    }

    override fun getClassReference(descriptor: ClassDescriptor): Descriptor {
        return classReferenceCache.getOrPut(descriptor.toClassReferenceCacheKey()) {
            val classId = descriptor.classId ?: throw Exception(
                "Couldn't find the classId for $fqNameSafe."
            )
            Descriptor(descriptor, classId, this)
        } as Descriptor
    }

    override fun getClassReferenceOrNull(fqName: FqName): ClassReference? {
        fun psiClassReference(): Psi? = allPsiClassReferences.firstOrNull { it.fqName == fqName }
        fun descriptorClassReference(): Descriptor? =
            resolveFqNameOrNull(fqName)?.let { getClassReference(it) }

        return psiClassReference() ?: descriptorClassReference()
    }

    private val KtFile.identifier: String
        get() = packageFqName.asString() + name

    internal class Factory {
        private val cache = mutableMapOf<ModuleDescriptor, IrisMockModuleDescriptorImpl>()

        fun create(delegate: ModuleDescriptor): IrisMockModuleDescriptorImpl {
            return cache.getOrPut(delegate) { IrisMockModuleDescriptorImpl(delegate) }
        }
    }

    private data class ClassReferenceCacheKey(
        private val fqName: FqName,
        private val type: Type
    ) {
        enum class Type {
            PSI,
            DESCRIPTOR
        }

        companion object {
            fun KtClassOrObject.toClassReferenceCacheKey(): ClassReferenceCacheKey =
                ClassReferenceCacheKey(requireFqName(), Type.PSI)

            fun ClassDescriptor.toClassReferenceCacheKey(): ClassReferenceCacheKey =
                ClassReferenceCacheKey(fqNameSafe, Type.DESCRIPTOR)
        }
    }
}

private fun KtFile.classesAndInnerClasses(): List<KtClassOrObject> {
    val children = findChildrenByClass(KtClassOrObject::class.java)

    return generateSequence(children.toList()) { list ->
        list.flatMap { it.declarations.filterIsInstance<KtClassOrObject>() }
            .ifEmpty { null }
    }.flatten().toList()
}

private fun KtClassOrObject.toClassId(): ClassId {
    val className = parentsWithSelf.filterIsInstance<KtClassOrObject>()
        .toList()
        .reversed()
        .joinToString(separator = ".") { it.nameAsSafeName.asString() }

    return ClassId(containingKtFile.packageFqName, FqName(className), false)
}
