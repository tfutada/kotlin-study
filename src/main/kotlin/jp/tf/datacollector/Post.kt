package jp.tf.datacollector

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

@Serializable
data class ApiResponse(
    val holdingIdentityShortHash: String,
    val clientRequestId: String,
    val flowId: String?,
    val flowStatus: String,
    val flowResult: String?,
    val flowError: String?,
    val timestamp: String
)

// singleton pattern for HttpClient
suspend fun postClient(block: suspend HttpClient.() -> Unit) {
    HttpClient(CIO) {
        engine {
            https {
                trustManager = trust
            }
        }
        install(ContentNegotiation) {
            json()
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            requestTimeoutMillis = 10_000
        }
    }.use { postClient ->
        block(postClient)
    }
}

val trust = object : X509TrustManager {
    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}

    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}

    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
}