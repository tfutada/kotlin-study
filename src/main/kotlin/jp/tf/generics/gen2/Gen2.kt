package jp.tf.jp.tf.generics.gen2

fun <T : Number> echo(a: T): T {
    return a
}

fun main() {
    val ret = echo(10)
    println(ret.minus(2))

    val ret2 = echo(10.0)
    println(ret2.plus(2.0))

//    val ret3 = echo("hello")
//    println(ret3.uppercase())
}

