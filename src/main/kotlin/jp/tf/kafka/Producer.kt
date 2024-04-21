package jp.tf.kafka


import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

val bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:29092"

fun createProducer(): KafkaProducer<String, String> {
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


fun main() {
    val producer = createProducer()
    val topic = "simple-message-topic"

    try {
        // Example of sending a simple message
        val message = "Hello, Kafka!"
        val record = ProducerRecord<String, String>(topic, message)
        val metadata = producer.send(record).get()  // `get()` to ensure synchronous send

        println("Message sent to topic ${metadata.topic()} on partition ${metadata.partition()} at offset ${metadata.offset()}")
    } catch (e: Exception) {
        println("Failed to send message: ${e.message}")
        e.printStackTrace()
    } finally {
        producer.close()
    }
}
