package calculations

import com.github.h0tk3y.betterParse.parser.UnexpectedEof
import grammar.InvalidInput
import kotlin.math.pow

fun readScalar(): Number? {
    var input: Number = readLine()?.toDoubleOrNull() ?: return null
    if (input.toString().endsWith(".0")) {
        input = input.toInt()
    }
    return input
}


fun add(x: Number, y: Number): Number {
    return x + y
}

fun addMod(x: Number, y: Number, mod: Number): Number {
    return if (x.toInt() == x && y.toInt() == y) {
        (x.toInt() + y.toInt()).mod(mod.toInt())
    } else {
        (x.toDouble() + y.toDouble()).mod(mod.toDouble())
    }

}

infix fun Number.pow(other: Number): Number {
    val doubleResult = (this.toDouble()).pow(other.toDouble())
    return if (doubleResult.toInt().toDouble() == doubleResult) {
        doubleResult.toInt()
    } else {
        doubleResult
    }
}


infix fun Number.numMod(mod: Int): Number {
    return when (this) {
        is Int -> {
            this.toInt().mod(mod)
        }
        is Float -> {
            this.toFloat().mod(mod.toFloat())
        }
        is Long -> {
            this.toLong().mod(mod.toLong())
        }
        is Double -> {
            this.toDouble().mod(mod.toDouble())
        }
        else -> {
            TODO("Not implemented for this Datatype yet")
        }
    }
}

fun Number.toIntIfPossible(): Number {
    return if (this.toInt().toDouble() == this.toDouble()) this.toInt() else this
}

//I know that this looks ridiculous, but all of these operators call different functions like Int.plus(Int) or Long.plus(Long) because of smart casting
operator fun Number.times(other: Number): Number {
    return if (this is Int && other is Int) {
        this * other
    } else if (this is Float && other is Float) {
        this * other
    } else if (this is Long && other is Long) {
        this * other
    } else if (this is Double && other is Double) {
        this * other
    } else if (this is Int && other is Double) {
        this * other
    } else if (this is Double && other is Int) {
        this * other
    } else {
        TODO("Not implemented for this Datatype yet")
    }
}

operator fun Number.plus(other: Number): Number {
    return if (this is Int && other is Int) {
        this + other
    } else if (this is Float && other is Float) {
        this + other
    } else if (this is Long && other is Long) {
        this + other
    } else if (this is Double && other is Double) {
        this + other
    } else if (this is Int && other is Double) {
        this + other
    } else if (this is Double && other is Int) {
        this + other
    } else {
        TODO("Not implemented for this Datatype yet")
    }
}

operator fun Number.div(other: Number): Number {
    return if (this is Int && other is Int) {
        this / other
    } else if (this is Float && other is Float) {
        this / other
    } else if (this is Long && other is Long) {
        this / other
    } else if (this is Double && other is Double) {
        this / other
    } else if (this is Double && other is Int) {
        this / other
    } else {
        TODO("Not implemented for this Datatype yet")
    }
}

operator fun Number.minus(other: Number): Number {
    return if (this is Int && other is Int) {
        this - other
    } else if (this is Float && other is Float) {
        this - other
    } else if (this is Long && other is Long) {
        this - other
    } else if (this is Double && other is Double) {
        this - other
    } else {
        TODO("Not implemented for this Datatype yet")
    }
}