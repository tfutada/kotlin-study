package jp.tf.jp.tf.coroutine4

import kotlinx.coroutines.*

fun main() = runBlocking {
    val scope = CoroutineScope(Dispatchers.Default)

    val job = scope.launch {
        println("Parent : I'm working in thread ${Thread.currentThread().name}")

        launch {
            println("Child : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            println("Child : I'm working in thread ${Thread.currentThread().name}")
        }

        delay(1000L)
        println("Task completed!")
    }


//    scope.cancel()
    job.cancel()
    delay(1100L)
    println("Done")
}