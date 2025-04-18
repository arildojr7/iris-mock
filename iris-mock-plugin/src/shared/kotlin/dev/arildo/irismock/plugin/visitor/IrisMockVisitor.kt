package dev.arildo.irismock.plugin.visitor

import dev.arildo.irismock.plugin.util.ADD
import dev.arildo.irismock.plugin.util.BUILD
import dev.arildo.irismock.plugin.util.CONSTRUCTOR
import dev.arildo.irismock.plugin.util.INIT
import dev.arildo.irismock.plugin.util.INTERCEPTORS
import dev.arildo.irismock.plugin.util.IRIS_WRAPPER_INTERCEPTOR
import dev.arildo.irismock.plugin.util.LIST_DESCRIPTOR
import dev.arildo.irismock.plugin.util.LIST_OWNER
import dev.arildo.irismock.plugin.util.OBJECT
import dev.arildo.irismock.plugin.util.OKHTTP_BUILDER_DESCRIPTOR
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class IrisMockVisitor(apiVersion: Int, next: ClassVisitor) : ClassVisitor(apiVersion, next) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (name == BUILD) {
            return IrisMockMethodAdapter(
                super.visitMethod(
                    access,
                    name,
                    descriptor,
                    signature,
                    exceptions
                )
            )
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    inner class IrisMockMethodAdapter(originalVisitor: MethodVisitor) :
        MethodVisitor(api, originalVisitor) {

        override fun visitCode() {
            visitVarInsn(Opcodes.ALOAD, 0)

            visitFieldInsn(
                Opcodes.GETFIELD,
                OKHTTP_BUILDER_DESCRIPTOR,
                INTERCEPTORS,
                LIST_DESCRIPTOR
            )

            visitTypeInsn(Opcodes.NEW, IRIS_WRAPPER_INTERCEPTOR)
            visitInsn(Opcodes.DUP)

            visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                IRIS_WRAPPER_INTERCEPTOR,
                INIT,
                CONSTRUCTOR,
                false
            )

            visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                LIST_OWNER,
                ADD,
                OBJECT,
                true
            )
        }
    }
}
