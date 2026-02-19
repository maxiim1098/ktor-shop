package com.example.domain.repository

import com.example.domain.model.Order
import com.example.domain.model.OrderItem

interface OrderRepository {
    suspend fun createOrder(userId: Int, items: List<OrderItem>): Order
    suspend fun getOrdersByUser(userId: Int): List<Order>
    suspend fun getOrderById(id: Int): Order?
    suspend fun cancelOrder(id: Int): Boolean
    suspend fun countAll(): Long
    suspend fun sumAllOrderTotals(): Double
}