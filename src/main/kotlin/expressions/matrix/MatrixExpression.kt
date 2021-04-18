package expressions.matrix

abstract class MatrixExpression {
    abstract fun evaluate(): Array<Array<out Number>>
}