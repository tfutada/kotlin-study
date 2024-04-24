package jp.tf.generics.gen1

import java.util.*

fun <T> echo(a: T): T {
    return a
}

fun main() {
    val ret = echo(10)
    println(ret.minus(2))

    val ret2 = echo("Hello, Generics!")
    println(ret2.uppercase(Locale.getDefault()))
}

