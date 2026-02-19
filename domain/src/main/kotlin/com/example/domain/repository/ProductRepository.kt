package com.example.domain.repository

import com.example.domain.model.Product

interface ProductRepository {
    suspend fun getAll(): List<Product>
    suspend fun getById(id: Int): Product?
    suspend fun create(name: String, price: Double, stock: Int): Product
    suspend fun update(id: Int, name: String?, price: Double?, stock: Int?): Product?
    suspend fun delete(id: Int): Boolean
    suspend fun decreaseStock(productId: Int, amount: Int): Boolean
}