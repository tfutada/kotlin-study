package jp.tf.fibonacci

import kotlinx.coroutines.*


fun main() = runBlocking {
    val numberOfCores = Runtime.getRuntime().availableProcessors()
    println("Number of logical CPU cores: $numberOfCores")

    val NUM_COROUTINES = numberOfCores + 1 // create one more coroutine than CPU cores

    repeat(NUM_COROUTINES) {
        launch(Dispatchers.Default) { // This is where the magic happens
            println("start...")
            // compute a fibonacci number
            val n = 46
            val fib = fibonacciRecursive(n)
            println("thread $it Fibonacci($n) = $fib")
        }
    }

    delay(100_000L) // Wait for all coroutines to finish
}

fun fibonacciRecursive(n: Int): Int {
    return if (n <= 1) n
    else fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2)
}
