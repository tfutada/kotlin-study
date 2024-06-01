package jp.tf.jp.tf.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private val bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:29092"

private fun createConsumer(): KafkaConsumer<String, String> {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.GROUP_ID_CONFIG, "simple-consumer-group3")
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")  // To start reading from the beginning of the topic
    }
    return KafkaConsumer(props)
}

fun main() {
    val consumer = createConsumer()
    val topic = "simple-message-topic" // トピック

    consumer.subscribe(listOf(topic)) // Subscribe to the topic

    try {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100)) // Poll for new data
            for (record in records) {
                val timestamp = record.timestamp()
                val dateTime = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                val formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS"))

                println(
                    "Consumed message: ${record.value()} from topic: ${record.topic()}," +
                            " partition: ${record.partition()}, offset: ${record.offset()}," +
                            " timestamp: ${formattedDateTime}"
                )
            }
        }
    } catch (e: Exception) {
        println("Failed to consume message: ${e.message}")
        e.printStackTrace()
    } finally {
        consumer.close()
    }
}
