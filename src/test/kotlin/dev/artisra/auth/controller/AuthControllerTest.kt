package dev.artisra.auth.controller

import tools.jackson.databind.ObjectMapper
import dev.artisra.auth.dto.LoginRequest
import dev.artisra.auth.dto.UserRegistrationRequest
import dev.artisra.auth.model.User
import dev.artisra.auth.service.JwtService
import dev.artisra.auth.service.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Tag
import org.springframework.http.HttpStatus
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.junit.jupiter.api.BeforeEach

@Tag("unit")
@SpringBootTest
class AuthControllerTest {

    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userService: UserService

    @MockitoBean
    private lateinit var authenticationManager: AuthenticationManager

    @MockitoBean
    private lateinit var jwtService: JwtService

    @MockitoBean
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var authController: AuthController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> any(): T {
        return org.mockito.ArgumentMatchers.any() as T
    }

    @Test
    fun `register should return 201 when successful`() {
        val request = UserRegistrationRequest(
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            password = "Password123!"
        )
        val savedUser = User(
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            password = "encodedPassword"
        )

        `when`(userService.registerUser(any())).thenReturn(savedUser)

        val response = authController.register(request)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        val body = response.body as Map<*, *>
        assertEquals("User registered successfully", body["message"])
        assertNotNull(body["uuid"])
    }

    @Test
    fun `register should return 400 when email already exists`() {
        val request = UserRegistrationRequest(
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            password = "Password123!"
        )

        `when`(userService.registerUser(any()))
            .thenThrow(IllegalArgumentException("Email already in use"))

        val response = authController.register(request)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val body = response.body as Map<*, *>
        assertEquals("Email already in use", body["error"])
    }

    @Test
    fun `login should return token when successful`() {
        val request = LoginRequest("john@example.com", "Password123!")
        val userDetails = org.springframework.security.core.userdetails.User.builder()
            .username("john@example.com")
            .password("password")
            .authorities("USER")
            .build()

        `when`(userDetailsService.loadUserByUsername(request.email)).thenReturn(userDetails)
        `when`(jwtService.generateToken(userDetails)).thenReturn("mocked-jwt-token")

        val response = authController.login(request)
        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body as dev.artisra.auth.dto.LoginResponse
        assertEquals("mocked-jwt-token", body.token)
    }

    @Test
    fun `login should return 401 when credentials are invalid`() {
        val request = LoginRequest("john@example.com", "wrong-password")

        `when`(authenticationManager.authenticate(any())).thenThrow(RuntimeException("Invalid credentials"))

        val response = authController.login(request)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        val body = response.body as Map<*, *>
        assertEquals("Invalid credentials", body["error"])
    }
}
