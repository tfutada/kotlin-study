package jp.tf.channels2

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel


fun main() = runBlocking {
    val channel = Channel<Int>()

    launch {
        // Consumer
        for (y in channel) {
            println(y)
        }
    }

    launch {
        // Producer
        for (x in 1..5) {
            channel.send(x)
        }
        channel.close() // Close the channel, signaling no more sends.
    }

    println("Done")
}
