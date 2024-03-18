package jp.tf.ifs

fun main() {
    val a = 1
    val b = 3

    val ifs = if (a > b) {
        println("if block")
        a
    } else {
        println("else block")
        b
    }

    println(ifs)
}

