package com.example.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class Order(
    val id: Int,
    val userId: Int,
    val status: OrderStatus,
    @Contextual
    val createdAt: LocalDateTime,
    val items: List<OrderItem> = emptyList()
)

enum class OrderStatus {
    PENDING, COMPLETED, CANCELLED
}