package jp.tf.lists

fun main() {
    val numbers = mutableListOf(1, 2, 3, 4, 5)
    numbers.add(6)
    numbers.removeAt(1)
    println(numbers)
}

