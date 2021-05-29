import kotlin.math.pow

fun add(x: Number, y: Number): Number {
    return x + y
}

infix fun Number.pow(other: Number): Number {
    val doubleResult = (this.toDouble()).pow(other.toDouble())
    return if (doubleResult.toInt().toDouble() == doubleResult) {
        doubleResult.toInt()
    } else {
        doubleResult
    }
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
    }else {
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