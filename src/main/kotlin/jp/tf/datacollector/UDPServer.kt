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
const val MaxLogFileSize = 10_000  // Max file size in bytes for log rotation

const val HttpServer = "https://localhost:8888"

val auth = System.getenv("CORDA_AUTH")!!
val s3BucketName = System.getenv("S3_BUCKET")!!


@Serializable
data class ChatRequest(
    val clientRequestId: String,
    val flowClassName: String,
    val requestBody: ChatDetails
)

@Serializable
data class ChatDetails(
    val chatName: String,
    val otherMember: String,
    val message: String
)

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
                bucket = s3BucketName
                key = fileName
                metadata = metadataVal
                body = File("${LogDir}/${fileName}").asByteStream()
            }

            awsS3Client {
                val response = putObject(request)
                println("Tag information is ${response.eTag}")
            }

            postClient {
                val response: HttpResponse = post("${HttpServer}/api/v1/flow/86F3F0502295") {
                    headers {
                        append(HttpHeaders.Authorization, "Basic $auth")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(
                        ChatRequest(
                            fileName,
                            "com.r3.developers.cordapptemplate.utxoexample.workflows.CreateNewChatFlow",
                            ChatDetails("Chat with Bob", "CN=Bob, OU=Test Dept, O=R3, L=London, C=GB", "Hello Bob")
                        )
                    )
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
                    // TODO: is this a buffere write like 4096 bytes at a time?
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
