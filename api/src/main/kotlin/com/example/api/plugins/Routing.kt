package com.example.api.plugins

import com.example.api.controllers.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoutes()
        authenticate("auth-jwt") {
            productRoutes()
            orderRoutes()
        }
    }
}