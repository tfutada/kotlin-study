package jp.tf.shared.parallel

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100  // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
//                    log("Launching coroutine $it")
                    repeat(k) { action() }
                }
            }
        }
    }
    println("Completed ${n * k} actions in $time ms")
}

var counter = 0

suspend fun main(): Unit = runBlocking {

    withContext(Dispatchers.Default) {
        massiveRun {
            counter++
        }
    }
    println("Counter = $counter")

    // run on a single CPU core.
    counter = 0
    val myDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    withContext(myDispatcher) {
        massiveRun {
            counter++
        }
    }
    println("Counter = $counter")
}

