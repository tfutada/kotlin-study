package jp.tf.datacollector

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch

import java.nio.file.Paths

fun main() = runBlocking {
    val path = Paths.get("/Users/tafu/futasoft/kotlin/kotlin-study/logs")
    val directoryWatcher = DirectoryWatcher(path)

    directoryWatcher.onCreate {
        println("New file added: $this")
        // Additional logic can be added here, using `this` to refer to the Path instance
    }

    println("Watching directory. Press enter to exit...")
    launch(Dispatchers.IO) {
        directoryWatcher.watchDirectoryEvents()
    }
    readLine()  // Keep the program running until user input

    println("Exiting...")
}
