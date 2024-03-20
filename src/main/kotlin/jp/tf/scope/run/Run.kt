package jp.tf.jp.tf.scope.run

fun main() {
    val message = StringBuilder().run {
        append("Hello, ")
        append("world!")
        toString()
    }

    println(message)
}


