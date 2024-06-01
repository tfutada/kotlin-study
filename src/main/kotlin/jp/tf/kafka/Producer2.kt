package jp.tf.jp.tf.kafka


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

private val bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:29092"

data class MyMessage(val city: String, val temperature: Int)

private fun createProducer(): KafkaProducer<String, String> {
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        // Additional producer-specific configurations
        put(ProducerConfig.ACKS_CONFIG, "all")  // Ensure high durability
        put(ProducerConfig.RETRIES_CONFIG, 0)  // If needed, can increase for higher reliability
    }
    return KafkaProducer(props)
}



val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())


fun main() {
    val producer = createProducer()
    val topic = "simple-message-topic" // トピック

    try {
        val msg = MyMessage("Nagoya", 38)
        val msgJson = objectMapper.writeValueAsString(msg)

        val record = ProducerRecord<String, String>(topic, msgJson)
        val metadata = producer.send(record).get()  // 送信する

        println(
            "Message sent to topic ${metadata.topic()} on partition ${metadata.partition()} " +
                    "at offset ${metadata.offset()}"
        )

    } catch (e: Exception) {
        println("Failed to send message: ${e.message}")
        e.printStackTrace()
    } finally {
        producer.close()
    }
}
