package jp.tf.udp.nio

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

fun main() {
    val serverPort = 5106
    val buffer = ByteBuffer.allocateDirect(1024)  // Use a direct buffer for better performance

    DatagramChannel.open().use { channel ->
        channel.bind(InetSocketAddress(serverPort))
        channel.configureBlocking(false)  // Set the channel to non-blocking mode
        println("NIO UDP サーバーが起動しました。ポート: $serverPort")

        while (true) {
            buffer.clear()
            val clientAddress = channel.receive(buffer)  // Non-blocking receive

            if (clientAddress != null) {
                buffer.flip()
                val data = String(buffer.array(), buffer.position(), buffer.limit())
                println("クライアントからのデータ: $data")

                // Prepare to send the response back
                buffer.rewind()  // Rewind buffer to read the same data again for sending
                channel.send(buffer, clientAddress)  // Echo the received data back to the client
            }
            // The loop will continue immediately if no data is available, without waiting.
        }
    }
}
