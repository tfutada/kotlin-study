package jp.tf.jp.tf.sha256

import java.security.MessageDigest

fun String.toSHA256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") {
        "%02x".format(it)
    }
}

fun main() {
    val originalString = "Tech Guru at your service!"
    val hashedString = originalString.toSHA256()

    println("Original: $originalString")
    println("SHA-256 Hashed: $hashedString")
}

