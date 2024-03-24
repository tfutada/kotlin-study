package jp.tf.jp.tf.coroutine4

import kotlinx.coroutines.*

fun main() = runBlocking {

    val job = launch(Dispatchers.Default) {
        println("Parent : I'm working in thread ${Thread.currentThread().name}")

        launch(Dispatchers.Default) {
            println("Child : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            println("Child : I'm working in thread ${Thread.currentThread().name}")
        }

        delay(1000L)
        println("Task completed!")
    }


//    scope.cancel()
//    job.cancel()
    delay(1100L)
    println("Done")
}