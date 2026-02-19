package com.example.data.repository

import com.example.data.tables.Users
import com.example.data.tables.toUser
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.insert

class UserRepositoryImpl : UserRepository {
    override suspend fun findByEmail(email: String): User? = newSuspendedTransaction {
        Users.select { Users.email eq email }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun create(email: String, passwordHash: String, role: String): User = newSuspendedTransaction {
        val insert = Users.insert {
            it[Users.email] = email
            it[Users.passwordHash] = passwordHash
            it[Users.role] = role
        }
        insert.resultedValues?.first()?.toUser() ?: error("Failed to create user")
    }

    override suspend fun findById(id: Int): User? = newSuspendedTransaction {
        Users.select { Users.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }
}