package jp.tf.jp.tf.sha256stream

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

fun hashFile(filePath: String, algorithm: String = "SHA-256"): String {
    val buffer = ByteArray(1024) // Buffer size of 1KB
    val messageDigest = MessageDigest.getInstance(algorithm)

    FileInputStream(File(filePath)).use { fis ->
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            messageDigest.update(buffer, 0, bytesRead)
        }
    }

    val digestBytes = messageDigest.digest()
    return digestBytes.joinToString("") { "%02x".format(it) }
}

fun main() {
    val filePath = "largefile.dat"
    val hashed = hashFile(filePath)
    println("SHA-256 Hash: $hashed")
}
