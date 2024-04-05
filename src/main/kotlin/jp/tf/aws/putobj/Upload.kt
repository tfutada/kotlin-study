package jp.tf.jp.tf.aws.putobj

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeout
import java.io.File
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking {
    putObjects("futa-taka-bucket-1", "access-log-20241231.log")
}

// Define a higher-order function with a lambda receiver for S3Client operations
suspend fun withS3Client(block: suspend S3Client.() -> Unit) {
    S3Client {
        region = "us-east-1"
        credentialsProvider = ProfileCredentialsProvider("default")
        httpClient {
            connectTimeout = 10.seconds

            maxConcurrency = 5u
        }
    }.use { s3Client ->
        block(s3Client)
    }
}

suspend fun putObjects(bucketName: String, objectPath: String) {
    val metadataVal = mutableMapOf<String, String>()
    metadataVal["myVal"] = "test123"

    val request = PutObjectRequest {
        bucket = bucketName
        key = objectPath
        metadata = metadataVal
        body = File(objectPath).asByteStream()
    }

    withS3Client {
        val response = putObject(request)
        println("Tag information is ${response.eTag}")
    }
}

