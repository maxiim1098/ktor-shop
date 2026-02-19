package com.example.infrastructure.worker

import com.rabbitmq.client.*
import kotlinx.coroutines.*
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeoutException

@OptIn(DelicateCoroutinesApi::class)
class OrderWorker {

    private val rabbitHost = System.getenv("RABBITMQ_HOST") ?: "localhost"
    private val rabbitPort = (System.getenv("RABBITMQ_PORT") ?: "5672").toInt()
    private val queueName = "order_events"

    private val factory = ConnectionFactory().apply {
        host = rabbitHost
        port = rabbitPort
    }

    private val maxRetries = 12
    private val initialDelayMs = 1000L

    private var connection: Connection? = null
    private var channel: Channel? = null

    fun start() {
        GlobalScope.launch {
            try {
                connectWithRetry()
                startConsuming()
            } catch (e: Exception) {
                System.err.println("Failed to start OrderWorker after $maxRetries attempts: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private suspend fun connectWithRetry() {
        var attempt = 0
        var lastException: Exception? = null

        while (attempt < maxRetries) {
            try {
                connection = factory.newConnection()
                channel = connection!!.createChannel()
                channel!!.queueDeclare(queueName, false, false, false, null)
                println("OrderWorker: Connected to RabbitMQ successfully")
                return
            } catch (e: IOException) {
                lastException = e
                println("OrderWorker: Connection attempt ${attempt + 1} failed: ${e.message}")
            } catch (e: TimeoutException) {
                lastException = e
                println("OrderWorker: Timeout attempt ${attempt + 1}: ${e.message}")
            }

            attempt++
            if (attempt < maxRetries) {
                val delayMs = initialDelayMs * (1L shl attempt)
                println("OrderWorker: Retrying in ${delayMs}ms...")
                delay(delayMs)
            }
        }

        throw lastException ?: RuntimeException("Unable to connect to RabbitMQ after $maxRetries attempts")
    }

    private fun startConsuming() {
        val deliverCallback = DeliverCallback { _, delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
            println(" [x] Received '$message'")
            // Здесь можно добавить логику обработки (например, запись в БД, отправка email)
            channel?.basicAck(delivery.envelope.deliveryTag, false)
        }

        val cancelCallback = CancelCallback { _ ->
            println(" [x] Consumer cancelled")
        }

        channel?.basicConsume(queueName, false, deliverCallback, cancelCallback)
        println("OrderWorker: Waiting for messages. To exit press CTRL+C")
    }

    fun stop() {
        try {
            channel?.close()
            connection?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}