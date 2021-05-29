import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.TokenMatchesSequence
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.*
import com.whitemagicsoftware.tex.DefaultTeXFont
import com.whitemagicsoftware.tex.TeXEnvironment
import com.whitemagicsoftware.tex.TeXFormula
import com.whitemagicsoftware.tex.TeXLayout
import com.whitemagicsoftware.tex.graphics.SvgGraphics2D
import java.awt.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants


typealias Scalar = Number

val matrices: MutableMap<String, Matrix> =
    mutableMapOf(
        "A" to Matrix(Array(2) { Array(2) { 1 } }),
        "B" to Matrix(Array(2) { Array(2) { 1 } })
    )
val scalars: MutableMap<String, Scalar> = mutableMapOf("ten" to 10)

class ExpressionGrammar : Grammar<Any>() {
    val number by regexToken("-?\\d+(\\.\\d+)?")
    val plus by literalToken("+")
    val minus by literalToken("-")
    val times by literalToken("*")
    val div by literalToken("/")
    val equals by literalToken("=")
    val read by literalToken("read()")
    val lpar by literalToken("(")
    val rpar by literalToken(")")
    val identity by regexToken("Id((\\s)*(\\d)+)+")
    val transpose by regexToken("\\^[Tt]")
    val power by regexToken("\\^((\\s)*(\\d)+)+(\\.((\\s)*(\\d)+)+)?")
    val matrixToken by regexToken("[A-Z]+[A-Za-z]*")
    val scalarToken by regexToken("[a-z]+[A-Za-z]*")
    val whitespace by regexToken("(\\s|${System.lineSeparator()}|\t)+", ignore = true)

    val matrix by (identity use {
        val dimension = this.text.replace(" ", "").substring(2).toInt()
        Matrix.getIdentityMatrix(dimension)
    }) or ((matrixToken and skip(equals) and skip(read)) use
            {
                //FIXME this still sometimes gets called twice, because the term gets parsed multiple times which shouldn't happen
                // the workaround is currently to just use the value if its there
                matrices[this.text] ?: run {
                    val input = Matrix.readMatrix()
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


    val term: Parser<Any> by parser(::matrixAssignment) or matrix or
            (skip(lpar) and parser(::rootParser) and skip(rpar))

    val matrixAssignment by (matrixToken and skip(equals)).and<TokenMatch, Any>(parser(::rootParser)) use {
        matrices[t1.text] = t2 as Matrix
        t2 as Matrix
    }
    val scalarAssignment by (scalarToken and skip(equals)).and<TokenMatch, Any>(parser(::rootParser)) use {
        scalars[t1.text] = t2 as Scalar
        t2 as Scalar
    }
    val scalarTerm by scalarAssignment or term or scalar

    val powerChain by (scalarTerm and zeroOrMore(power)) use {
        if (t2.isEmpty()) {
            t1
        } else {
            val powerzzzzz = t2.map { tokenMatch -> tokenMatch.text.substring(1).replace(" ", "") }
                .map { string ->
                    if (string.toInt().toDouble() == string.toDouble()) {
                        string.toInt()
                    } else {
                        string.toDouble()
                    }
                }
            when (t1) {
                is Scalar -> {
                    var result = t1 as Scalar
                    powerzzzzz.forEach {
                        result = result pow it
                    }
                    result
                }
                is Matrix -> {
                    var result: Matrix? = t1 as Matrix
                    powerzzzzz.forEach {
                        result = result?.pow(it.toInt())
                    }
                    result ?: throw  Exception("Matrix has the wrong dimensions (is not square: $t1")
                }
                else -> {
                    UnexpectedEof(matrixToken)
                }
            }
        }
    }

    val transposeChain by ((powerChain and zeroOrMore(transpose))) map {
        if (it.t1 is Scalar) {
            it.t1
        } else {
            var transposed: Matrix = it.t1 as Matrix
            repeat(it.t2.size) { transposed = transposed.transposed() }
            transposed
        }
    }

    val mulChain: Parser<Any> by leftAssociative(
        (transposeChain or scalarTerm),
        (times or div) use { type }) { a, op, b ->
        return@leftAssociative if (a is Scalar && b is Scalar) {
            if (op == times) {
                (a * b)
            } else {
                if (a.toDouble() / b == (a / b).toDouble()) {
                    a / b
                } else {
                    ((a.toDouble()) / b)
                }
            }
        } else if (a is Scalar && b is Matrix) {
            b * a
        } else if (a is Matrix && b is Scalar) {
            a * b
        } else if (a is Matrix && b is Matrix) {
            (a * b) ?: throw Exception("Matrices have the wrong dimensions: a: $a; b:$b")
        } else {
            UnexpectedEof(matrixToken)
        }
    }

    val sumChain by leftAssociative(mulChain, plus or minus) { a, op, b ->
        return@leftAssociative if (op.type == plus) {
            if (a is Matrix && b is Matrix) {
                (a + b) ?: UnexpectedEof(matrixToken)
            } else if (a is Scalar && b is Scalar) {
                Array(1) { Array(1) { a + b } }
            } else {
                UnexpectedEof(matrixToken)
            }
        } else if (op.type == minus) {
            if (a is Matrix && b is Matrix) {
                (a - b) ?: UnexpectedEof(matrixToken)
            } else if (a is Scalar && b is Scalar) {
                Array(1) { Array(1) { a - b } }
            } else {
                UnexpectedEof(matrixToken)
            }
        } else {
            UnexpectedEof(matrixToken)
        }
    }

    override val rootParser: Parser<Any> by sumChain
}


fun main() {
//    val size = 20f
//    val texFont = DefaultTeXFont(size)
//    val env = TeXEnvironment(texFont)
//    val g = SvgGraphics2D()
//    g.scale(size.toDouble(), size.toDouble())
//    var equation = "\\sigma=\\sqrt{\\sum_{i=1}^{k} p_i(x_i-\\mu)^2}"
//    val formula = TeXFormula(equation)
//    val box = formula.createBox(env)
//    val layout = TeXLayout(box, size)
//    g.initialize( layout.getWidth(), layout.getHeight() );
//
//    val frame = JFrame()
//    frame.defaultCloseOperation=WindowConstants.EXIT_ON_CLOSE
//    val jPanel = object : JPanel() {
//        override fun paintComponent(g2: Graphics?) {
//            super.paintComponent(g2)
//            box.draw(g as Graphics2D?,layout.x,layout.y)
//        }
//    }
//    frame.add(jPanel, BorderLayout.CENTER)
//    frame.setSize(800,600)
//    jPanel.grabFocus()
//    frame.isVisible = true


    val grammar = ExpressionGrammar()
    while (true) {
        println("Input Expression:")
        lateinit var tokensForDebugging: TokenMatchesSequence
        try {
            val input = readLine() ?: ""
            if (input == "clear") {
                repeat(50) { println() }
                continue
            }
            tokensForDebugging = grammar.tokenizer.tokenize(input)
            when (val result = grammar.parseToEnd(tokensForDebugging)) {
                is Scalar -> {
                    println(result)
                }
                is Matrix -> {
                    result.printMatrix()
                    println("Latex: ${result.asLatex()}")
                }
                else -> {
                    System.err.println("Error, result was neither Scalar nor Matrix")
                }
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