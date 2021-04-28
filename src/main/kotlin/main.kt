import java.lang.NumberFormatException
import kotlin.system.exitProcess
import Matrix.*
import Matrix.Companion.addAll
import Matrix.Companion.multiplyAll
import Matrix.Companion.readMatrix
import Matrix.Companion.times

private val exitCommands: List<String> = listOf("quit", "stop", "exit", "vim")


fun main() {
    while (true) {
        println("Pick your poison (type 'quit','stop' or 'exit' without the '' at any time to stop the application):")
        CLIOptions.values().forEach(::println)
        println("Select one by typing its corresponding number")
        val input = readLine()
        val option: CLIOptions? = CLIOptions.values().find { cliOptions ->
            cliOptions.index == input?.toIntOrNull()
        }
        try {
            val result: Matrix = when (option) {
                CLIOptions.MATRIX_MULTIPLY -> {
                    val matrices = readMatrices(2)
                    matrices.multiplyAll()
                }
                CLIOptions.MATRIX_ADD -> {
                    val matrices = readMatrices(2)
                    matrices.addAll()
                }
                CLIOptions.MATRIX_SUBTRACT -> {
                    val left =
                        readMatrix("Input 1. matrix, row by row, spaces between members, end matrix with empty line")
                    val right =
                        readMatrix("Input 2. matrix, row by row, spaces between members, end matrix with empty line")
                    (left + (right * (-1.0))) ?: error("")
                }
                CLIOptions.MATRIX_POWER -> {
                    val matrix = readMatrix()
                    println("Exponent:")
                    val exponent = readLine()?.toIntOrNull() ?: 2

                    val matrices = List(exponent) {
                        matrix
                    }
                    matrices.multiplyAll()
                }
                CLIOptions.MATRIX_TRANSPOSE -> {
                    val matrix = readMatrix()
                    matrix.transposed()
                }
                CLIOptions.SCALAR_MATRIX_MULTIPLY -> {
                    println("How many scalars would you like to multiply? (Default: 1)")
                    val howManyScalars = readLine()?.toIntOrNull() ?: 1

                    val scalars: List<Double> = List(howManyScalars) {
                        println("Input Scalar:")
                        readLine()?.toDoubleOrNull() ?: 1.0
                    }

                    val matrices = readMatrices(1)
                    val finalScalar = scalars.reduce { x, y -> x * y }
                    (finalScalar as Number) * (matrices.multiplyAll())
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

private fun readMatrices(defaultAmount: Int): List<Matrix> {
    println("How many matrices would you like to multiply? (Default: 2)")
    val howManyMatrices = readLine()?.toIntOrNull() ?: defaultAmount

    return List(howManyMatrices) {
        readMatrix("Input  ${it + 1}. matrix, row by row, spaces between members, end matrix with empty line")
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
