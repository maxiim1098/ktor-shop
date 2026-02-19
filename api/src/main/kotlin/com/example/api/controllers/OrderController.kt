package com.example.api.controllers

import com.example.domain.dto.CreateOrderRequest
import com.example.domain.service.OrderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.orderRoutes() {
    val orderService: OrderService by inject()

    authenticate("auth-jwt") {
        post("/orders") {
            println(">>> POST /orders called")
            val userId = call.getUserId()
            println(">>> userId = $userId")
            if (userId == null) {
                println(">>> userId is null, returning 401")
                return@post call.respond(HttpStatusCode.Unauthorized, "User not identified")
            }
            val request = try {
                call.receive<CreateOrderRequest>()
            } catch (e: Exception) {
                println(">>> Error parsing CreateOrderRequest: ${e.message}")
                e.printStackTrace()
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
            }
            println(">>> request items size = ${request.items.size}")
            try {
                val order = orderService.createOrder(userId, request)
                println(">>> order created successfully, id = ${order.id}")
                call.respond(HttpStatusCode.Created, order)
            } catch (e: IllegalArgumentException) {
                println(">>> IllegalArgumentException: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: IllegalStateException) {
                println(">>> IllegalStateException: ${e.message}")
                call.respond(HttpStatusCode.Conflict, mapOf("error" to e.message))
            } catch (e: Exception) {
                println(">>> Unexpected exception: ${e.message}")
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
            }
        }

        get("/orders") {
            val userId = call.getUserId()
                ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val orders = orderService.getUserOrders(userId)
            call.respond(orders)
        }

        delete("/orders/{id}") {
            val userId = call.getUserId()
                ?: return@delete call.respond(HttpStatusCode.Unauthorized)
            val orderId = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid id")
            try {
                val cancelled = orderService.cancelOrder(orderId, userId)
                if (cancelled) call.respond(HttpStatusCode.NoContent)
                else call.respond(HttpStatusCode.NotFound)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: SecurityException) {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to e.message))
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to e.message))
            }
        }
    }
}

fun ApplicationCall.getUserId(): Int? {
    val principal = principal<io.ktor.server.auth.jwt.JWTPrincipal>()
    return principal?.payload?.subject?.toIntOrNull()
}