package com.example.infrastructure.redis

import com.example.domain.cache.ProductCache
import com.example.domain.model.Product
import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit

class RedisProductCache(
    private val redisson: RedissonClient
) : ProductCache {
    override fun getProduct(id: Int): Product? {
        val bucket = redisson.getBucket<Product>("product:$id")
        return bucket.get()
    }

    override fun putProduct(product: Product) {
        val bucket = redisson.getBucket<Product>("product:${product.id}")
        bucket.set(product, 10, TimeUnit.MINUTES)
    }

    override fun evictProduct(id: Int) {
        redisson.getKeys().delete("product:$id")
    }
}