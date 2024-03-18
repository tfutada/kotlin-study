package jp.tf.whens

enum class Bit {
    ZERO, ONE
}

fun main() {
    val numericValue = when (getRandomBit()) {
        Bit.ZERO -> 0
        Bit.ONE -> 1
        else -> {
            2
        }
    }
    println(numericValue)
}

fun getRandomBit(): Any {
    return Bit.ZERO
}
