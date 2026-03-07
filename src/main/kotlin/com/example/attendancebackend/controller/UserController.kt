package com.example.attendancebackend.controller

import com.example.attendancebackend.dto.UserRequest
import com.example.attendancebackend.model.User
import com.example.attendancebackend.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepo: UserRepository
) {

    data class ApiResponse(val status: String, val message: String)

    @PostMapping("/register")
    fun registerUser(@RequestBody req: UserRequest): ResponseEntity<ApiResponse> {

        if (userRepo.findByEmail(req.email) != null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse("ERROR", "Email already registered"))
        }

        val user = User(
            name = req.name,
            email = req.email,
            password = req.password
        )

        userRepo.save(user)

        return ResponseEntity.ok(ApiResponse("SUCCESS", "User registered successfully"))
    }
}