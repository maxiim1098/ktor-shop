package com.example.e2e

import com.example.api.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Disabled("Временно отключено из-за проблем с подключением к БД в тестовом окружении")
class ApplicationE2ETest {

    @Test
    fun `health endpoint should return OK`() = testApplication {
        application { module() }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `register endpoint should accept valid user`() = testApplication {
        application { module() }
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"e2e@test.com","password":"123456"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }
}