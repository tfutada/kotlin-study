package jp.tf.channels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlin.time.Duration

// There are two producers and one consumer.
// The consumer selects the next number from the first producer or the second producer.
// The two producers produce incremental numbers with different delays.
fun main() = runBlocking {
    val producer1 = produceNumbers(100) // get a producer coroutine
    val producer2 = produceNumbers(300) // get a producer coroutine
    repeat(10) {
        val nextNumber = select<String> {
            producer1.onReceive { it -> "$it from first" }
            producer2.onReceive { it -> "$it from second" }
        }

        println("Received $nextNumber")
    }

    producer1.cancel() // cancel producer coroutine because we don't need it anymore
    producer2.cancel() // cancel producer coroutine because we don't need it anymore
}

fun CoroutineScope.produceNumbers(duration: Long) = produce<Int> {
    var x = 1 // start from 1
    while (true) {
        send(x++) // produce next
        delay(duration) // wait 0.1s
    }
}

