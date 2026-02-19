package com.example.api.plugins

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal

fun ApplicationCall.hasRole(role: String): Boolean {
    val principal = principal<JWTPrincipal>()
    return principal?.payload?.getClaim("role")?.asString() == role
}

fun ApplicationCall.getUserId(): Int? {
    val principal = principal<JWTPrincipal>()
    return principal?.payload?.subject?.toIntOrNull()
}