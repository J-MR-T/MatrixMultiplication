import java.lang.NumberFormatException
import kotlin.system.exitProcess

@OptIn(ExperimentalStdlibApi::class)
fun main(args: Array<String>) {
    while (true) {
        println("Pick your poison (type 'quit','stop' or 'exit' without the '' at any time to stop the application):")
        println("1: Matrix multiplication")
        println("2: Matrix pow")
        println("3: Matrix transpose NOT YET IMPLEMENTED")
        println("4: Matrix add NOT YET IMPLEMENTED")
        println("5: Matrix subtract NOT YET IMPLEMENTED")
        println("Type 1,2,3,4 or 5:")
        when (readLine()) {
            "1" -> {
                println("How many matrices would you like to multiply? (Default: 2)")
                val howManyMatrices = readLine()?.toIntOrNull() ?: 2;

                val matrices: List<Array<Array<out Number>>> = List(howManyMatrices) {
                    readDoubleMatrix("Input  ${it + 1}. matrix, row by row, spaces between members, end matrix with empty line").asNumberMatrix
                }
                println("Result:")
                matrices.multiplyAll().printMatrix()
            }
            "2" -> {
                val matrix: Array<Array<out Number>> = readDoubleMatrix().asNumberMatrix
                println("Exponent:")
                val exponent = readLine()?.toIntOrNull() ?: 2;

                val matrices = List(exponent) {
                    matrix
                }
                println("Result:")
                matrices.multiplyAll().printMatrix()
            }
            "3" -> {
                val matrix: Array<Array<out Number>> = readDoubleMatrix().asNumberMatrix
//                matrix.transpose()
                matrix.printMatrix()
            }
            else -> {
                println("Try again")
            }
        }
    }
}

fun readLine(): String? {
    val line = kotlin.io.readLine();
    line?.trim()?.toLowerCase()?.matches(Regex("quit|exit|stop"))?.let {
        if (it) {
            println("Shutting down")
            exitProcess(0)
        }
    }
    return line;
}

private fun Array<Array<out Number>>.printMatrix() {
    forEach { ints ->
        ints.forEach { print("$it\t") }
        println("")
    }
}

//private fun Array<Array<out Number>>.transpose(): Array<Array<out Number>>? {
//    return Array(this[0].size,Array(this.size){
//
//    })
//}

private fun List<Array<Array<out Number>>>.multiplyAll(): Array<Array<out Number>> {
    //FIXME Not that elegant because reduce has to have the same return type but whatever
    return reduce { left, right ->
        multiply(left, right)?.map { arrayOfNumbers ->
            arrayOfNumbers.map { it }.toTypedArray()
        }?.toTypedArray() ?: error("Matrix multiplication not defined on inputs")
    }
}

val Array<Array<Double>>.asNumberMatrix: Array<Array<out Number>>
    get() {
        return if (flatMap { doubles: Array<Double> -> doubles.asIterable() }
                .all { d: Double -> d.toString().endsWith(".0") }) {
            map { doubles -> doubles.map { d -> d.toInt() }.toTypedArray() }.toTypedArray();
        } else {
            map { doubles -> doubles.map { d -> d }.toTypedArray() }.toTypedArray()
        }
    }

fun Array<Array<Double>>.convertToIntMatrixOrNull(): Array<Array<Int>>? {
    return if (flatMap { doubles: Array<Double> -> doubles.asIterable() }
            .all { d: Double -> d.toString().endsWith(".0") }) {
        map { doubles -> doubles.map { d -> d.toInt() }.toTypedArray() }.toTypedArray();
    } else {
        null;
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
        lines.map { it ->
            it.splitToSequence(" ").filter(String::isNotBlank).map { s -> s.toDouble() }.toList().toTypedArray()
        }.toTypedArray()
    } catch (e: NumberFormatException) {
        error("Did not input only numbers: $e")
    }
    if (!matrix.map(Array<Double>::size).all { i -> i == matrix[0].size }) {
        error("Non uniform dimensions")
    }
    return matrix;
}

//fun multiply(left: Array<Array<Int>>, right: Array<Array<Int>>): Array<Array<Int>>? {
//    //Otherwise matrix multiplication isn't possible
//    return if (left[0].size == right.size) {
//        val returnMatrix: Array<Array<Int>> = Array(left.size) { Array(right[0].size) { 0 } }
//        returnMatrix.forEachIndexed { indexRow, arrayOfNumbers ->
//            arrayOfNumbers.forEachIndexed { indexColumn, _ ->
//                returnMatrix[indexRow][indexColumn] =
//                    left[indexRow].mapIndexed { index, number -> number * right[index][indexColumn] }
//                        .reduce(::add)
//                        .toInt()
//            }
//        }
//        returnMatrix
//    } else {
//        null
//    }
//}
//
//fun multiply(left: Array<Array<Double>>, right: Array<Array<Double>>): Array<Array<Double>>? {
//    //Otherwise matrix multiplication isn't possible
//    return if (left[0].size == right.size) {
//        val returnMatrix: Array<Array<Double>> = Array(left.size) { Array(right[0].size) { 0.0 } }
//        returnMatrix.forEachIndexed { indexRow, arrayOfNumbers ->
//            arrayOfNumbers.forEachIndexed { indexColumn, _ ->
//                returnMatrix[indexRow][indexColumn] =
//                    left[indexRow].mapIndexed { index, number -> number * right[index][indexColumn] }.reduce(::add)
//                        .toDouble()
//            }
//        }
//        returnMatrix
//    } else {
//        null
//    }
//}

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

operator fun Number.times(other: Number): Number {
    return if (this is Int && other is Int) {
        this * other;
    } else if (this is Float && other is Float) {
        this * other;
    } else if (this is Long && other is Long) {
        this * other;
    } else if (this is Double && other is Double) {
        this * other;
    } else {
        0;
    }
}

fun add(x: Number, y: Number): Number {
    return x + y;
}

operator fun Number.plus(other: Number): Number {
    return if (this is Int && other is Int) {
        this + other;
    } else if (this is Float && other is Float) {
        this + other;
    } else if (this is Long && other is Long) {
        this + other;
    } else if (this is Double && other is Double) {
        this + other;
    } else {
        0;
    }
}