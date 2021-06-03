package codes

import calculations.Matrix
import calculations.minus
import calculations.numMod
import calculations.plus
import java.util.*

class LinearCode(val n: Int, val k: Int, val A: Matrix) {
    val identityK = Matrix.getIdentityMatrix(k)
    val identityNMinusK = Matrix.getIdentityMatrix(n - k)
    val generatorMatrix: Matrix = Matrix(
        Array(n) { rowIndex ->
            if (rowIndex < k) {
                identityK[rowIndex]
            } else {
                A[rowIndex - k]
            }
        }
    )

    val parityCheckMatrix: Matrix = Matrix(
        Array(n) { columnIndex ->
            if (columnIndex < k) {
                A.columnVectors[columnIndex]
            } else {
                identityNMinusK.columnVectors[columnIndex - k]
            }
        }
    ).columnVectors

    val hammingDistance: Int = generatorMatrix.columnVectors
        .flatMap { column ->
            generatorMatrix.columnVectors.filter { !it.contentEquals(column) }.map { innerColumn ->
                addMod2(column, innerColumn).filter { number -> number != 0 }.count()
            }
        }.minOrNull() ?: 0

    companion object {
        fun noisyChannelSimulation(
            v: Array<Number>,
            probabilityOfFlipPerBit: Double = 0.1,
            maxFlippedBits: Int = v.size
        ): Array<Number> {
            var maxFlippedBits = maxFlippedBits
            return v.map { num ->
                (num + if (Math.random() < probabilityOfFlipPerBit && (maxFlippedBits > 0)) {
                    maxFlippedBits--
                    1
                } else 0) numMod 2
            }.toTypedArray()
        }

        fun oneHotVector(hotIndex: Int, size: Int): Array<Int> {
            return Array(size) {
                if (it == hotIndex) 1 else 0
            }
        }

        fun addMod2(vec1: Array<Number>, vec2: Array<Number>): Array<Number> {
            return vec1.mapIndexed { index, number -> (number + vec2[index]) numMod 2 }.toTypedArray()
        }

        fun addMod2(vec1: Array<Int>, vec2: Array<Int>): Array<Int> {
            return if (vec1.size > vec2.size) {
                vec1.mapIndexed { index, number -> (number + (vec2.getOrNull(index) ?: 0)).mod(2) }.toTypedArray()
            } else {
                vec2.mapIndexed { index, number -> (number + (vec1.getOrNull(index) ?: 0)).mod(2) }.toTypedArray()
            }
        }
    }

    /**
     * @param [v] must have size [k] and be *written* as a rowVector for example by using [arrayOf]
     * @return an encoded vector of size [n]
     */
    fun encode(v: Array<Number>): Array<Number>? {
        return (generatorMatrix.mulMod(Matrix(Array(1) { v }).transposed(), 2))?.transposed()?.get(0)?.map { a -> a }
            ?.toTypedArray()
    }

    /**
     * @param [v] must have size [n] and be *written* as a rowVector for example by using [arrayOf]
     * @return a decoded vector of size [k]
     */
    fun decode(v: Array<Number>, mod: Int = 2): Array<Number>? {
        val syndrome =
            (parityCheckMatrix.mulMod(Matrix(Array(1) { v }).transposed(), mod))?.transposed()?.get(0) ?: return null
        var mostProbableErrorVector =
            parityCheckMatrix.columnVectors.map { vec ->
                if (vec.contentDeepEquals(syndrome)) 1 else 0
            }.toTypedArray()
        //If its right or there is more than one error
        if (syndrome.any { it != 0 } && mostProbableErrorVector.all { it == 0 }) {
            var currentHammingWeightTry = 2;
            val matchingErrorVectors = mutableSetOf<Array<Int>>()
            //generate new linear combinations

            val generatedVectors = parityCheckMatrix.columnVectors.flatMapIndexed { index, column ->
                parityCheckMatrix.columnVectors.mapIndexed { indexInner, innerColumn ->
                    val oneHotVector1 = oneHotVector(index, n)
                    val oneHotVector2 = oneHotVector(indexInner, n)
                    val twoHotVector = addMod2(oneHotVector1, oneHotVector2)
                    val added = addMod2(column, innerColumn)
                    added to twoHotVector
                }
            }.toMutableSet()
            while (currentHammingWeightTry < ((hammingDistance - 1) / 2) && matchingErrorVectors.size == 0) {
                for (i in 2 until currentHammingWeightTry) {
                    val l = parityCheckMatrix.columnVectors.flatMapIndexed { parityCheckColumnIndex, column ->
                        generatedVectors.map { innerColumn ->
                            val oneHotVector1 = innerColumn.second
                            val oneHotVector2 = oneHotVector(parityCheckColumnIndex, n)
                            val twoHotVector = addMod2(oneHotVector1, oneHotVector2)
                            val added = addMod2(column, innerColumn.first)
                            added to twoHotVector
                        }
                    }
                    generatedVectors.addAll(l)
                }
                currentHammingWeightTry++
            }
            matchingErrorVectors.addAll(
                //filtering out doubly added vectors
                generatedVectors.associateBy({ pair -> pair.second }, { pair -> pair.first })
                    .filter { entry -> entry.value.contentDeepEquals(syndrome) }
                    .asSequence().distinctBy { entry -> entry.key.contentHashCode() }
                    .map { entry -> entry.key }
            )
            //This might not work because a bit of concurrent modification, lets see
//            matchingErrorVectors.removeIf { outer ->
//                matchingErrorVectors.filter { it != outer }.any { inner -> outer.contentDeepEquals(inner) }
//            }

            if (matchingErrorVectors.size == 1) {
                mostProbableErrorVector = matchingErrorVectors.first()
            } else {
                System.err.println("Couldn't find a matching and unique error vector")
                return null
            }
        }
        return v.copyOfRange(0, k).mapIndexed { index, number -> (number - mostProbableErrorVector[index]) numMod 2 }
            .toTypedArray()
    }


}

