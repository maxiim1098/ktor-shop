package com.example.domain.service

import com.example.domain.repository.OrderRepository

class StatsService(
    private val orderRepository: OrderRepository
) {
    suspend fun getOrdersStats(): Map<String, Any> {
        val totalOrders = orderRepository.countAll()
        val totalRevenue = orderRepository.sumAllOrderTotals()
        return mapOf(
            "totalOrders" to totalOrders,
            "totalRevenue" to totalRevenue
        )
    }
}