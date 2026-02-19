package com.example.unit

import com.example.domain.dto.LoginRequest
import com.example.domain.dto.RegisterRequest
import com.example.domain.model.Role
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.domain.service.UserService
import com.example.domain.util.HashUtil
import com.example.domain.util.JwtConfig
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var jwtConfig: JwtConfig
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        jwtConfig = mockk(relaxed = true)
        userService = UserService(userRepository, jwtConfig)

        mockkObject(HashUtil)
        coEvery { HashUtil.verify(any(), any()) } returns true
        coEvery { HashUtil.hash(any()) } returns "hashedPassword"
    }

    @Test
    fun `register should return token`() = runTest {
        val request = RegisterRequest("test@test.com", "password")
        coEvery { userRepository.findByEmail(request.email) } returns null
        coEvery { userRepository.create(any(), any(), Role.USER.name) } returns User(1, request.email, "hashedPassword", Role.USER)
        coEvery { jwtConfig.generateToken(any(), any(), any()) } returns "fake.jwt.token"

        val token = userService.register(request)

        assertEquals("fake.jwt.token", token)
        coVerify { userRepository.create(any(), "hashedPassword", Role.USER.name) }
    }

    @Test
    fun `register should throw if email exists`() = runTest {
        val request = RegisterRequest("existing@test.com", "password")
        coEvery { userRepository.findByEmail(request.email) } returns User(1, request.email, "hash", Role.USER)

        assertFailsWith<IllegalArgumentException> {
            userService.register(request)
        }
    }

    @Test
    fun `login should return token`() = runTest {
        val request = LoginRequest("test@test.com", "password")
        val user = User(1, request.email, "hashedPassword", Role.USER)
        coEvery { userRepository.findByEmail(request.email) } returns user
        coEvery { jwtConfig.generateToken(any(), any(), any()) } returns "fake.jwt.token"

        val token = userService.login(request)

        assertEquals("fake.jwt.token", token)
    }

    @Test
    fun `login should throw if wrong password`() = runTest {
        val request = LoginRequest("test@test.com", "wrong")
        val user = User(1, request.email, "hashedPassword", Role.USER)
        coEvery { userRepository.findByEmail(request.email) } returns user
        coEvery { HashUtil.verify("wrong", "hashedPassword") } returns false

        assertFailsWith<IllegalArgumentException> {
            userService.login(request)
        }
    }
}