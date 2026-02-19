package com.example.data.tables

import com.example.domain.model.Product
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.timestamp

object Products : IntIdTable("products") {
    val name = varchar("name", 255)
    val price = decimal("price", 10, 2)
    val stock = integer("stock")
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp())
}

fun ResultRow.toProduct(): Product = Product(
    id = this[Products.id].value,
    name = this[Products.name],
    price = this[Products.price].toDouble(),
    stock = this[Products.stock]
)