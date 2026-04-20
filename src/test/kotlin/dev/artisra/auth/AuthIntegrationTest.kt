package dev.artisra.auth

import dev.artisra.auth.dto.UserRegistrationRequest
import dev.artisra.auth.service.JwtService
import dev.artisra.auth.service.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Tag("integration")
@SpringBootTest
class AuthIntegrationTest {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Test
    fun `should register user`() {
        val email = "john.doe.${System.currentTimeMillis()}@example.com"
        val registerRequest = UserRegistrationRequest(
            firstName = "John",
            lastName = "Doe",
            email = email,
            password = "Password123!"
        )

        val user = userService.registerUser(registerRequest)
        assertNotNull(user.uuid)
        assertEquals(email, user.email)
    }

    @Test
    fun `should login and generate jwt`() {
        val email = "jane.doe.${System.currentTimeMillis()}@example.com"
        val registerRequest = UserRegistrationRequest(
            firstName = "Jane",
            lastName = "Doe",
            email = email,
            password = "Password123!"
        )
        userService.registerUser(registerRequest)

        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(email, "Password123!")
        )
        assertNotNull(auth)

        val userDetails = userDetailsService.loadUserByUsername(email)
        val token = jwtService.generateToken(userDetails)
        assertNotNull(token)

        assertTrue(jwtService.isTokenValid(token, userDetails))
        assertEquals(email, jwtService.extractUsername(token))
    }
}
