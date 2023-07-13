package dev.arildo.iris.plugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.WARNING
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind.CLASS
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrPackageFragment
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.addSimpleDelegatingConstructor
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.createImplicitParameterDeclarationWithWrappedDescriptor
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class GenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {

    private val annotatedClasses = mutableListOf<String>()
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext
    ) {
        messageCollector.report(WARNING, "@@@ " + moduleFragment.files.joinToString { it.name })
        val classes = mutableListOf<IrClass>()

        moduleFragment.acceptChildren(object : IrElementVisitor<Unit, MutableList<IrClass>> {
            override fun visitElement(
                element: IrElement,
                data: MutableList<IrClass>
            ) {
                element.acceptChildren(this, data)
            }

            override fun visitClass(
                declaration: IrClass,
                data: MutableList<IrClass>
            ) {
                if (declaration.hasAnnotation(FqName("dev.arildo.iris.mock.annotation.IrisMockInterceptor"))) {
                    data += declaration
                    annotatedClasses.add(declaration.kotlinFqName.asString())
                } else {
                    super.visitClass(declaration, data)
                }
                super.visitClass(declaration, data)
            }
        }, classes)

        moduleFragment.transformChildrenVoid(object : IrElementTransformerVoidWithContext() {

            override fun visitPackageFragment(declaration: IrPackageFragment): IrPackageFragment {
                messageCollector.report(WARNING, "@@@ visitPackageFragment")

                classes
                    .filter { it.getPackageFragment() == declaration }
                    .forEach {
                        messageCollector.report(WARNING, "@@@ ${it.kotlinFqName.asString()}")

                        val irClass = pluginContext.irFactory
                            .buildClass {
                                name = Name.identifier("Testando")
                                kind = CLASS
                            }
                            .apply {
                                this.parent = declaration
                                createImplicitParameterDeclarationWithWrappedDescriptor()

                                val constructorOfAny =
                                    pluginContext.irBuiltIns.anyClass.owner.constructors.first()
                                addSimpleDelegatingConstructor(
                                    constructorOfAny,
                                    pluginContext.irBuiltIns,
                                    isPrimary = true
                                )
                            }
                        declaration.addChild(irClass)
                    }

                return declaration
            }
        })
    }
}
