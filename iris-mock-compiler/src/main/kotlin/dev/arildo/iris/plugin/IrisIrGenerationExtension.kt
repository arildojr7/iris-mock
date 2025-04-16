package dev.arildo.iris.plugin

import dev.arildo.iris.plugin.codegen.CodeGenerator
import dev.arildo.iris.plugin.codegen.irisMockContainer
import dev.arildo.iris.plugin.utils.IRIS_MOCK_CONTAINER
import dev.arildo.iris.plugin.utils.IRIS_MOCK_PACKAGE
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.LineAndColumn
import org.jetbrains.kotlin.ir.SourceRangeInfo
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.addConstructor
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrFactoryImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrFileImpl
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrClassSymbolImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrFileSymbolImpl
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.createImplicitParameterDeclarationWithWrappedDescriptor
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.packageFqName
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addToStdlib.shouldNotBeCalled
import java.io.File

val ANNOTATION_FQN = FqName("dev.arildo.iris.mock.annotation.IrisMockInterceptor")

val IDENTITY_FUNCTION_NAME = Name.identifier("identity")

class IrisIrGenerationExtension(
    private val codeGenDir: File,
    private val codeGenerator: CodeGenerator?
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        createIrFile(moduleFragment,pluginContext, "GeneratedClass")

        val generatedDir = File(codeGenDir, "generated/source/iris-mock/$IRIS_MOCK_PACKAGE")
        generatedDir.mkdirs()

        val generatedFile = File(generatedDir, "$IRIS_MOCK_CONTAINER.kt")

        val classes = findAnnotatedClasses(moduleFragment)


        val dsd = irisMockContainer(classes.map { it.packageFqName?.asString() + "." + it.name.asString() })
        generatedFile.writeText(dsd)
    }

    private fun findAnnotatedClasses(moduleFragment: IrModuleFragment): List<IrClass> {
        val classes = mutableListOf<IrClass>()
        moduleFragment.acceptVoid(object : IrElementVisitorVoid {
            override fun visitElement(element: IrElement) = element.acceptChildrenVoid(this)

            override fun visitClass(declaration: IrClass) {
                println("@@@ ${declaration.name.asString()}")
                if (declaration.hasAnnotation(ANNOTATION_FQN)) {
                    classes += declaration
                }
                super.visitClass(declaration)
            }
        })
        return classes
    }

    fun createIrFile(module: IrModuleFragment, pluginContext: IrPluginContext, fileName: String) {

        val fakeFile = ErrorFile
//
        // Create the class with a function that returns a string
        val irClass = IrErrorClassImpl(fakeFile)
        val irFunction = createIrFunction(pluginContext)

        // Add function to class and class to file
        irClass.declarations.add(irFunction)
        fakeFile.declarations.add(irClass)

        // Create function body (returning a string)
        irFunction.body = createFunctionBody(pluginContext, irFunction)
//
    }

    private val ErrorFile = IrFileImpl(
        fileEntry = object : IrFileEntry {
            override val name: String = "<error-class>"
            override val maxOffset: Int
                get() = shouldNotBeCalled()

            override fun getSourceRangeInfo(beginOffset: Int, endOffset: Int): SourceRangeInfo = shouldNotBeCalled()
            override fun getLineNumber(offset: Int): Int = shouldNotBeCalled()
            override fun getColumnNumber(offset: Int): Int = shouldNotBeCalled()
            override fun getLineAndColumnNumbers(offset: Int): LineAndColumn = shouldNotBeCalled()
        },
        symbol = IrFileSymbolImpl(),
        packageFqName = FqName("<error-package>"),
    )


    fun IrErrorClassImpl(file: IrFileImpl): IrClass = IrFactoryImpl.createClass(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        origin = IrDeclarationOrigin.ERROR_CLASS,
        symbol = IrClassSymbolImpl(),
        name = Name.special("<error>"),
        kind = ClassKind.CLASS,
        visibility = DescriptorVisibilities.DEFAULT_VISIBILITY,
        modality = Modality.FINAL,
        source = SourceElement.NO_SOURCE,
    ).apply {
        parent = this
        createImplicitParameterDeclarationWithWrappedDescriptor()

        // Primary constructor is needed so that we could create annotations with error types in KAPT3+K2.
        // (In KAPT3+K1, error class is created based on ErrorClassDescriptor, which has a primary constructor.)
        addConstructor {
            startOffset = SYNTHETIC_OFFSET
            endOffset = SYNTHETIC_OFFSET
            visibility = DescriptorVisibilities.INTERNAL
            isPrimary = true
        }
    }

    fun createIrClass(pluginContext: IrPluginContext): IrClass {
        // Create a new class declaration
        val classSymbol = pluginContext.irFactory.buildClass {
            this.name = Name.identifier("GeneratedClass") // Specify the class name
            this.visibility = DescriptorVisibilities.PUBLIC
            origin = IrDeclarationOrigin.DEFINED
            name = Name.identifier("")
            visibility = DescriptorVisibilities.PUBLIC
            kind = ClassKind.CLASS
            modality = Modality.FINAL
        }

        // Build the class
//        val irClass = pluginContext.irFactory.createClass(
//            startOffset = UNDEFINED_OFFSET,
//            endOffset = UNDEFINED_OFFSET,
//            origin = IrDeclarationOrigin.DEFINED,
//            name = Name.identifier(""),
//            visibility = DescriptorVisibilities.PUBLIC,
//            symbol = classSymbol.symbol,
//            kind = ClassKind.CLASS,
//            modality = Modality.FINAL
//        )
        return classSymbol
    }

    fun createIrFunction(pluginContext: IrPluginContext): IrFunction {
        // Create a new function declaration
        val functionSymbol = pluginContext.irFactory.buildFun {
            this.name = Name.identifier("generatedFunction")
            this.returnType = pluginContext.irBuiltIns.stringType
            this.visibility = DescriptorVisibilities.PUBLIC
        }

//        // Create the function IR
//        val irFunction = IrFunctionImpl(
//            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
//            IrDeclarationOrigin.DEFINED,
//            functionSymbol
//        )

        return functionSymbol
    }

    fun createFunctionBody(pluginContext: IrPluginContext, irFunction: IrFunction): IrBlockBody {
        // Create the function body with a return statement
        val returnExpression = IrConstImpl.string(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET, pluginContext.irBuiltIns.stringType, "Hello World"
        )

        // Create the return statement
        val returnStatement = IrReturnImpl(
            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
            irFunction.returnType,
            irFunction.symbol,
            returnExpression
        )

        // Return the function body
        return pluginContext.irFactory.createBlockBody(UNDEFINED_OFFSET, UNDEFINED_OFFSET, listOf(returnStatement))
    }
}