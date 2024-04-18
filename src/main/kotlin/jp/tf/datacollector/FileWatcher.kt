package jp.tf.datacollector

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*

class DirectoryWatcher(private val path: Path) {
    private val watcher = path.fileSystem.newWatchService()
    var onCreate: Path.() -> Unit = {}  // Using a lambda receiver here

    init {
        path.register(watcher, ENTRY_CREATE)
        // print the full path to watch
        println("Watching directory at ${path.toAbsolutePath()}")
    }

    fun onCreate(action: Path.() -> Unit) {
        onCreate = action
    }

    suspend fun watchDirectoryEvents() = coroutineScope {
        val eventChannel = Channel<WatchEvent<*>>()
        launch(Dispatchers.IO) {
            try {
                while (!eventChannel.isClosedForSend) {
                    val key: WatchKey = watcher.take()
                    key.pollEvents().forEach {
                        eventChannel.send(it)
                    }
                    key.reset()
                }
            } catch (e: InterruptedException) {
                eventChannel.close()
            } finally {
                watcher.close()
            }
        }

        launch {
            for (event in eventChannel) {
                val kind = event.kind()
                if (kind == OVERFLOW) {
                    continue
                }

                @Suppress("UNCHECKED_CAST")
                val ev = event as WatchEvent<Path>
                val filename = ev.context()

                when (kind) {
                    ENTRY_CREATE -> filename.onCreate()  // Calling the lambda receiver
                    else -> println("Event kind: $kind at $filename")
                }
            }
        }
    }
}
