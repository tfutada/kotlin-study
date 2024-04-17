package jp.tf.datacollector

import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

const val LogDir = "logs"  // Directory to store log files
const val LogFileNamePrefix = "${LogDir}/netflow-packet"
const val LogFileName = "$LogFileNamePrefix.log"
const val S3BucketName = "futa-taka-bucket-1"

fun main() {
    //
    // Directory Watcher
    //
    val path = Paths.get(LogDir)
    val directoryWatcher = DirectoryWatcher(path)

    // Lambda Receiver - define an action when a new log file is created
    directoryWatcher.onCreate {
        val fileName = this.toString()
        println("New file created: $fileName")

        val metadataVal = mutableMapOf<String, String>()
        metadataVal["fileType"] = "binary"

        val request = PutObjectRequest {
            bucket = S3BucketName
            key = fileName
            metadata = metadataVal
            body = File("${LogDir}/${fileName}").asByteStream()
        }

        CoroutineScope(Dispatchers.IO).launch {
            awsS3Client {
                val response = putObject(request)
                println("Tag information is ${response.eTag}")
            }

            // TODO call Corda API to store the file hash and metadata
            // client.post
        }
    }

    // Start watching the log directory
    CoroutineScope(Dispatchers.IO).launch {
        directoryWatcher.watchDirectoryEvents()
    }

    //
    // UDP Server
    //
    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("::", 5106))
    println("Server is listening at ${serverSocket.localAddress}")

    val maxFileSize = 10_000  // Max file size in bytes
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
