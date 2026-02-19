package com.example.data.repository

import com.example.data.tables.Products
import com.example.data.tables.toProduct
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ProductRepositoryImpl : ProductRepository {
    override suspend fun getAll(): List<Product> = newSuspendedTransaction {
        Products.selectAll().map { it.toProduct() }
    }

    override suspend fun getById(id: Int): Product? = newSuspendedTransaction {
        Products.select { Products.id eq id }
            .map { it.toProduct() }
            .singleOrNull()
    }

    override suspend fun create(name: String, price: Double, stock: Int): Product = newSuspendedTransaction {
        val insert = Products.insert {
            it[Products.name] = name
            it[Products.price] = price.toBigDecimal()
            it[Products.stock] = stock
        }
        insert.resultedValues?.first()?.toProduct() ?: error("Failed to create product")
    }

    override suspend fun update(id: Int, name: String?, price: Double?, stock: Int?): Product? = newSuspendedTransaction {
        val updateCount = Products.update({ Products.id eq id }) {
            name?.let { value -> it[Products.name] = value }
            price?.let { value -> it[Products.price] = value.toBigDecimal() }
            stock?.let { value -> it[Products.stock] = value }
        }
        if (updateCount > 0) getById(id) else null
    }

    override suspend fun delete(id: Int): Boolean = newSuspendedTransaction {
        Products.deleteWhere { Products.id eq id } > 0
    }

    override suspend fun decreaseStock(productId: Int, amount: Int): Boolean = newSuspendedTransaction {
        val product = Products.select { Products.id eq productId }.forUpdate().singleOrNull()
            ?: return@newSuspendedTransaction false
        val currentStock = product[Products.stock]
        if (currentStock < amount) return@newSuspendedTransaction false
        Products.update({ Products.id eq productId }) {
            it[Products.stock] = currentStock - amount
        }
        true
    }
}