package jp.tf.kafka

import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration
import java.util.*
import kotlin.system.measureTimeMillis

private val bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:29092"

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

private fun createConsumer(): KafkaConsumer<String, String> {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.GROUP_ID_CONFIG, "kotlin-consumer-group")
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    }
    return KafkaConsumer(props)
}

fun main() {
    val producer = createProducer()
    val topic = "kfk-topic-3" // トピック
    val numMessages = 100_000 // Number of messages to send
    val message = "Hello, Kafka!" // メッセージ

    runBlocking {
        val producerJob = launch {
            try {
                val timeMillis = measureTimeMillis {
                    for (i in 1..numMessages) {
                        val record = ProducerRecord<String, String>(topic, "$message $i")
                        producer.send(record).get()  // 送信する
                    }
                }

                val messagesPerSecond = numMessages / (timeMillis / 1000.0)
                println("Sent $numMessages messages in $timeMillis ms")
                println("Performance: $messagesPerSecond messages/second")

            } catch (e: Exception) {
                println("Failed to send messages: ${e.message}")
                e.printStackTrace()
            } finally {
                producer.close()
            }
        }

        val consumerJob = launch {
            val consumer = createConsumer()
            consumer.subscribe(listOf(topic))
            var consumedMessages = 0
            val startTime = System.currentTimeMillis()

            try {
                while (consumedMessages < numMessages && isActive) {
                    val records = consumer.poll(Duration.ofMillis(100))
                    for (record in records) {
                        consumedMessages++
//                        println("Received message: ${record.value()} from partition: ${record.partition()}, offset: ${record.offset()}")
                    }
                }
            } catch (e: Exception) {
                println("Failed to consume messages: ${e.message}")
                e.printStackTrace()
            } finally {
                val endTime = System.currentTimeMillis()
                val timeMillis = endTime - startTime
                val messagesPerSecond = consumedMessages / (timeMillis / 1000.0)
                println("Consumed $consumedMessages messages in $timeMillis ms")
                println("Performance: $messagesPerSecond messages/second")
                consumer.close()
            }
        }

        joinAll(producerJob, consumerJob)
    }
}


