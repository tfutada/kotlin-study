package jp.tf.jp.tf.coroutine6

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    try {
        failedConcurrentWork()
    } catch (e: ArithmeticException) {
        println("Computation failed with ArithmeticException")
    }
}

suspend fun failedConcurrentWork() = coroutineScope {
    val jobOne = launch {
        try {
            delay(Long.MAX_VALUE) // Emulates very long computation
        } finally {
            println("First child was cancelled")
        }
    }

    val jobTwo = launch {
        println("Second child throws an exception")
        throw ArithmeticException()
    }

    try {
        joinAll(jobOne, jobTwo)
    } catch (e: CancellationException) {
        println("CoroutineScope was cancelled due to an exception in one of the coroutines")
    }
}
