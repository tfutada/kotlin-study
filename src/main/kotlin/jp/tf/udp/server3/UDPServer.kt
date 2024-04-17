package jp.tf.jp.tf.udp.server3

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun main() {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("::", 5106))
    println("Server is listening at ${serverSocket.localAddress}")

    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            // Each packet processing is offloaded to a new coroutine.
            val packet = serverSocket.receive()  // This is a suspending function and will not block the thread.

            launch {
                val message = packet.packet.readBytes().decodeToString()
                // CPU intensive task
                val ret = withContext(Dispatchers.IO) {
                    // write message to a file
                    java.io.File("udp_server3.txt").appendText("$message\n")
                    "Done"
                }

                println("Received from ${packet.address}: $message $ret")
                // optional: echo the message back
                serverSocket.send(Datagram(ByteReadPacket("Echo->[ $message".encodeToByteArray()), packet.address))
            }
        }
    }

    println("Press Enter to exit")
    readln()  // Keep the main thread alive until an Enter is pressed.
}
