package jp.tf.ojbexpression

fun main() {
    val helloWorld = object {
        val hello = "Hello"
        val world = "World"

        // object expressions extend Any, so `override` is required on `toString()`
        override fun toString() = "$hello $world"
//        override fun toString(): String {
//            // Potential additional logic here
//            return "$hello $world"
//        }
    }
    println(helloWorld)
}


