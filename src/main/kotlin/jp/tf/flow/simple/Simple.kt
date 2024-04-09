package jp.tf.flow.simple

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

suspend fun main() = coroutineScope {
    // consumer
    simpleFlow().collect { value ->
        println(value)
    }
}

// producer
// generates a sequence of values with a delay of 1 second
fun simpleFlow() = flow {
    for (i in 1..10) {
        delay(1000)
        emit(i)  // called when collected. backpressure
    }
}

