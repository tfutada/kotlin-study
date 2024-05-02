package jp.tf.datacollector

import aws.sdk.kotlin.services.s3.model.*
import aws.smithy.kotlin.runtime.content.asByteStream
import io.ktor.client.call.*
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
import java.util.*


const val LogDir = "logs"  // Directory to store log files
const val LogFileNamePrefix = "${LogDir}/netflow-packet"
const val LogFileName = "${LogFileNamePrefix}.log"
const val MaxLogFileSize = 10_000  // Max file size in bytes for log rotation

const val HttpServer = "https://localhost:8888"

val auth = System.getenv("AUTH")!!
val hashId = System.getenv("HASH_ID")!!
val flowName = System.getenv("FLOW_NAME")!!
val s3BucketName =
    System.getenv("S3_BUCKET") ?: throw IllegalStateException("S3_BUCKET environment variable not set")

//val s3BucketName: String by lazy {
//    val uuid = UUID.randomUUID().toString()
//    "$bucketNamePrefix-$uuid"
//}

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

fun getHexFromBase64Sha256(base64Sha256: String?): String =
    base64Sha256?.let {
        Base64.getDecoder().decode(it)
    }?.joinToString("") { byte ->
        "%02x".format(byte)
    } ?: throw IllegalStateException("SHA256 hash is null")

fun main() = runBlocking<Unit> {
    // NB. can NOT delete buckets with objects in it.
//    awsS3Client {
//        val listBucketsResponse = listBuckets()
//        listBucketsResponse.buckets?.forEach { bucket ->
//            bucket.name?.let { bucketName ->
//                if (bucketName.startsWith(bucketNamePrefix)) {
//                    val deleteRequest = DeleteBucketRequest {
//                        this.bucket = bucketName
//                    }
//                    deleteBucket(deleteRequest)
//                    println("S3 Bucket deleted: $bucketName")
//                }
//            }
//        }
//    }

    // create a S3 bucket every time the program starts
    awsS3Client {
        val request = CreateBucketRequest {
            bucket = s3BucketName
        }
        createBucket(request)
        println("S3 Bucket created: $s3BucketName")
    }

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
            metadataVal["clientId"] = fileName

            var sha256Hash: String? = null
            awsS3Client {
                val request = PutObjectRequest {
                    bucket = s3BucketName
                    key = fileName
                    metadata = metadataVal
                    body = File("${LogDir}/${fileName}").asByteStream()
                    checksumAlgorithm = (ChecksumAlgorithm.Sha256)
                }
                val response = putObject(request)
                sha256Hash = response.checksumSha256
                println("SHA256 hash is $sha256Hash")
            }

            var resp: ApiResponse? = null

            val clientId = getHexFromBase64Sha256(sha256Hash)
            println("clientId : $clientId")

            postClient {
                val response: HttpResponse = post("${HttpServer}/api/v1/flow/${hashId}") {
                    headers {
                        append(HttpHeaders.Authorization, "Basic $auth")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(
                        ChatRequest(
                            clientId,
                            flowName,
                            ChatDetails(
                                "Chat test",
                                "CN=Bob, OU=Test Dept, O=R3, L=London, C=GB",
                                "Hello!"
                            )
                        )
                    )
                }

                if (response.status.value in 200..299) {  // actually 201 should be returned.
                    resp = response.body<ApiResponse>()
                    println("Success: $resp")
                } else {
                    println("Failed with status code: ${response.status.value}")
                }
            }

            // should i simply serialize resp and store it as a string?
            // what if value is null? should i store it as "null"? will aws s3 tag accept null???
            awsS3Client {
                val t =
                    Tagging {
                        tagSet = listOf(
                            Tag {
                                key = "clientId"
                                value = clientId
                            },
                            Tag {
                                key = "flowId"
                                value = resp?.flowId ?: "null"
                            },
                            Tag {
                                key = "txStartTime"
                                value = resp?.timestamp ?: "null"
                            },
                            Tag {
                                key = "status"
                                value = resp?.flowStatus ?: "failed"
                            }
                        )
                    }

                println("updating a S3 tag: $t")

                val putObjectTaggingRequest = PutObjectTaggingRequest {
                    bucket = s3BucketName
                    key = fileName
                    tagging = t
                }

                putObjectTagging(putObjectTaggingRequest)
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
                    // TODO: is this a buffer write like 4096 bytes at a time?
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
