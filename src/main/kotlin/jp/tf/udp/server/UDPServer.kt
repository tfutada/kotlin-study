package jp.tf.udp.server

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*


fun main() {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("::", 5106))
    println("Kotlin UDP Server is listening at ${serverSocket.localAddress}")

    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            // Each packet processing is offloaded to a new coroutine.
            val packet = serverSocket.receive()  // This is a suspending function and will not block the thread.

            launch {
                val message = packet.packet.readUTF8Line()
                // async sleep
                delay(3000)
                println("Received from ${packet.address}: $message")
                // optional: echo the message back
                serverSocket.send(Datagram(ByteReadPacket("Echo->[ $message".encodeToByteArray()), packet.address))
            }
        }
    }

    println("Press Enter to exit")
    readln()  // Keep the main thread alive until an Enter is pressed.
}
