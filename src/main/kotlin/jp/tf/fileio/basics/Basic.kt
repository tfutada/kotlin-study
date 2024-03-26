package jp.tf.jp.tf.fileio.basics

import java.io.File

fun main() {
    val contentToWrite = "Kotlin is awesome!\n"
    File("output.txt").writeText(contentToWrite)

    val readContent = File("output.txt").readText()
    println(readContent)
}

