package jp.tf.jp.tf.scope.with

fun main() {
    val message = StringBuilder()

    with(message) {
        append("Hello, ")
        append("world!")
    }

    println(message.toString()) // Prints: Hello, world!
}


