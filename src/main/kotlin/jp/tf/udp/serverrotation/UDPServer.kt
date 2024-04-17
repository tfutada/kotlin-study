package jp.tf.jp.tf.udp.serverrotation

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 1. ログローテーション機能
// 2. フォルダ・ウォッチ機能
// 3. S3ファイル・アップロード機能
//
// 設定したファイルサイズになったら、ファイルをリネームして、新しいファイルを作成する。
// 別スレッドで、フォルダを監視し、ファイルが追加(ログローテーションのタイミング)されたら、そのファイルをS3にアップロードする。
fun main() {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("::", 5106))
    println("Server is listening at ${serverSocket.localAddress}")

    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            // Each packet processing is offloaded to a new coroutine.
            val packet = serverSocket.receive()  // This is a suspending function and will not block the thread.

            launch {
                val message = packet.packet.readBytes().decodeToString()
                // CPU intensive task
                val ret = withContext(Dispatchers.IO) {
                    // write message to a file
                    java.io.File("udp_server3.txt").appendText("$message\n")
                    "Done"
                }

                println("Received from ${packet.address}: $message $ret")
                // optional: echo the message back
                serverSocket.send(Datagram(ByteReadPacket("Echo->[ $message".encodeToByteArray()), packet.address))
            }
        }
    }

    println("Press Enter to exit")
    readln()  // Keep the main thread alive until an Enter is pressed.
}
