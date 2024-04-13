package jp.tf.jp.tf.coroutine1

import kotlinx.coroutines.*

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun main() = runBlocking {
    repeat(9) { // launch a lot of coroutines
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
