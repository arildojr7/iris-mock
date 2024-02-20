package dev.arildo.iris.plugin.visitor

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import dev.arildo.iris.plugin.util.OKHTTP_BUILDER
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.objectweb.asm.ClassVisitor

abstract class IrisMockVisitorFactory7Dot2 :
    AsmClassVisitorFactory<IrisMockVisitorFactory7Dot2.Params> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor = IrisMockVisitor(instrumentationContext.apiVersion.get(), nextClassVisitor)

    override fun isInstrumentable(classData: ClassData) = classData.className == OKHTTP_BUILDER

    interface Params : InstrumentationParameters {
        @get:Internal
        val transformEpoch: Property<Long>
    }
}
