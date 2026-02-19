package com.example.data.tables

import com.example.domain.model.AuditLog
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.LocalDateTime

object AuditLogs : IntIdTable("audit_logs") {
    val userId = integer("user_id").references(Users.id)
    val action = varchar("action", 255)
    val timestamp = timestamp("timestamp").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp())
}

fun ResultRow.toAuditLog(): AuditLog = AuditLog(
    id = this[AuditLogs.id].value,
    userId = this[AuditLogs.userId],
    action = this[AuditLogs.action],
    timestamp = this[AuditLogs.timestamp] as LocalDateTime
)