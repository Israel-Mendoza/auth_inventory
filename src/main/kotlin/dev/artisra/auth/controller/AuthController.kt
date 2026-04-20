package dev.artisra.auth.controller

import dev.artisra.auth.dto.LoginRequest
import dev.artisra.auth.dto.LoginResponse
import dev.artisra.auth.dto.UserRegistrationRequest
import dev.artisra.auth.service.JwtService
import dev.artisra.auth.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: UserRegistrationRequest): ResponseEntity<Any> {
        return try {
            val user = userService.registerUser(request)
            ResponseEntity.status(HttpStatus.CREATED).body(mapOf(
                "message" to "User registered successfully",
                "uuid" to user.uuid
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
            val userDetails = userDetailsService.loadUserByUsername(request.email)
            val jwt = jwtService.generateToken(userDetails)
            ResponseEntity.ok(LoginResponse(jwt))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Invalid credentials"))
        }
    }

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Any> {
        return ResponseEntity.ok(mapOf(
            "email" to userDetails.username,
            "authorities" to userDetails.authorities
        ))
    }
}
