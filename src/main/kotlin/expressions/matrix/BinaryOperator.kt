package expressions.matrix

abstract class MatrixBinaryOperator(protected val left: MatrixExpression, protected val right: MatrixExpression) :
    MatrixExpression() {
}