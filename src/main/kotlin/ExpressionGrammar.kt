import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.EmptyParser
import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.h0tk3y.betterParse.parser.Parser
import com.github.h0tk3y.betterParse.utils.Tuple3
import kotlin.math.pow
import kotlin.reflect.KProperty

typealias Matrix = Array<Array<out Number>>
typealias Scalar = Number

val matrices: MutableMap<String, Matrix> =
    mutableMapOf("A" to Array(2) { Array(2) { 1 } }, "B" to Array(2) { Array(2) { 1 } })
val scalars: MutableMap<String, Scalar> = mutableMapOf("ten" to 10)

class ExpressionGrammar : Grammar<Any>() {
    //TODO insert identity matrix of any size by doing (I^x) where x is any natural number
    val plus by literalToken("+")
    val minus by literalToken("-")
    val times by literalToken("*")
    val equals by literalToken("=")
    val read by literalToken("read()")
    val lpar by literalToken("(")
    val rpar by literalToken(")")
    val transpose by regexToken("\\^[Tt]")
    val matrixToken by regexToken("[A-Z]+[A-Za-z]*")
    val scalarToken by regexToken("[a-z]+[A-Za-z]*")
    val whitespace by regexToken("(\\s|${System.lineSeparator()}|\t)+", ignore = true)

    val matrix by ((matrixToken and equals and read) use
            {
                //FIXME this still sometimes gets called twice, because the term gets parsed multiple times which shouldn't happen
                // the workaround is currently to just use the value if its there
                matrices[this.t1.text]?:run{
                    val input = readDoubleMatrix().asNumberMatrix
                    matrices[this.t1.text] = input
                    input
                }
            }) or (matrixToken use { matrices[text] ?: throw ParseException(object : ErrorResult() {}) })

    val scalar by ((scalarToken and equals and read) use {
        println("Input Scalar")
        var input: Number = readLine()?.toDoubleOrNull() ?: throw ParseException(object : ErrorResult() {})
        if (input.toString().endsWith(".0")) input = input.toInt()
        scalars[this.t1.text] = input
        input
    }) or (scalarToken use { scalars[text] ?: throw ParseException(object : ErrorResult() {}) })


    val matrixTerm: Parser<Any> by matrix or
            (skip(lpar) and parser(::rootParser) and skip(rpar))

    val term: Parser<Any> by matrixTerm or scalar or
            (skip(lpar) and parser(::rootParser) and skip(rpar))


    val transposeChain by (matrixTerm and transpose) use {
        if (this.t1 as? Matrix != null) (this.t1 as Matrix).transpose() else throw ParseException(object :
            ErrorResult() {})
    }

//    val transposeChain by leftAssociative(matrixTerm or EmptyParser, transpose) { a, op, _ ->
//        return@leftAssociative if (a as? Matrix != null) a.transpose() else throw ParseException(object :
//            ErrorResult() {})
//    }

    //FIXME or is not commutative and breaks it either way
    val mulChain by leftAssociative(transposeChain or term, times use { type }) { a, op, b ->
        return@leftAssociative if (a is Scalar && b is Scalar) {
            a * b
        } else if (a is Scalar && b !is Scalar) {
            a * ((b as? Matrix) ?: throw ParseException(object : ErrorResult() {}))
        } else if (a !is Scalar && b is Scalar) {
            ((a as? Matrix) ?: throw ParseException(object : ErrorResult() {})) * b
        } else {
            (((a as? Matrix)
                ?: throw ParseException(object : ErrorResult() {})) * ((b as? Matrix)
                ?: throw ParseException(object : ErrorResult() {}))) ?: throw ParseException(object : ErrorResult() {})
        }
    }

    val sumChain by leftAssociative(mulChain, plus) { a, op, b ->
        if (a as? Matrix != null && b as? Matrix != null) {
            return@leftAssociative ((a + b) as? Matrix) ?: throw ParseException(object : ErrorResult() {})
        } else if (a is Scalar && b is Scalar) {
            return@leftAssociative Array(1) { Array(1) { a + b } }
        } else {
            throw ParseException(object : ErrorResult() {})
        }
    }
    override val rootParser: Parser<Any> by sumChain
}


fun main(args: Array<String>) {
    val grammar = ExpressionGrammar()
    while (true) {
        println("Input Expression:")
        try {
            val result = grammar.parseToEnd(readLine() ?: "")
            if (result is Scalar) {
                println(result)
            } else {
                val matrix = (result as Matrix).toIntsIfApplicable
                matrix.printMatrix()
                println("Latex: ${matrix.asLatex()}")
            }
        }catch (e:Exception){
            System.err.println("There was a problem parsing or evaluating your input: $e")
        }
    }
}