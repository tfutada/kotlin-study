package jp.tf.jp.tf.flow.buffer

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow

suspend fun main() = coroutineScope {
    // consumer
    simpleFlow().buffer().collect { value ->
        delay(1000)
        println(value)
    }
}

// producer
// generates a sequence of values with a delay of 1 second
fun simpleFlow() = flow {
    for (i in 1..10) {
//        delay(1000)
        println("Emitting $i")
        emit(i)  // called when collected. backpressure
    }
}

