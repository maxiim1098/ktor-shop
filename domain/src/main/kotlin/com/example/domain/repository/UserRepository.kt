package com.example.domain.repository

import com.example.domain.model.User

interface UserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun create(email: String, passwordHash: String, role: String): User
    suspend fun findById(id: Int): User?
}