package jp.tf.udp.kotlinsdk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.DatagramPacket
import java.net.DatagramSocket

fun main() = runBlocking {
    val serverPort = 5106
    val buffer = ByteArray(1024)  // Buffer size can be adjusted as needed

    val socket = DatagramSocket(serverPort)
    println("非同期UDPサーバーが起動しました。ポート: $serverPort")

    while (true) {
        val packet = DatagramPacket(buffer, buffer.size)
        socket.receive(packet)  // Receive packet from client

        launch(Dispatchers.IO) {  // Handle each packet in a separate coroutine
            val data = String(packet.data, 0, packet.length)
            println("クライアントからのデータ: $data")

            val responseData = "Received: $data".toByteArray()
            val responsePacket = DatagramPacket(
                responseData,
                responseData.size,
                packet.address,
                packet.port
            )

            socket.send(responsePacket)  // Send response back to client
        }
    }
}
