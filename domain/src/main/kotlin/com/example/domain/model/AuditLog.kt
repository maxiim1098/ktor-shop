package com.example.domain.model

import java.time.LocalDateTime

data class AuditLog(
    val id: Int,
    val userId: Int,
    val action: String,
    val timestamp: LocalDateTime
)