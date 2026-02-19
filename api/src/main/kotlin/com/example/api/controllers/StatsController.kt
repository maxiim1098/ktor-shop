package com.example.api.controllers

import com.example.domain.service.StatsService
import com.example.api.plugins.hasRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminRoutes() {
    val statsService: StatsService by inject()

    authenticate("auth-jwt") {
        get("/stats/orders") {
            if (!call.hasRole("ADMIN")) {
                return@get call.respond(HttpStatusCode.Forbidden)
            }
            val stats = statsService.getOrdersStats()
            call.respond(stats)
        }
    }
}