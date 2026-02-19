package com.example.api.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuth() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256("my-ultra-secure-secret"))
                    .withIssuer("ktor-shop")
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.subject.toIntOrNull()
                val email = credential.payload.getClaim("email").asString()
                val role = credential.payload.getClaim("role").asString()
                if (userId != null && email != null && role != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}