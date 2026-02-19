package com.example.data.repository

import com.example.data.tables.AuditLogs
import com.example.domain.repository.AuditLogRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AuditLogRepositoryImpl : AuditLogRepository {
    override suspend fun log(userId: Int, action: String) {
        newSuspendedTransaction {
            AuditLogs.insert {
                it[AuditLogs.userId] = userId
                it[AuditLogs.action] = action
            }
        }
    }
}