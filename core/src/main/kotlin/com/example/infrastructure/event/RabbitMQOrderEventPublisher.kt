package com.example.infrastructure.event

import com.example.domain.event.OrderEventPublisher
import com.example.domain.model.Order
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

class RabbitMQOrderEventPublisher(
    private val host: String = System.getenv("RABBITMQ_HOST") ?: "rabbitmq",
    private val port: Int = System.getenv("RABBITMQ_PORT")?.toIntOrNull() ?: 5672
) : OrderEventPublisher {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    init {
        logger.info("RabbitMQOrderEventPublisher created with host=$host, port=$port")
    }

    private val connection: Connection by lazy {
        logger.info("Creating new RabbitMQ connection to $host:$port")
        ConnectionFactory().apply {
            this.host = this@RabbitMQOrderEventPublisher.host
            this.port = this@RabbitMQOrderEventPublisher.port
        }.newConnection()
    }

    override suspend fun publishOrderCreated(order: Order) {
        withContext(Dispatchers.IO) {
            connection.createChannel().use { channel ->
                channel.queueDeclare("orders", false, false, false, null)
                val message = objectMapper.writeValueAsString(order)
                logger.info("Publishing order to RabbitMQ: $message")
                channel.basicPublish("", "orders", null, message.toByteArray())
            }
        }
    }
}
