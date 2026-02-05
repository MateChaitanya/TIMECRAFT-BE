package com.example.attendancebackend.controller

import com.example.attendancebackend.model.User
import com.example.attendancebackend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class LoginResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val message: String
)

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository
) {

    // SIGNUP
    @PostMapping("/signup")
    fun signup(@RequestBody user: User): ResponseEntity<Map<String, String>> {

        val existingUser = userRepository.findByEmail(user.email)
        if (existingUser != null) {
            return ResponseEntity.badRequest()
                .body(mapOf("message" to "Email already exists"))
        }

        userRepository.save(user)
        return ResponseEntity.ok(mapOf("message" to "Signup successful"))
    }

    // LOGIN
    @PostMapping("/login")
    fun login(@RequestBody user: User): ResponseEntity<LoginResponse> {

        val existingUser = userRepository.findByEmail(user.email)

        return if (existingUser != null && existingUser.password == user.password) {
            ResponseEntity.ok(
                LoginResponse(
                    userId = existingUser.id,
                    name = existingUser.name,
                    email = existingUser.email,
                    message = "Login successful"
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponse(0, "", "", "Invalid credentials"))
        }
    }
}
