package com.example.data.tables

import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.LocalDateTime

object Orders : IntIdTable("orders") {
    val userId = integer("user_id").references(Users.id)
    val status = varchar("status", 50)
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp())
}

fun ResultRow.toOrder(): Order = Order(
    id = this[Orders.id].value,
    userId = this[Orders.userId],
    status = OrderStatus.valueOf(this[Orders.status]),
    createdAt = this[Orders.createdAt] as LocalDateTime
)