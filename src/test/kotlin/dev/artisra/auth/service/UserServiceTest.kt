package dev.artisra.auth.service

import dev.artisra.auth.dto.UserRegistrationRequest
import dev.artisra.auth.model.User
import dev.artisra.auth.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.security.crypto.password.PasswordEncoder

@Tag("unit")
class UserServiceTest {

    private val userRepository = mock(UserRepository::class.java)
    private val passwordEncoder = mock(PasswordEncoder::class.java)
    private val userService = UserService(userRepository, passwordEncoder)

    @Test
    fun `registerUser should save user when email is not in use`() {
        val request = UserRegistrationRequest(
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            password = "Password123!"
        )

        `when`(userRepository.findByEmail(request.email)).thenReturn(null)
        `when`(passwordEncoder.encode(request.password)).thenReturn("encodedPassword")
        `when`(userRepository.save(any(User::class.java))).thenAnswer { it.arguments[0] as User }

        val savedUser = userService.registerUser(request)

        assertNotNull(savedUser)
        assertEquals(request.email, savedUser.email)
        assertEquals("encodedPassword", savedUser.password)
        verify(userRepository).save(any(User::class.java))
    }

    @Test
    fun `registerUser should throw exception when email is already in use`() {
        val request = UserRegistrationRequest(
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            password = "Password123!"
        )

        `when`(userRepository.findByEmail(request.email)).thenReturn(User(
            firstName = "Existing",
            lastName = "User",
            email = "john@example.com",
            password = "oldPassword"
        ))

        val exception = assertThrows<IllegalArgumentException> {
            userService.registerUser(request)
        }

        assertEquals("Email already in use", exception.message)
    }
}
