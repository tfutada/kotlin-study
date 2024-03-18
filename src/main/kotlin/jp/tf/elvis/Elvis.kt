package jp.tf.elvis

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // elvis operator
    val a: Int? = null
    val b = a ?: throw IllegalArgumentException("a is null")
//    val b = if (a != null) a else 0

    println(b) // 0
}

