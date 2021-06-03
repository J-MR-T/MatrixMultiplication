import calculations.Matrix
import kotlin.system.exitProcess
import calculations.Matrix.Companion.addAll
import calculations.Matrix.Companion.multiplyAll
import calculations.Matrix.Companion.readMatrix
import calculations.Matrix.Companion.times
import codes.LinearCode

private val exitCommands: List<String> = listOf("quit", "stop", "exit", "vim")


fun main() {
    val bauer84 = LinearCode(
        8,
        4,
        Matrix(arrayOf(arrayOf(0, 1, 1, 1), arrayOf(1, 0, 1, 1), arrayOf(1, 1, 0, 1), arrayOf(1, 1, 1, 0)))
    )
    println(bauer84.encode(arrayOf(0, 1, 1, 0)).contentToString())
    println(bauer84.decode(arrayOf(0, 0, 0, 0, 0, 1, 1,0)).contentDeepToString())

//    val error194 = codes.LinearCode(
//        19,
//        4,
//        Matrix(
//            arrayOf(
//                arrayOf(0, 1, 1, 1),
//                arrayOf(1, 0, 1, 1),
//                arrayOf(1, 1, 0, 1),
//                arrayOf(1, 1, 1, 0),
//                arrayOf(0, 1, 1, 1),
//                arrayOf(1, 1, 0, 0),
//                arrayOf(0, 1, 1, 0),
//                arrayOf(0, 0, 1, 1),
//                arrayOf(1, 0, 0, 1),
//                arrayOf(1, 0, 1, 0),
//                arrayOf(0, 1, 0, 1),
//                arrayOf(1, 0, 0, 0),
//                arrayOf(0, 1, 0, 0),
//                arrayOf(0, 0, 1, 0),
//                arrayOf(0, 0, 0, 1),
//            )
//        )
//    )
//    println(error194.hammingDistance)
//    println(error194.encode(arrayOf(1, 0, 1, 0)).contentToString())
//    println(error194.decode(arrayOf(0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0)).contentToString())

//    val hamming154 = codes.LinearCode(
//        15, 11, Matrix(
//            arrayOf(
//                arrayOf(1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1),
//                arrayOf(1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1),
//                arrayOf(0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1),
//                arrayOf(0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1),
//            )
//        )
//    )
//    println(hamming154.hammingDistance)
//    println(hamming154.encode(arrayOf(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1)).contentToString())
//    println(hamming154.decode(arrayOf(1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1)).contentToString())

}

private fun oldCalcLoop() {
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
