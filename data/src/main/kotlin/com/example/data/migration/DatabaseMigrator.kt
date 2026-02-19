package com.example.data.migration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseMigrator {
    private val logger = LoggerFactory.getLogger(DatabaseMigrator::class.java)

    fun migrate() {
        println(">>> DatabaseMigrator.migrate() started")
        logger.info("Initializing database connection...")

        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/shop"
            username = System.getenv("DB_USER") ?: "postgres"
            password = System.getenv("DB_PASSWORD") ?: "postgres"
            maximumPoolSize = 3
            driverClassName = "org.postgresql.Driver"
            validate()
        }
        val dataSource = HikariDataSource(config)

        Database.connect(dataSource)

        try {
            transaction {
                exec("SELECT 1")
            }
            logger.info("Database connection test successful")
            println(">>> Database connection test successful")
        } catch (e: Exception) {
            logger.error("Database connection test failed", e)
            println(">>> Database connection test FAILED: ${e.message}")
            throw e
        }

        logger.info("Running Flyway migrations...")
        println(">>> Running Flyway migrations...")
        Flyway.configure()
            .dataSource(dataSource)
            .load()
            .migrate()
        logger.info("Flyway migrations completed")
        println(">>> DatabaseMigrator.migrate() finished successfully")
    }
}