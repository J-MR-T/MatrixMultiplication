import java.lang.NumberFormatException
import kotlin.system.exitProcess

private val exitCommands: List<String> = listOf("quit", "stop", "exit", "vim")

fun main() {
    while (true) {
        println("Pick your poison (type 'quit','stop' or 'exit' without the '' at any time to stop the application):")
        CLIOptions.values().forEach(::println)
        println("Select one by typing its corresponding number")
        val input = readLine();
        val option: CLIOptions? = CLIOptions.values().find { cliOptions ->
            cliOptions.index == input?.toIntOrNull()
        }
        try {
            val result: Array<Array<out Number>> = when (option) {
                CLIOptions.MATRIX_MULTIPLY -> {
                    println("How many matrices would you like to multiply? (Default: 2)")
                    val howManyMatrices = readLine()?.toIntOrNull() ?: 2

                    val matrices: List<Array<Array<out Number>>> = List(howManyMatrices) {
                        readDoubleMatrix("Input  ${it + 1}. matrix, row by row, spaces between members, end matrix with empty line").asNumberMatrix
                    }
                    matrices.multiplyAll()
                }
                CLIOptions.MATRIX_POWER -> {
                    val matrix: Array<Array<out Number>> = readDoubleMatrix().asNumberMatrix
                    println("Exponent:")
                    val exponent = readLine()?.toIntOrNull() ?: 2

                    val matrices = List(exponent) {
                        matrix
                    }
                    matrices.multiplyAll()
                }
                CLIOptions.MATRIX_TRANSPOSE -> {
                    val matrix: Array<Array<out Number>> = readDoubleMatrix().asNumberMatrix
                    matrix.transpose()
                }
                CLIOptions.SCALAR_MATRIX_MULTIPLY -> {
                    println("")
                    val matrix: Array<Array<out Number>> = readDoubleMatrix().asNumberMatrix
                    matrix
                }
                else -> {
                    error("Wrong Option chosen")
                }
            }
            println("Result:")
            result.printMatrix()
            println(result.asLatex())

        } catch (e: Exception) {
            System.err.println("Try again: $e")
        }
    }
}

fun readLine(): String? {
    val line = kotlin.io.readLine()
    line?.trim()?.toLowerCase()?.findAnyOf(exitCommands)?.let {
        println("Shutting down")
        exitProcess(0)
    }
    return line
}

private fun Array<Array<out Number>>.asLatex(whichKind: String = "pmatrix"): String {
    return this.joinToString(
        separator = "\\\\",
        prefix = "\\begin{$whichKind}",
        postfix = "\\end{$whichKind}",
    ) { arrayOfNumbers -> arrayOfNumbers.joinToString("&") };
}

operator fun Array<Array<out Number>>.times(other: Array<Array<out Number>>): Array<Array<Number>>? {
    return multiply(this, other);
}

private fun Array<Array<out Number>>.printMatrix() {
    forEach { ints ->
        ints.forEach { print("$it\t") }
        println("")
    }
}

private fun Array<Array<out Number>>.transpose(): Array<Array<out Number>> {
    return Array(this[0].size) { indexRow ->
        Array(this.size) { indexColumn ->
            this[indexColumn][indexRow]
        }
    }
}

private fun List<Array<Array<out Number>>>.multiplyAll(): Array<Array<out Number>> {
    //FIXME Not that elegant because reduce has to have the same return type but whatever
    return reduce { left, right ->
        (left * right)?.map { arrayOfNumbers ->
            arrayOfNumbers.map { it }.toTypedArray()
        }?.toTypedArray() ?: error("Matrix multiplication not defined on inputs")
    }
}

val Array<Array<Double>>.asNumberMatrix: Array<Array<out Number>>
    get() {
        return if (flatMap { doubles: Array<Double> -> doubles.asIterable() }
                .all { d: Double -> d.toString().endsWith(".0") }) {
            map { doubles -> doubles.map { d -> d.toInt() }.toTypedArray() }.toTypedArray()
        } else {
            map { doubles -> doubles.map { d -> d }.toTypedArray() }.toTypedArray()
        }
    }

fun Array<Array<Double>>.convertToIntMatrixOrNull(): Array<Array<Int>>? {
    return if (flatMap { doubles: Array<Double> -> doubles.asIterable() }
            .all { d: Double -> d.toString().endsWith(".0") }) {
        map { doubles -> doubles.map { d -> d.toInt() }.toTypedArray() }.toTypedArray()
    } else {
        null
    }
}

fun readDoubleMatrix(text: String = "Input matrix, row by row, spaces between members, end matrix with empty line"): Array<Array<Double>> {
    val lines: MutableList<String> = mutableListOf()
    println(text)
    do {
        val lastInput = readLine() ?: ""
        if (lastInput != "") lines.add(lastInput)
    } while (lastInput != "")
    val matrix = try {
        lines.map {
            it.splitToSequence(" ").filter(String::isNotBlank).map { s -> s.toDouble() }.toList().toTypedArray()
        }.toTypedArray()
    } catch (e: NumberFormatException) {
        error("Did not input only numbers: $e")
    }
    if (!matrix.map(Array<Double>::size).all { i -> i == matrix[0].size }) {
        error("Non uniform dimensions")
    }
    return matrix
}

fun multiply(left: Array<Array<out Number>>, right: Array<Array<out Number>>): Array<Array<Number>>? {
    //Otherwise matrix multiplication isn't possible
    return if (left[0].size == right.size) {
        val returnMatrix: Array<Array<Number>> = Array(left.size) { Array(right[0].size) { 0 } }
        returnMatrix.forEachIndexed { indexRow, arrayOfNumbers ->
            arrayOfNumbers.forEachIndexed { indexColumn, _ ->
                returnMatrix[indexRow][indexColumn] =
                    left[indexRow].mapIndexed { index, number -> number * right[index][indexColumn] }.reduce(::add)
            }
        }
        returnMatrix
    } else {
        null
    }
}