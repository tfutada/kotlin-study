package jp.tf.jp.tf.datacollector

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import jp.tf.datacollector.DirectoryWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Paths

const val LogFileNamePrefix = "logs/netflow-packet"
const val LogFileName = "$LogFileNamePrefix.log"

fun main() {
    //
    // Directory Watcher
    //
    val path = Paths.get("logs")
    val directoryWatcher = DirectoryWatcher(path)

    directoryWatcher.onCreate {
        println("New file added: $this")
    }

    CoroutineScope(Dispatchers.IO).launch {
        directoryWatcher.watchDirectoryEvents()
    }

    //
    // UDP Server
    //
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

//                println("Received from ${packet.address}: $udpPacket")
                println("Received from ${packet.address}")
                // optional: echo the message back
                serverSocket.send(Datagram(ByteReadPacket("Echo->[ $udpPacket".encodeToByteArray()), packet.address))
            }
        }
    }

    println("Press Enter to exit")
    readln()  // Keep the main thread alive until an Enter is pressed.
}
