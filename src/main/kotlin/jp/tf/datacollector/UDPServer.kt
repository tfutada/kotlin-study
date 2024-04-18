package jp.tf.datacollector

import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.io.File
import java.nio.file.Paths

const val LogDir = "logs"  // Directory to store log files
const val LogFileNamePrefix = "${LogDir}/netflow-packet"
const val LogFileName = "${LogFileNamePrefix}.log"
const val S3BucketName = "futa-taka-bucket-1"
const val MaxLogFileSize = 10_000  // Max file size in bytes for log rotation
const val HttpServer = "http://localhost:8080"

@Serializable
data class Post(val title: String, val body: String, val userId: Int)

val counterContext = newSingleThreadContext("CounterContext")

fun main() = runBlocking<Unit> {
    //
    // Directory Watcher
    //
    val path = Paths.get(LogDir)
    val directoryWatcher = DirectoryWatcher(path)

    // Lambda Receiver - define an action when a new log file is created
    directoryWatcher.onCreate {
        val fileName = this.toString()
        println("New file created: $fileName")

        // Upload the file to S3 and Http Post
        CoroutineScope(Dispatchers.IO).launch {
            val metadataVal = mutableMapOf<String, String>()
            metadataVal["fileType"] = "binary"

            val request = PutObjectRequest {
                bucket = S3BucketName
                key = fileName
                metadata = metadataVal
                body = File("${LogDir}/${fileName}").asByteStream()
            }

            awsS3Client {
                val response = putObject(request)
                println("Tag information is ${response.eTag}")
            }

            postClient {
                val response: HttpResponse = post("${HttpServer}/upload") {
                    headers {
                        append(HttpHeaders.Authorization, "abc123")
                        append(HttpHeaders.UserAgent, "ktor client")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(Post("foo", "bar", 1))
                }

                if (response.status.value in 200..299) {
                    println("Success: ${response.bodyAsText()}")
                } else {
                    println("Failed with status code: ${response.status.value}")
                }

            }
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

    var logFile = File(LogFileName)

    val job = CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            val packet = serverSocket.receive()

            launch {
                // Decode the packet data
                val udpPacket = packet.packet.readBytes().decodeToString()

                // Log rotation check and execution
                val ret = withContext(counterContext) {
                    if (logFile.length() > MaxLogFileSize) {
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
//                serverSocket.send(Datagram(ByteReadPacket("Echo->[ $udpPacket".encodeToByteArray()), packet.address))
            }
        }
    }

    job.join()  // Wait for the job to finish to avoid closing the serverSocket too early
}
