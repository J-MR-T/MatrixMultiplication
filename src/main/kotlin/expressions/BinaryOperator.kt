package expressions

abstract class BinaryOperator(protected val left: Expression<*>, protected val right: Expression<*>) : Expression<*> {
}