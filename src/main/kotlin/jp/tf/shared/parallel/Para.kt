package jp.tf.shared.parallel

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

var counter = 0

// see the difference between the two runs: 1. multiple CPU cores vs. 2. a single CPU core.
suspend fun main(): Unit = runBlocking {
    // 1. run on multiple CPU cores
    withContext(Dispatchers.Default) {
        massiveRun {
            counter++
        }
    }
    log("Counter = $counter") // race condition may occur

    // 2. run on a single CPU core.
    counter = 0
    val myDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    withContext(myDispatcher) {
        massiveRun {
            counter++
        }
    }
    log("Counter(single) = $counter") // race condition will NOT occur

    // 3. run on the main thread
    counter = 0
    massiveRun {
        counter++
    }
    log("Counter(main) = $counter") // race condition will NOT occur

}

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
    log("Completed ${n * k} actions in $time ms")
}
