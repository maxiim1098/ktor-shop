package com.example.domain.repository

import com.example.domain.model.AuditLog

interface AuditLogRepository {
    suspend fun log(userId: Int, action: String)
}