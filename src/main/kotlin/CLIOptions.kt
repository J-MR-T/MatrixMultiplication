enum class CLIOptions(private val optionAsString: String, internal val index: Int = Counter.indices++) {
    MATRIX_MULTIPLY("Matrix multiplication"),
    MATRIX_POWER("Matrix power"),
    MATRIX_TRANSPOSE("Matrix transpose"),
    MATRIX_ADD("Matrix-Scalar multiplication"),
    MATRIX_SUBTRACT("Matrix add NOT YET IMPLEMENTED"),
    SCALAR_MATRIX_MULTIPLY("Matrix subtract NOT YET IMPLEMENTED"),
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
