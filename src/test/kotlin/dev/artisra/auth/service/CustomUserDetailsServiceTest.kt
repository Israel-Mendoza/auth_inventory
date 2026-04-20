package dev.artisra.auth.service

import dev.artisra.auth.model.User
import dev.artisra.auth.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Tag("unit")
class CustomUserDetailsServiceTest {

    private val userRepository = mock(UserRepository::class.java)
    private val userDetailsService = CustomUserDetailsService(userRepository)

    @Test
    fun `loadUserByUsername should return user details when user exists`() {
        val email = "test@example.com"
        val user = User(
            firstName = "Test",
            lastName = "User",
            email = email,
            password = "encodedPassword"
        )

        `when`(userRepository.findByEmail(email)).thenReturn(user)

        val userDetails = userDetailsService.loadUserByUsername(email)

        assertEquals(email, userDetails.username)
        assertEquals("encodedPassword", userDetails.password)
    }

    @Test
    fun `loadUserByUsername should throw exception when user does not exist`() {
        val email = "nonexistent@example.com"
        `when`(userRepository.findByEmail(email)).thenReturn(null)

        assertThrows<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername(email)
        }
    }
}
