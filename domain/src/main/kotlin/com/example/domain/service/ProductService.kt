package com.example.domain.service

import com.example.domain.cache.ProductCache
import com.example.domain.dto.CreateProductRequest
import com.example.domain.dto.UpdateProductRequest
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository

class ProductService(
    private val productRepository: ProductRepository,
    private val productCache: ProductCache
) {
    suspend fun getAll(): List<Product> = productRepository.getAll()

    suspend fun getById(id: Int): Product? {
        val cached = productCache.getProduct(id)
        if (cached != null) return cached
        val product = productRepository.getById(id)
        if (product != null) productCache.putProduct(product)
        return product
    }

    suspend fun create(request: CreateProductRequest): Product {
        require(request.name.isNotBlank()) { "Product name required" }
        require(request.price > 0) { "Price must be positive" }
        require(request.stock >= 0) { "Stock cannot be negative" }
        return productRepository.create(request.name, request.price, request.stock)
    }

    suspend fun update(id: Int, request: UpdateProductRequest): Product? {
        val updated = productRepository.update(id, request.name, request.price, request.stock)
        if (updated != null) productCache.evictProduct(id)
        return updated
    }

    suspend fun delete(id: Int): Boolean = productRepository.delete(id)
}