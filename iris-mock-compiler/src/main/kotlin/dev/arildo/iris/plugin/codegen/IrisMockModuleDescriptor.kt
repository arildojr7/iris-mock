package dev.arildo.iris.plugin.codegen

import dev.arildo.iris.plugin.codegen.ClassReference.Descriptor
import dev.arildo.iris.plugin.codegen.ClassReference.Psi
import dev.arildo.iris.plugin.utils.classIdBestGuess
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.NoLookupLocation.FROM_BACKEND
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

/**
 * Adapted from https://github.com/square/anvil
 */
interface IrisMockModuleDescriptor : ModuleDescriptor {
    fun resolveClassIdOrNull(classId: ClassId): FqName?

    fun resolveFqNameOrNull(
        fqName: FqName,
        lookupLocation: LookupLocation = FROM_BACKEND
    ): ClassDescriptor?

    fun getClassAndInnerClassReferences(ktFile: KtFile): List<Psi>

    fun getClassReference(clazz: KtClassOrObject): Psi

    fun getClassReference(descriptor: ClassDescriptor): Descriptor

    /**
     * Attempts to find the [KtClassOrObject] for the [FqName] first, then falls back to the
     * [ClassDescriptor] if the Psi element cannot be found. This will happen if the class for
     * [FqName] is not part of this compilation unit.
     */
    fun getClassReferenceOrNull(fqName: FqName): ClassReference?
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun ModuleDescriptor.asIrisMockModuleDescriptor(): IrisMockModuleDescriptor =
    this as IrisMockModuleDescriptor

fun FqName.canResolveFqName(module: ModuleDescriptor) = module.asIrisMockModuleDescriptor()
    .resolveClassIdOrNull(classIdBestGuess()) != null

fun Collection<KtFile>.classAndInnerClassReferences(module: ModuleDescriptor) = asSequence()
    .flatMap { module.asIrisMockModuleDescriptor().getClassAndInnerClassReferences(it) }
