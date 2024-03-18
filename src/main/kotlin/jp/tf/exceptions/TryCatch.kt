package jp.tf.exceptions

fun main() {

    val input = "a"
    val a: Int? = try {
        input.toInt()
    } catch (e: NumberFormatException) {
        null
    }
    println(a)
}

