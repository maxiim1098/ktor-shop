package com.example.api.controllers

import com.example.domain.cache.ProductCache
import com.example.domain.dto.CreateProductRequest
import com.example.domain.dto.UpdateProductRequest
import com.example.domain.service.ProductService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.productRoutes() {
    val productService: ProductService by inject()
    val productCache: ProductCache by inject()

    get("/products") {
        println(">>> GET /products called")
        val principal = call.principal<JWTPrincipal>()
        println(">>> principal = $principal")
        if (principal == null) {
            println(">>> principal is null, returning 401")
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }
        val products = productService.getAll()
        call.respond(products)
    }

    get("/products/{id}") {
        println(">>> GET /products/{id} called")
        val principal = call.principal<JWTPrincipal>()
        println(">>> principal = $principal")
        if (principal == null) {
            println(">>> principal is null, returning 401")
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid id")
            return@get
        }
        var product = productCache.getProduct(id)
        if (product == null) {
            product = productService.getById(id)
            if (product != null) {
                productCache.putProduct(product)
            }
        }
        if (product == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(product)
        }
    }

    post("/products") {
        println(">>> POST /products called")
        val principal = call.principal<JWTPrincipal>()
        println(">>> principal = $principal")
        if (principal == null) {
            println(">>> principal is null, returning 401")
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }
        val role = principal.payload.getClaim("role").asString()
        println(">>> role from token = $role")
        if (role != "ADMIN") {
            println(">>> role is $role, not ADMIN, returning 403")
            call.respond(HttpStatusCode.Forbidden, "Admin only")
            return@post
        }
        val request = call.receive<CreateProductRequest>()
        try {
            val product = productService.create(request)
            call.respond(HttpStatusCode.Created, product)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }

    put("/products/{id}") {
        println(">>> PUT /products/{id} called")
        val principal = call.principal<JWTPrincipal>()
        if (principal == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@put
        }
        val role = principal.payload.getClaim("role").asString()
        if (role != "ADMIN") {
            call.respond(HttpStatusCode.Forbidden, "Admin only")
            return@put
        }
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid id")
            return@put
        }
        val request = call.receive<UpdateProductRequest>()
        try {
            val updated = productService.update(id, request)
            if (updated != null) {
                productCache.evictProduct(id)
                call.respond(updated)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }

    delete("/products/{id}") {
        println(">>> DELETE /products/{id} called")
        val principal = call.principal<JWTPrincipal>()
        if (principal == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@delete
        }
        val role = principal.payload.getClaim("role").asString()
        if (role != "ADMIN") {
            call.respond(HttpStatusCode.Forbidden, "Admin only")
            return@delete
        }
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid id")
            return@delete
        }
        val deleted = productService.delete(id)
        if (deleted) {
            productCache.evictProduct(id)
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}