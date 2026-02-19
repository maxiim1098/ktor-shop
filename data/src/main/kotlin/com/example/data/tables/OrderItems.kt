package com.example.data.tables

import com.example.domain.model.OrderItem
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import java.math.BigDecimal

object OrderItems : IntIdTable("order_items") {
    val orderId = integer("order_id").references(Orders.id)
    val productId = integer("product_id").references(Products.id)
    val quantity = integer("quantity")
    val price = decimal("price", 10, 2)
}

fun ResultRow.toOrderItem(): OrderItem = OrderItem(
    id = this[OrderItems.id].value,
    orderId = this[OrderItems.orderId],
    productId = this[OrderItems.productId],
    quantity = this[OrderItems.quantity],
    price = this[OrderItems.price].toDouble()
)