package jp.tf.`class`

class SimpleClass(val a: String, var b: Int = 0) {
    // a method
    fun toStr(): String {
        return "a: $a, b: $b"
    }
}

// Extension function for SimpleClass
fun SimpleClass.dump() {
    println("a: $a, b: $b")
}

fun String.dump() {
    println("String: $this")
}

fun main() {
    val obj = SimpleClass("hello")
    println(obj.toStr())  // a: hello, b: 0
    obj.dump()

    val str = "world"
    str.dump()
    "hello".dump() // "String: hello
}

