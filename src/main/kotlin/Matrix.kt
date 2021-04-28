import java.lang.NumberFormatException

private val Array<Array<Double>>.asNumberMatrix: Array<Array<out Number>>
    get() {
        return if (flatMap { doubles: Array<Double> -> doubles.asIterable() }
                .all { d: Double -> d.toString().endsWith(".0") }) {
            map { doubles -> doubles.map { d -> d.toInt() }.toTypedArray() }.toTypedArray()
        } else {
            map { doubles -> doubles.map { d -> d }.toTypedArray() }.toTypedArray()
        }
    }

class Matrix(private var matrix: Array<Array<out Number>>) {
    companion object {
        fun getIdentityMatrix(dimension: Int): Matrix {
            return Matrix(Array(dimension) { indexRow ->
                Array(dimension) { indexColumn ->
                    if (indexRow == indexColumn) 1 else 0
                }
            })
        }

        fun readMatrix(text: String = "Input matrix, row by row, spaces between members, end matrix with empty line"): Matrix {
            return Matrix(readDoubleMatrix(text).asNumberMatrix)
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

        private operator fun Array<Array<out Number>>.times(other: Array<Array<out Number>>): Array<Array<Number>>? {
            return multiply(this, other)
        }

        private fun multiply(left: Array<Array<out Number>>, right: Array<Array<out Number>>): Array<Array<Number>>? {
            //Otherwise matrix multiplication isn't possible
            return if (left[0].size == right.size) {
                val returnMatrix: Array<Array<Number>> = Array(left.size) { Array(right[0].size) { 0 } }
                returnMatrix.forEachIndexed { indexRow, arrayOfNumbers ->
                    arrayOfNumbers.forEachIndexed { indexColumn, _ ->
                        returnMatrix[indexRow][indexColumn] =
                            left[indexRow].mapIndexed { index, number -> number * right[index][indexColumn] }
                                .reduce(::add)
                    }
                }
                returnMatrix
            } else {
                null
            }
        }

        private fun add(left: Array<Array<out Number>>, right: Array<Array<out Number>>): Array<Array<Number>>? {
            //Otherwise matrix multiplication isn't possible
            return if (left.size == right.size && left[0].size == right[0].size) {
                val returnMatrix: Array<Array<Number>> = Array(left.size) { Array(left[0].size) { 0 } }
                returnMatrix.forEachIndexed { indexRow, arrayOfNumbers ->
                    arrayOfNumbers.forEachIndexed { indexColumn, _ ->
                        returnMatrix[indexRow][indexColumn] =
                            left[indexRow][indexColumn] + right[indexRow][indexColumn]
                    }
                }
                returnMatrix
            } else {
                null
            }
        }

        operator fun Number.times(matrix: Matrix): Matrix {
            return Matrix(this * matrix.matrix)
        }


        private operator fun Number.times(matrix: Array<Array<out Number>>): Array<Array<out Number>> {
            return matrix.map { row -> row.map { it * this }.toTypedArray() }.toTypedArray()
        }


        fun List<Matrix>.multiplyAll(): Matrix {
            return Matrix(map { matrix -> matrix.matrix }.multiplyAll())
        }

        fun List<Matrix>.addAll(): Matrix {
            return Matrix(map { matrix -> matrix.matrix }.addAll())
        }


        private fun List<Array<Array<out Number>>>.multiplyAll(): Array<Array<out Number>> {
            return reduce { left, right ->
                (left * right)?.map { arrayOfNumbers ->
                    arrayOfNumbers.map { it }.toTypedArray()
                }?.toTypedArray() ?: error("Matrix multiplication not defined on inputs")
            }
        }

        private fun List<Array<Array<out Number>>>.addAll(): Array<Array<out Number>> {
            return reduce { left, right ->
                (add(left, right))?.map { arrayOfNumbers ->
                    arrayOfNumbers.map { it }.toTypedArray()
                }?.toTypedArray() ?: error("Matrix add not defined on inputs")
            }
        }

    }

    private val Array<Array<Number>>.addOut: Array<Array<out Number>>
        get() {
            return this.map { arrayOfNumbers -> arrayOfNumbers.map { number -> number }.toTypedArray() }.toTypedArray()
        }

    fun asLatex(whichKind: String = "pmatrix"): String {
        return matrix.asLatex(whichKind)
    }

    private fun Array<Array<out Number>>.asLatex(whichKind: String = "pmatrix"): String {
        return this.joinToString(
            separator = "\\\\",
            prefix = "\\begin{$whichKind}",
            postfix = "\\end{$whichKind}",
        ) { arrayOfNumbers -> arrayOfNumbers.joinToString("&") }
    }

    operator fun times(other: Matrix): Matrix? {
        return (this.matrix * other.matrix)?.let { Matrix(it.addOut) }
    }

    operator fun times(number: Number): Matrix {
        return Matrix(number * this.matrix)
    }

    operator fun plus(other: Matrix): Matrix? {
        return (this.matrix + other.matrix)?.let { Matrix(it.addOut) }
    }

    operator fun minus(other: Matrix): Matrix? {
        return this + (-other)
    }

    private operator fun Array<Array<out Number>>.plus(other: Array<Array<out Number>>): Array<Array<Number>>? {
        return add(this, other)
    }

    fun printMatrix() {
        this.matrix.toIntsIfApplicable.printMatrix()
    }

    private fun Array<Array<out Number>>.printMatrix() {
        val maxSpaces =
            flatMap { it.asIterable() }.maxOf { number: Number -> number.toString().length }
        forEach { ints ->
            ints.forEach { print("$it" + " ".repeat(maxSpaces - it.toString().length + 1)) }
            println("")
        }
    }

    fun transpose() {
        this.matrix = this.matrix.transposed()
    }

    fun transposed(): Matrix {
        return Matrix(this.matrix.transposed())
    }

    private fun Array<Array<out Number>>.transposed(): Array<Array<out Number>> {
        return Array(this[0].size) { indexRow ->
            Array(this.size) { indexColumn ->
                this[indexColumn][indexRow]
            }
        }
    }

    private val Array<Array<out Number>>.toIntsIfApplicable: Array<Array<out Number>>
        get() {
            return if (flatMap { numbers -> numbers.asIterable() }.all { number -> number is Int }) {
                this
            } else {
                if (flatMap { numbers -> numbers.asIterable() }
                        .all { d: Number -> d.toString().endsWith(".0") }) {
                    map { numbers -> numbers.map { d -> d.toInt() }.toTypedArray() }.toTypedArray()
                } else {
                    map { numbers -> numbers.map { d -> d }.toTypedArray() }.toTypedArray()
                }
            }
        }

    private fun Array<Array<Double>>.convertToIntMatrixOrNull(): Array<Array<Int>>? {
        return if (flatMap { doubles: Array<Double> -> doubles.asIterable() }
                .all { d: Double -> d.toString().endsWith(".0") }) {
            map { doubles -> doubles.map { d -> d.toInt() }.toTypedArray() }.toTypedArray()
        } else {
            null
        }
    }

    operator fun unaryMinus(): Matrix {
        return this * -1
    }

    operator fun get(row: Int, column: Int): Number {
        return matrix[row][column]
    }

}