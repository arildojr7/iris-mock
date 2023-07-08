package dev.arildo.iris.plugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.WARNING
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind.CLASS
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.declarations.IrAnonymousInitializer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrPackageFragment
import org.jetbrains.kotlin.ir.declarations.addMember
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrAnonymousInitializerSymbolImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.addSimpleDelegatingConstructor
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.createImplicitParameterDeclarationWithWrappedDescriptor
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.invokeFun
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.library.metadata.KlibMetadataProtoBuf.className
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

                    val implClassSymbol = pluginContext.referenceClass(
                        ClassId(
                            FqName("dev.arildo.iris.sample"),
                            Name.identifier("FirstInterceptor")
                        )
                    )

                    val newConstructor = implClassSymbol!!.constructors.first()

                    //Create the constructor call for _ExampleApiImpl()
                    val newCall = IrConstructorCallImpl(
                        0,
                        0,
                        type = implClassSymbol.defaultType,
                        symbol = newConstructor,
                        0,
                        0,
                        0,
                        null
                    )

                    //Set _ExampleApiImpl() as argument for create<ExampleApi>()
                    irCall.putValueArgument(0, newCall)


                } else {
                    super.visitClass(declaration, data)
                }
                super.visitClass(declaration, data)
            }
        }, classes)

        moduleFragment.transformChildrenVoid(object : IrElementTransformerVoidWithContext() {

            override fun visitClassNew(declaration: IrClass): IrStatement {
                return super.visitClassNew(declaration)
            }
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

    @OptIn(ExperimentalContracts::class, ObsoleteDescriptorBasedAPI::class)
    public inline fun IrClass.addAnonymousInitializer(builder: IrAnonymousInitializer.() -> Unit): IrAnonymousInitializer {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return this.factory.createAnonymousInitializer(
            UNDEFINED_OFFSET,
            UNDEFINED_OFFSET,
            IrDeclarationOrigin.DEFINED,
            IrAnonymousInitializerSymbolImpl(this.descriptor)
        ).apply(builder).also {
            this.addMember(it)
            it.parent = this
        }
    }

    private val FqName?.packageName: String
        get() {
            return this.toString().substringBeforeLast(".")
        }
}
