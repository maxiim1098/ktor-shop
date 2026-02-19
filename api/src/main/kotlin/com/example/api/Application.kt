package com.example.api

import com.example.api.plugins.*
import com.example.data.migration.DatabaseMigrator
import com.example.infrastructure.worker.OrderWorker
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.core.di.configureDI

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    DatabaseMigrator.migrate()
    configureDI()
    configureSerialization()
    configureAuth()
    configureSwagger()
    configureRouting()

    // Запускаем воркер только если не в тестовом окружении
    if (System.getProperty("test.env") == null) {
        val orderWorker = OrderWorker()
        orderWorker.start()
    }
}