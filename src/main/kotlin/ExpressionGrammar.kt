import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.TokenMatchesSequence
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.*

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
    val identity by regexToken("Id(\\s)*((\\s)*(\\d)+)+")
    val number by regexToken("\\d+")
    val transpose by regexToken("\\^[Tt]")
    val matrixToken by regexToken("[A-Z]+[A-Za-z]*")
    val scalarToken by regexToken("[a-z]+[A-Za-z]*")
    val whitespace by regexToken("(\\s|${System.lineSeparator()}|\t)+", ignore = true)

    val matrix by (identity use {
        val dimension = this.text.replace(" ", "").substring(2).toInt()
        Array(dimension) { indexRow ->
            Array(dimension) { indexColum ->
                if (indexRow == indexColum) 1 else 0
            }
        }
    }) or ((matrixToken and skip(equals) and skip(read)) use
            {
                //FIXME this still sometimes gets called twice, because the term gets parsed multiple times which shouldn't happen
                // the workaround is currently to just use the value if its there
                matrices[this.text] ?: run {
                    val input = readDoubleMatrix().asNumberMatrix
                    matrices[this.text] = input
                    input
                }
            }) or (matrixToken use { matrices[text] ?: throw ParseException(object : ErrorResult() {}) })

    val scalar by ((scalarToken and skip(equals) and skip(read)) use {
        println("Input Scalar")
        var input: Number = readLine()?.toDoubleOrNull() ?: throw ParseException(object : ErrorResult() {})
        if (input.toString().endsWith(".0")) input = input.toInt()
        scalars[this.text] = input
        input
    }) or (scalarToken use { scalars[text] ?: throw ParseException(object : ErrorResult() {}) }) or (
            number use {
                if (this.text.toDouble().toString().endsWith(".0")) this.text.toInt() else this.text.toDouble()
            }
            )

    val matrixTerm by matrix


    val term: Parser<Any> by matrixTerm or scalar or
            (skip(lpar) and parser(::rootParser) and skip(rpar))


    val transposeChain by ((matrixTerm and zeroOrMore(transpose))) use {
        if (this.t1 as? Matrix != null) {
            var transposed: Matrix = this.t1 as Matrix;
            repeat(this.t2.size) { transposed = transposed.transposed() }
            transposed
        } else {
            UnexpectedEof(expected = matrixToken)
        }
    }

    //FIXME or is not commutative and breaks it either way
    val mulChain: Parser<Any> by leftAssociative((transposeChain or term), times use { type }) { a, op, b ->
        return@leftAssociative if (a is Scalar && b is Scalar) {
            a * b
        } else if (a is Scalar && b !is Scalar) {
            a * ((b as? Matrix) ?: return@leftAssociative UnexpectedEof(matrixToken))
        } else if (a !is Scalar && b is Scalar) {
            ((a as? Matrix) ?: return@leftAssociative UnexpectedEof(matrixToken)) * b
        } else {
            if ((a as? Matrix) != null && (b as? Matrix) != null) {
                (a * b) ?: throw Exception("Matrices have the wrong dimensions: a: $a; b:$b")
            } else {
                UnexpectedEof(matrixToken)
            }
        }
    }

    val sumChain by leftAssociative(mulChain, plus) { a, op, b ->
        return@leftAssociative if (a as? Matrix != null && b as? Matrix != null) {
            ((a + b) as? Matrix) ?:  UnexpectedEof(matrixToken)
        } else if (a is Scalar && b is Scalar) {
            Array(1) { Array(1) { a + b } }
        } else {
            UnexpectedEof(matrixToken)
        }
    }
    override val rootParser: Parser<Any> by sumChain
}


fun main(args: Array<String>) {
    val grammar = ExpressionGrammar()
    while (true) {
        println("Input Expression:")
        lateinit var tokensForDebugging: TokenMatchesSequence
        try {
            val input = readLine() ?: ""
            tokensForDebugging = grammar.tokenizer.tokenize(input)
            val result = grammar.parseToEnd(tokensForDebugging)
            if (result is Scalar) {
                println(result)
            } else {
                val matrix = (result as Matrix).toIntsIfApplicable
                matrix.printMatrix()
                println("Latex: ${matrix.asLatex()}")
            }
        } catch (e: Exception) {
            System.err.println("There was a problem parsing or evaluating your input: $e")
            e.printStackTrace()
            System.err.println("These were the tokens that were read: ")
            tokensForDebugging.forEachIndexed { index, tokenMatch -> println("Token $index: $tokenMatch\t") }
            println()
        }
    }
}