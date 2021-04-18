package expressions.operators

import expressions.BinaryOperator
import expressions.Expression
import expressions.Matrix

class Multiply(left: Expression, right: Expression) : BinaryOperator(left, right) {

    override fun evaluate(): Expression {
        return left.evaluate() * right.evaluate()
    }
}