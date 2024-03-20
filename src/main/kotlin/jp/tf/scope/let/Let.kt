package jp.tf.jp.tf.scope.let

fun main() {
    val message = StringBuilder().let {
        it.append("Hello, ")
        it.append("world!")
        it.toString()
    }

    println(message)

    val numbers = mutableListOf("one", "two", "three", "four", "five")
    val resultList = numbers.map { it.length }.filter { it > 3 }
    println(resultList)

    numbers.map { it.length }.filter { it > 3 }.let {
        println(it)
        // and more function calls if needed
    }

    numbers.map { it.length }.filter { it > 3 }.let(::println)
}


