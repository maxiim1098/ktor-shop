package com.example.domain.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.domain.model.Role
import java.util.*

class JwtConfig(
    private val secret: String,
    private val issuer: String,
    private val validityInMs: Long
) {
    fun generateToken(userId: Int, email: String, role: Role): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withClaim("role", role.name)
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(Algorithm.HMAC256(secret))
    }
}