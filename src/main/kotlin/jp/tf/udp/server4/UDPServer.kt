package jp.tf.udp.server4

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import java.util.concurrent.Executors

// N.B. both of them below does not work.
@OptIn(ExperimentalCoroutinesApi::class)
val fileContext = newSingleThreadContext("FileContext")
val myDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

fun main() {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("::", 5106))
    println("Server is listening at ${serverSocket.localAddress}")

    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            // Each packet processing is offloaded to a new coroutine.
            val packet = serverSocket.receive()  // This is a suspending function and will not block the thread.

            launch {
                val message = packet.packet.readUTF8Line()
                // CPU intensive task
                val ret = withContext(myDispatcher) {
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
