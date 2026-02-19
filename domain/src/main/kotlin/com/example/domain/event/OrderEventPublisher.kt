package com.example.domain.event

import com.example.domain.model.Order

interface OrderEventPublisher {
    suspend fun publishOrderCreated(order: Order)
}