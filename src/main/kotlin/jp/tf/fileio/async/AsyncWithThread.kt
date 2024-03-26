package jp.tf.jp.tf.fileio.async

import kotlinx.coroutines.*
import java.io.File

suspend fun asyncReadFile(fileName: String): String = withContext(Dispatchers.IO) {
    File(fileName).readText()
}

fun main() = runBlocking {
    val content = async { asyncReadFile("gradle.properties") }
    println("Do something else")
    println(content.await())
}


