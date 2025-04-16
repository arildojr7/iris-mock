package dev.arildo.iris.plugin.ir

import dev.arildo.iris.plugin.fir.ClassGenerator
import dev.arildo.iris.plugin.utils.Holder
import dev.arildo.iris.plugin.utils.classId
import dev.arildo.iris.plugin.utils.toClassId
import dev.arildo.iris.plugin.utils.toConeType
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.fir.backend.toIrType
import org.jetbrains.kotlin.fir.types.toSymbol
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBlockBody

import org.jetbrains.kotlin.ir.builders.IrStatementsBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.declarations.buildProperty
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFactory
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.createBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstantArrayImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.IdSignature
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.irCall
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.callableIdForConstructor

class SimpleIrBodyGenerator(private val pluginContext: IrPluginContext) :
    AbstractTransformerForGenerator(pluginContext) {
    override fun interestedIn(key: GeneratedDeclarationKey?): Boolean {
        return key == ClassGenerator.Key
    }

    override fun generateBodyForFunction(
        function: IrSimpleFunction,
        key: GeneratedDeclarationKey?
    ): IrBody {
        require(function.name == ClassGenerator.FOO_ID.callableName)
        val type = FqName("okhttp3.Interceptor").toClassId().toConeType(emptyArray())

        val const = IrConstImpl(-1, -1, function.returnType, IrConstKind.String, value = "")
        val returnStatement = IrReturnImpl(-1, -1, irBuiltIns.anyType, function.symbol, const)
        val irBuilder = IrBlockBodyBuilder(
            pluginContext, Scope(function.symbol), function.startOffset,
            function.endOffset,
        )

//        return irBuilder.createListIrExpression(pluginContext)
        return irFactory.createBlockBody(-1, -1, listOf(returnStatement))
    }

    fun IrBlockBodyBuilder.createListElement(value: Int): IrExpression {
        return irInt(value)
    }

    fun IrBlockBodyBuilder.createListIrExpression(pluginContext: IrPluginContext): IrBlockBody {
        val listSymbol = pluginContext.getListSymbol()!!
        val arrayListConstructor = pluginContext.getArrayListConstructorSymbol()
        val mutableListAdd = pluginContext.getMutableListAddSymbol()!!
        val intType = pluginContext.irBuiltIns.intType
        val listType = listSymbol.typeWith(intType)

        return irBlockBody {
            val arrayList = irCall(arrayListConstructor, listType)
            +arrayList

            val elements = listOf(
                createListElement(1),
                createListElement(2),
                createListElement(3)
            )

            for (element in elements) {
                +irCall(mutableListAdd).apply {
                    dispatchReceiver = arrayList
                    putValueArgument(0, element)
                }
            }
        }
    }

    fun IrPluginContext.getListSymbol(): IrClassSymbol? {
        val classId = ClassId.fromString("kotlin/collections/List")
        return referenceClass(classId)
    }

    fun IrPluginContext.getArrayListSymbol(): IrClassSymbol {
        val classId = ClassId.fromString("kotlin/collections/ArrayList")
        val symbol = referenceClass(classId)
        if (symbol != null) {
            return symbol;
        }

        return irBuiltIns.mutableListClass
    }

//    fun IrPluginContext.getArrayListConstructorSymbol(): IrFunctionSymbol {
//        val arrayListSymbol = getArrayListSymbol()
//        return arrayListSymbol.constructors.first()
//    }

    fun IrPluginContext.getArrayListConstructorSymbol(): IrConstructorSymbol {
        val arrayListClassId = ClassId.fromString("kotlin/collections/ArrayList")

        // Create the IdSignature for the default constructor
        val signature = IdSignature.CommonSignature(
            arrayListClassId.packageFqName.asString(),
            arrayListClassId.asSingleFqName().asString(),
            null, // id: Long?, usually null for constructors
            0L, // mask: Long, 0 for default constructor
            null // description: String?, can be null
        )
        return symbolTable.referenceConstructor(signature)
    }

    fun IrPluginContext.getMutableListAddSymbol(): IrFunctionSymbol? {
        val mutableListSymbol = irBuiltIns.mutableListClass
        if (mutableListSymbol == null) return null

        return mutableListSymbol.functions.firstOrNull {
            val function = it.owner as? IrSimpleFunction
            function?.name?.asString() == "add"
        }
    }

//    fun IrBuilderWithScope.createListIrExpression(pluginContext: IrPluginContext): IrExpression? {
//        val elements = listOf(
//            irInt(1),
//            irInt(2),
//            irInt(3)
//        )
//        return createListOfCall(pluginContext, elements)
//    }
//
//    fun IrBuilderWithScope.createListOfCall(pluginContext: IrPluginContext, elements: List<IrExpression>): IrExpression? {
//        val listOfFunction = pluginContext.getListOfFunctionSymbol() ?: return null
//        return irCall(listOfFunction).apply {
//            elements.forEachIndexed { index, irExpression ->
//                putValueArgument(index, irExpression)
//            }
//        }
//    }
//
//    fun IrPluginContext.getListSymbol(): IrClassSymbol? {
//        val classId = ClassId.fromString("kotlin/collections/List")
//        return referenceClass(classId)
//    }
//
//    fun IrPluginContext.getListOfFunctionSymbol(): IrFunctionSymbol? {
//        val functionId = ClassId.fromString("kotlin/collections/listOf").callableIdForConstructor()
//        val functions = referenceFunctions(functionId)
//        return functions.firstOrNull()
//    }

    override fun generateBodyForConstructor(
        constructor: IrConstructor,
        key: GeneratedDeclarationKey?
    ): IrBody? {
        return generateBodyForDefaultConstructor(constructor)
    }
}

