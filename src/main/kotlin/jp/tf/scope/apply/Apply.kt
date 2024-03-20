package jp.tf.jp.tf.scope.apply

fun main() {
    val message = StringBuilder().apply {
        append("Hello, ")
        append("world!")
    }

    println(message.toString())
}


