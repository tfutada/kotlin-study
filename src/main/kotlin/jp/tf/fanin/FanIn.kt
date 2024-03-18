package jp.tf.fanin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val sharedChannel = Channel<Int>(10) // Create a shared channel for fan-in

    // Launching two producers that send to the same channel
    launchProducer(sharedChannel, 100, 1) // Producer with 100ms delay
    launchProducer(sharedChannel, 300, 1000) // Producer with 300ms delay

    // Consumer
    repeat(20) { // Assuming we want to consume 20 numbers in total
        println("Received ${sharedChannel.receive()}")
        delay(300)
    }

    sharedChannel.close() // Close the channel when we're done

    println("Done")
}

fun CoroutineScope.launchProducer(channel: Channel<Int>, delayTime: Long, start: Int) = launch {
    var number = 1 + start
    while (!channel.isClosedForSend) {
        channel.send(number++)
        delay(delayTime)
    }
}
