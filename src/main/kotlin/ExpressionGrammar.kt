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
                println(this)
                val input = readDoubleMatrix().asNumberMatrix
                matrices[this.t1.text] = input
                input
            }) or (matrixToken use { matrices[text] ?: throw ParseException(object : ErrorResult() {}) })

    val scalar by ((scalarToken and equals and read) use {
        println("Input Scalar")
        var input: Number = readLine()?.toDoubleOrNull() ?: throw ParseException(object : ErrorResult() {})
        if (input.toString().endsWith(".0")) input = input.toInt()
        scalars[this.t1.text] = input
        input
    }) or (scalarToken use { scalars[text] ?: throw ParseException(object : ErrorResult() {}) })


    val matrixTerm: Parser<Matrix> by matrix or
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

    val mulChain by leftAssociative(term or transposeChain, times use { type }) { a, op, b ->
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

    val sumChain: Parser<Matrix> by leftAssociative(mulChain, plus) { a, op, b ->
        if (a as? Matrix != null && b as? Matrix != null) {
            return@leftAssociative ((a + b) as? Matrix) ?: throw ParseException(object : ErrorResult() {})
        } else if (a is Scalar && b is Scalar) {
            return@leftAssociative Array(1) { Array(1) { a + b } }
        } else {
            throw ParseException(object : ErrorResult() {})
        }
    } as Parser<Matrix>
    override val rootParser: Parser<Matrix> by sumChain
}


fun main(args: Array<String>) {
    val grammar = ExpressionGrammar()
    while (true) {
        println("Input Expression:")
        val result = grammar.parseToEnd(readLine() ?: "")
        if (result is Scalar) {
            println(result)
        } else {
            (result as Matrix).printMatrix()
        }
    }
}