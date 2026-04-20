package dev.artisra.auth.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.User
import java.util.*

@Tag("unit")
class JwtServiceTest {

    private lateinit var jwtService: JwtService
    private val secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
    private val expiration = 3600000L

    @BeforeEach
    fun setUp() {
        jwtService = JwtService(secret, expiration)
    }

    @Test
    fun `should generate a valid token`() {
        val userDetails = User.builder()
            .username("test@example.com")
            .password("password")
            .authorities("USER")
            .build()

        val token = jwtService.generateToken(userDetails)

        assertNotNull(token)
        assertEquals("test@example.com", jwtService.extractUsername(token))
        assertTrue(jwtService.isTokenValid(token, userDetails))
    }

    @Test
    fun `should invalid token for different user`() {
        val userDetails1 = User.builder()
            .username("user1@example.com")
            .password("password")
            .authorities("USER")
            .build()

        val userDetails2 = User.builder()
            .username("user2@example.com")
            .password("password")
            .authorities("USER")
            .build()

        val token = jwtService.generateToken(userDetails1)

        assertFalse(jwtService.isTokenValid(token, userDetails2))
    }

    @Test
    fun `should include extra claims`() {
        val userDetails = User.builder()
            .username("test@example.com")
            .password("password")
            .authorities("USER")
            .build()
        val extraClaims = mapOf("role" to "ADMIN")

        val token = jwtService.generateToken(extraClaims, userDetails)

        assertEquals("ADMIN", jwtService.extractClaim(token) { it["role"] })
    }
}
