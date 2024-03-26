package jp.tf.jp.tf.fileio.buffer

import java.io.BufferedReader
import java.io.FileReader

fun streamReadFile(fileName: String) {
    BufferedReader(FileReader(fileName), 4096).use { br ->
        var line: String?
        while (br.readLine().also { line = it } != null) {
            println(line)
        }
    }
}

fun main() {
    streamReadFile("gradle.properties")
}

