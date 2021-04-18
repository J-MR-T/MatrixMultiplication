fun add(x: Number, y: Number): Number {
    return x + y
}

//I know that this looks ridiculous, but all of these operators call different functions like Int.plus(Int) or Long.plus(Long)
operator fun Number.times(other: Number): Number {
    return if (this is Int && other is Int) {
        this * other
    } else if (this is Float && other is Float) {
        this * other
    } else if (this is Long && other is Long) {
        this * other
    } else if (this is Double && other is Double) {
        this * other
    } else {
        0
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
    } else {
        0
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
    } else {
        0
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
        0
    }
}