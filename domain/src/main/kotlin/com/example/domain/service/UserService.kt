package com.example.domain.service

import com.example.domain.dto.LoginRequest
import com.example.domain.dto.RegisterRequest
import com.example.domain.model.Role
import com.example.domain.repository.UserRepository
import com.example.domain.util.HashUtil
import com.example.domain.util.JwtConfig

class UserService(
    private val userRepository: UserRepository,
    private val jwtConfig: JwtConfig
) {
    suspend fun register(request: RegisterRequest): String {
        require(request.email.isNotBlank()) { "Email cannot be blank" }
        require(request.password.length >= 6) { "Password too short" }

        val existing = userRepository.findByEmail(request.email)
        if (existing != null) throw IllegalArgumentException("Email already exists")

        val passwordHash = HashUtil.hash(request.password)
        val user = userRepository.create(request.email, passwordHash, Role.USER.name)
        return jwtConfig.generateToken(user.id, user.email, user.role)
    }

    suspend fun login(request: LoginRequest): String {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid credentials")
        if (!HashUtil.verify(request.password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        return jwtConfig.generateToken(user.id, user.email, user.role)
    }
}