package jp.tf.jp.tf.extension.basic

import java.util.*

// Extension function for String
fun String.dump() {
    println("String: $this")
}

// lambda receiver
val output: String.() -> String = {
    "Moji: $this"
}

// to upper case
val output2: String.() -> String = { uppercase(Locale.getDefault()) }

fun main() {
    val str = "world"
    str.dump()

    println(str.output())
    println(str.output2())
}

