package jp.tf.jp.tf.udp.serverrotation

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

const val LogFileNamePrefix = "logs/netflow-packet"
const val LogFileName = "$LogFileNamePrefix.log"

fun main() {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("::", 5106))
    println("Server is listening at ${serverSocket.localAddress}")

    val maxFileSize = 100_000  // Max file size in bytes
    var logFile = File(LogFileName)

    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            val packet = serverSocket.receive()

            launch {
                // Decode the packet data
                val udpPacket = packet.packet.readBytes().decodeToString()

                // Log rotation check and execution
                withContext(Dispatchers.IO) {
                    if (logFile.length() > maxFileSize) {
                        val newFileName = "${LogFileNamePrefix}.${System.currentTimeMillis()}.log"
                        logFile.renameTo(File(newFileName))
                        logFile = File(LogFileName)  // reset log file
                    }

                    // Write message to the log file
                    logFile.appendText("$udpPacket\n")
                }

                println("Received from ${packet.address}: $udpPacket")
                // optional: echo the message back
                serverSocket.send(Datagram(ByteReadPacket("Echo->[ $udpPacket".encodeToByteArray()), packet.address))
            }
        }
    }

    println("Press Enter to exit")
    readln()  // Keep the main thread alive until an Enter is pressed.
}
