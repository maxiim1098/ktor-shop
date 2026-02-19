package com.example.data.tables

import com.example.domain.model.Role
import com.example.domain.model.User
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.timestamp

object Users : IntIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 50)
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentTimestamp())
}

fun ResultRow.toUser(): User = User(
    id = this[Users.id].value,
    email = this[Users.email],
    passwordHash = this[Users.passwordHash],
    role = Role.valueOf(this[Users.role])
)