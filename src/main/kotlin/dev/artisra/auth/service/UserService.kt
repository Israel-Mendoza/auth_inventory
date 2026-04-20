package dev.artisra.auth.service

import dev.artisra.auth.dto.UserRegistrationRequest
import dev.artisra.auth.model.User
import dev.artisra.auth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun registerUser(request: UserRegistrationRequest): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email already in use")
        }

        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = passwordEncoder.encode(request.password)!!
        )

        return userRepository.save(user)
    }
}
