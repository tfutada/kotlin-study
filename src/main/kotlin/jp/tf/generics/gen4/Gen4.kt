package jp.tf.jp.tf.generics.gen4

fun <T> T.protected(block: T.() -> Unit) {
    println("get a DB connection")
    block()
}

fun main() {
    val myList = mutableListOf(1, 2, 3)

    myList.protected {
        add(4)
        removeAt(0)
    }

    val stringBuilder = StringBuilder("Hello, ")
    stringBuilder.protected {
        append("world!")
    }
}