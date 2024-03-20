package jp.tf.jp.tf.delegate2

val lazyValue: String by lazy {
    println("Computing the value...")
    "Hello, World!" // This will be computed only upon first access
}

fun main() {
    println(lazyValue) // Computing the value... then Hello, World!
    println(lazyValue) // Just Hello, World!, without recomputing the value
}

