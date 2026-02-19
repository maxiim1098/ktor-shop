package com.example.api.controllers

import com.example.domain.dto.AuthResponse
import com.example.domain.dto.LoginRequest
import com.example.domain.dto.RegisterRequest
import com.example.domain.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val userService: UserService by inject()

    post("/auth/register") {
        val request = call.receive<RegisterRequest>()
        try {
            val token = userService.register(request)
            call.respond(HttpStatusCode.Created, AuthResponse(token))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }

    post("/auth/login") {
        val request = call.receive<LoginRequest>()
        try {
            val token = userService.login(request)
            call.respond(HttpStatusCode.OK, AuthResponse(token))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to e.message))
        }
    }
}