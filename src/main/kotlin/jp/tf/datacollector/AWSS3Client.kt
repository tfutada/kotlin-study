package jp.tf.datacollector

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import kotlin.time.Duration.Companion.seconds

// Define a higher-order function with a lambda receiver for S3Client operations
suspend fun awsS3Client(block: suspend S3Client.() -> Unit) {
    S3Client {
        region = "us-east-1"
        credentialsProvider = ProfileCredentialsProvider("default")
        httpClient {
            connectTimeout = 10.seconds

            maxConcurrency = 5u
        }
    }.use { s3Client ->
        println("uploading to S3...")
        block(s3Client)
    }
}