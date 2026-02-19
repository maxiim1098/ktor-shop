package com.example.domain.cache

import com.example.domain.model.Product

interface ProductCache {
    fun getProduct(id: Int): Product?
    fun putProduct(product: Product)
    fun evictProduct(id: Int)
}