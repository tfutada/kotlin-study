package jp.tf.jp.tf.aws.getobj

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.smithy.kotlin.runtime.content.writeToFile
import jp.tf.jp.tf.aws.putobj.putObjects
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() = runBlocking {
    getObjects("futa-taka-bucket-1", "access-log-20241231.log", "downloaded.log")
}

// Define a higher-order function with a lambda receiver for S3Client operations
suspend fun withS3Client(block: suspend S3Client.() -> Unit) {
    S3Client {
        region = "us-east-1"
        credentialsProvider = ProfileCredentialsProvider("mfa")
    }.use { s3Client ->
        block(s3Client)
    }
}

// get a specified object
suspend fun getObjects(bucketName: String, keyName: String, objectPath: String) {
    val request = GetObjectRequest {
        key = keyName
        bucket = bucketName
    }

    withS3Client {
        getObject(request) { resp ->
            val myFile = File(objectPath)
            resp.body?.writeToFile(myFile)
            println("Successfully read $keyName from $bucketName")
        }
    }
}

