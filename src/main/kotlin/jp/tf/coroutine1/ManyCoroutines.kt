package jp.tf.jp.tf.coroutine1

import kotlinx.coroutines.*

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun main() = runBlocking {
    val numCores = Runtime.getRuntime().availableProcessors()
    println("Number of logical CPU cores: $numCores")

    repeat(numCores + 1) { // launch a lot of coroutines
        launch(Dispatchers.Default) {
            delay(3000L)
//            Thread.sleep(3000L)
            log(".")
        }
    }

    println("Press Enter to exit")
    readln()
    println()
}
