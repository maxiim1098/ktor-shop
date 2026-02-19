package com.example.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItemRequest>
)

@Serializable
data class OrderItemRequest(
    val productId: Int,
    val quantity: Int
)