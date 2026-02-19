package com.example.data.repository

import com.example.data.tables.*
import com.example.domain.model.Order
import com.example.domain.model.OrderItem
import com.example.domain.model.OrderStatus
import com.example.domain.repository.OrderRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class OrderRepositoryImpl : OrderRepository {
    override suspend fun createOrder(userId: Int, items: List<OrderItem>): Order = newSuspendedTransaction {
        val orderId = Orders.insertAndGetId {
            it[Orders.userId] = userId
            it[Orders.status] = OrderStatus.PENDING.name
        }.value

        val orderItems = items.map { item ->
            val itemId = OrderItems.insertAndGetId {
                it[OrderItems.orderId] = orderId
                it[OrderItems.productId] = item.productId
                it[OrderItems.quantity] = item.quantity
                it[OrderItems.price] = item.price.toBigDecimal()
            }.value
            OrderItem(
                id = itemId,
                orderId = orderId,
                productId = item.productId,
                quantity = item.quantity,
                price = item.price
            )
        }

        Order(
            id = orderId,
            userId = userId,
            status = OrderStatus.PENDING,
            createdAt = LocalDateTime.now(),
            items = orderItems
        )
    }

    override suspend fun getOrdersByUser(userId: Int): List<Order> = newSuspendedTransaction {
        Orders.select { Orders.userId eq userId }
            .map { orderRow ->
                val orderId = orderRow[Orders.id].value
                val items = OrderItems.select { OrderItems.orderId eq orderId }
                    .map { itemRow ->
                        OrderItem(
                            id = itemRow[OrderItems.id].value,
                            orderId = orderId,
                            productId = itemRow[OrderItems.productId],
                            quantity = itemRow[OrderItems.quantity],
                            price = itemRow[OrderItems.price].toDouble()
                        )
                    }
                Order(
                    id = orderId,
                    userId = orderRow[Orders.userId],
                    status = OrderStatus.valueOf(orderRow[Orders.status]),
                    createdAt = (orderRow[Orders.createdAt] as Instant)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                    items = items
                )
            }
    }

    override suspend fun getOrderById(id: Int): Order? = newSuspendedTransaction {
        val orderRow = Orders.select { Orders.id eq id }.singleOrNull() ?: return@newSuspendedTransaction null
        val orderId = orderRow[Orders.id].value
        val items = OrderItems.select { OrderItems.orderId eq orderId }
            .map { itemRow ->
                OrderItem(
                    id = itemRow[OrderItems.id].value,
                    orderId = orderId,
                    productId = itemRow[OrderItems.productId],
                    quantity = itemRow[OrderItems.quantity],
                    price = itemRow[OrderItems.price].toDouble()
                )
            }
        Order(
            id = orderId,
            userId = orderRow[Orders.userId],
            status = OrderStatus.valueOf(orderRow[Orders.status]),
            createdAt = (orderRow[Orders.createdAt] as Instant)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime(),
            items = items
        )
    }

    override suspend fun cancelOrder(id: Int): Boolean = newSuspendedTransaction {
        Orders.update({ Orders.id eq id }) {
            it[Orders.status] = OrderStatus.CANCELLED.name
        } > 0
    }

    override suspend fun countAll(): Long = newSuspendedTransaction {
        Orders.selectAll().count()
    }

    override suspend fun sumAllOrderTotals(): Double = newSuspendedTransaction {
        OrderItems.selectAll()
            .map { it[OrderItems.price].toDouble() * it[OrderItems.quantity] }
            .sum()
    }
}