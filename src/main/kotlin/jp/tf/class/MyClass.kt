package jp.tf.`class`

class InitOrderDemo(name: String) {
    var firstProperty = "First property: $name".also(::println)

    init {
        println("First initializer block that prints $name")
    }

    val secondProperty = "Second property: ${name.length}".also { println(it) }

    init {
        println("Second initializer block that prints ${name.length}")
    }
}

fun main() {
    val obj = InitOrderDemo("hello")
    obj.firstProperty = "world"
    println(obj.firstProperty)
}

