package expressions.scalar

class Scalar(private val value: Number) : ScalarExpression() {

    override fun evaluate(): Number {
        return value
    }
}