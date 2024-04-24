package jp.tf.jp.tf.coroutine6

import kotlinx.coroutines.*

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun main() = runBlocking<Unit> {
    try {
        failedConcurrentWork()
    } catch (e: ArithmeticException) {
        log("Computation failed with ArithmeticException")
    }
}

suspend fun failedConcurrentWork() = coroutineScope {
    // 1つ目の子コルーチン
    val jobOne = launch {
        try {
            delay(Long.MAX_VALUE) // Emulates very long computation
        } finally {
            log("First child was cancelled")
        }
    }

    // 2つ目の子コルーチン。わざと例外をスローする。１つ目の子コルーチンがキャンセルされる。
    val jobTwo = launch {
        log("Second child throws an exception")
        throw ArithmeticException()
    }

    try {
        joinAll(jobOne, jobTwo)
    } catch (e: CancellationException) {
        log("CoroutineScope was cancelled due to an exception in one of the coroutines")
    }
}
