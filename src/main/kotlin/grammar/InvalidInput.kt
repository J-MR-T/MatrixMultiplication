package grammar

import com.github.h0tk3y.betterParse.parser.ErrorResult

class InvalidInput(val input: CharSequence? = null) : ErrorResult() {
    override fun toString(): String {
        return super.toString() + if (input != null) "\tInvalid Input: $input" else ""
    }
}