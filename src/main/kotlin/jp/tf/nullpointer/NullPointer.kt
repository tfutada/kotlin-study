package jp.tf.nullpointer

fun main() {
    val a: String? = null
    println(a?.length) // null安全な呼び方

    a?.let {
        println(it.length)
    }

    val ret =  a.toString()

    val price = 100
    val aInt: Int? = price as? Int
    println(aInt)

    var b: String = "abc"
//    b = null // compilation error
}
