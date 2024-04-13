package jp.tf.udp.client


import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

fun main() {
    try {
        val serverAddress = InetAddress.getByName("::1")
        val serverPort = 5106

        val largeString = "A".repeat(1024) // 1 KB
        val data = largeString.toByteArray()

        DatagramSocket().use { clientSocket ->
            val packet = DatagramPacket(data, data.size, serverAddress, serverPort)
            for (i in 1..100) {
                clientSocket.send(packet)
//                Thread.sleep(1000)
            }
            // Socket is automatically closed
        }
    } catch (e: Exception) {
        e.printStackTrace() // Or handle the exception as appropriate
    }
}
