package com.example.integration

import com.example.data.repository.ProductRepositoryImpl
import com.example.data.tables.OrderItems
import com.example.data.tables.Products
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.test.runTest
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import javax.sql.DataSource

@Testcontainers
class DatabaseIntegrationTest {

    @Container
    val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
        withDatabaseName("testdb")
        withUsername("test")
        withPassword("test")
    }

    private lateinit var dataSource: DataSource
    private lateinit var repository: ProductRepositoryImpl

    @BeforeEach
    fun setUp() {
        dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = postgres.jdbcUrl
            username = postgres.username
            password = postgres.password
            maximumPoolSize = 2
            driverClassName = "org.postgresql.Driver"
        })
        Database.connect(dataSource)

        Flyway.configure()
            .dataSource(dataSource)
            .load()
            .migrate()

        repository = ProductRepositoryImpl()
    }

    @AfterEach
    fun tearDown() {
        transaction {
            SchemaUtils.drop(OrderItems, Products)
        }
        (dataSource as HikariDataSource).close()
    }

    @Test
    fun `create and find product`() = runTest {
        val product = repository.create("Test Product", 99.99, 10)
        assertEquals("Test Product", product.name)
        assertEquals(99.99, product.price)
        assertEquals(10, product.stock)

        val found = repository.getById(product.id)
        assertNotNull(found)
        assertEquals(product.id, found?.id)
    }

    @Test
    fun `decrease stock`() = runTest {
        val product = repository.create("Test Product", 99.99, 10)
        val success = repository.decreaseStock(product.id, 3)
        assertTrue(success)

        val updated = repository.getById(product.id)
        assertNotNull(updated)
        assertEquals(7, updated?.stock)
    }
}