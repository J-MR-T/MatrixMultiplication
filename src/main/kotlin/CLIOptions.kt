enum class CLIOptions(private val optionAsString: String, internal val index: Int = Counter.indices++) {
    MATRIX_MULTIPLY("Matrix multiplication"),
    MATRIX_POWER("Matrix power"),
    MATRIX_TRANSPOSE("Matrix transpose"),
    MATRIX_ADD("Matrix add "),
    MATRIX_SUBTRACT("Matrix subtract NOT YET IMPLEMENTED"),
    SCALAR_MATRIX_MULTIPLY("Scalar-matrix multiplication NOT YET IMPLEMENTED"),
    ;

    override fun toString(): String {
        return "$index: $optionAsString"
    }

}

class Counter {
    companion object {
        var indices: Int = 0;
    }

}
