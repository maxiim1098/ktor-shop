package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val role: Role
)

enum class Role {
    USER, ADMIN
}