package expressions.matrix

class Matrix(private val content: Array<Array<out Number>>) : MatrixExpression() {

    override fun evaluate(): Array<Array<out Number>> {
        return content;
    }
}