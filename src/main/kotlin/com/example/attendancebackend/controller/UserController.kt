package com.example.attendancebackend.controller

import com.example.attendancebackend.dto.UserRequest
import com.example.attendancebackend.model.User
import com.example.attendancebackend.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.MessageDigest

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepo: UserRepository
) {

    data class ApiResponse(val status: String, val message: String)

    // REGISTER USER WITH FINGERPRINT AND PASSWORD
    @PostMapping("/register")
    fun registerUser(@RequestBody req: UserRequest): ResponseEntity<ApiResponse> {
        if (userRepo.findByEmail(req.email) != null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse("ERROR", "Email already registered"))
        }

        val fingerprintHash = hashFingerprint(req.fingerprint)

        val user = User(
            name = req.name,
            email = req.email,
            password = req.password,           // added
            fingerprintHash = fingerprintHash
        )

        userRepo.save(user)

        return ResponseEntity.ok(ApiResponse("SUCCESS", "User registered successfully"))
    }

    private fun hashFingerprint(fingerprint: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(fingerprint.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
