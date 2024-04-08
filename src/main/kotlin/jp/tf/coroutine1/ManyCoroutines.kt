package jp.tf.jp.tf.coroutine1

import kotlinx.coroutines.*

fun main() = runBlocking {
    repeat(50_000) { // launch a lot of coroutines
        launch(Dispatchers.IO) {
//            delay(5000L)
            Thread.sleep(5000L)
            print(".")
        }
    }
}

