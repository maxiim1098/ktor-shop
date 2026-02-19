package com.example.domain.service

import com.example.domain.dto.CreateOrderRequest
import com.example.domain.model.Order
import com.example.domain.model.OrderItem
import com.example.domain.model.OrderStatus
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.AuditLogRepository
import com.example.domain.event.OrderEventPublisher

class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val auditLogRepository: AuditLogRepository,
    private val eventPublisher: OrderEventPublisher
) {
    suspend fun createOrder(userId: Int, request: CreateOrderRequest): Order {
        val orderItems = mutableListOf<OrderItem>()

        for (item in request.items) {
            val product = productRepository.getById(item.productId)
                ?: throw IllegalArgumentException("Product ${item.productId} not found")
            if (product.stock < item.quantity) {
                throw IllegalStateException("Not enough stock for product ${product.id}")
            }
            val success = productRepository.decreaseStock(product.id, item.quantity)
            if (!success) throw IllegalStateException("Failed to reserve stock")

            orderItems.add(
                OrderItem(
                    id = 0,
                    orderId = 0,
                    productId = product.id,
                    quantity = item.quantity,
                    price = product.price
                )
            )
        }

        val order = orderRepository.createOrder(userId, orderItems)
        auditLogRepository.log(userId, "Created order ${order.id}")
        eventPublisher.publishOrderCreated(order)
        return order
    }

    suspend fun getUserOrders(userId: Int): List<Order> =
        orderRepository.getOrdersByUser(userId)

    suspend fun cancelOrder(orderId: Int, userId: Int): Boolean {
        val order = orderRepository.getOrderById(orderId)
            ?: throw IllegalArgumentException("Order not found")
        if (order.userId != userId) throw SecurityException("Access denied")
        if (order.status != OrderStatus.PENDING) {
            throw IllegalStateException("Only pending orders can be cancelled")
        }
        // TODO: вернуть товар на склад
        val cancelled = orderRepository.cancelOrder(orderId)
        if (cancelled) {
            auditLogRepository.log(userId, "Cancelled order $orderId")
        }
        return cancelled
    }
}