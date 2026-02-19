package com.example.core.di

import com.example.data.repository.*
import com.example.domain.cache.ProductCache
import com.example.domain.repository.*
import com.example.domain.service.*
import com.example.domain.util.JwtConfig
import com.example.infrastructure.event.RabbitMQOrderEventPublisher
import com.example.infrastructure.redis.RedisProductCache
import org.koin.dsl.module
import org.redisson.Redisson
import org.redisson.config.Config

val appModule = module {
    // ---------- Репозитории ----------
    single<UserRepository> { UserRepositoryImpl() }
    single<ProductRepository> { ProductRepositoryImpl() }
    single<OrderRepository> { OrderRepositoryImpl() }
    single<AuditLogRepository> { AuditLogRepositoryImpl() }

    // ---------- Redis ----------
    single {
        val redisUrl = System.getenv("REDIS_URL") ?: "redis://localhost:6379"
        val config = Config().apply {
            useSingleServer().address = redisUrl
        }
        Redisson.create(config)
    }
    single<ProductCache> { RedisProductCache(get()) }

    // ---------- RabbitMQ ----------
    single {
        val host = System.getenv("RABBITMQ_HOST") ?: "rabbitmq"
        val port = System.getenv("RABBITMQ_PORT")?.toIntOrNull() ?: 5672
        RabbitMQOrderEventPublisher(host, port)
    }

    // ---------- JWT конфиг ----------
    single {
        JwtConfig(
            secret = "my-ultra-secure-secret",
            issuer = "ktor-shop",
            validityInMs = 3600000
        )
    }

    // ---------- Сервисы ----------
    single<UserService> {
        UserService(
            userRepository = get(),
            jwtConfig = get()
        )
    }
    single<ProductService> {
        ProductService(
            productRepository = get(),
            productCache = get()
        )
    }
    single<OrderService> {
        OrderService(
            orderRepository = get(),
            productRepository = get(),
            auditLogRepository = get(),
            eventPublisher = get<RabbitMQOrderEventPublisher>()
        )
    }
    single<StatsService> {
        StatsService(
            orderRepository = get()
        )
    }
}