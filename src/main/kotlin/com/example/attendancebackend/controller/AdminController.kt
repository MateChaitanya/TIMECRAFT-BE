package com.example.attendancebackend.controller

import com.example.attendancebackend.dto.AdminDashboardResponse
import com.example.attendancebackend.model.User
import com.example.attendancebackend.service.AdminService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService
) {

    // 1️⃣ Get all pending users
    @GetMapping("/pending-users")
    fun getPendingUsers(): List<User> {
        return adminService.getPendingUsers()
    }

    // 2️⃣ Approve user
    @PutMapping("/approve/{userId}")
    fun approveUser(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {

            val user = adminService.approveUser(userId)

            ResponseEntity.ok(user)

        } catch (ex: RuntimeException) {

            ResponseEntity.badRequest().body(
                mapOf(
                    "error" to ex.message
                )
            )
        }
    }

    // 3️⃣ Reject user
    @PutMapping("/reject/{userId}")
    fun rejectUser(@PathVariable userId: Long): User {
        return adminService.rejectUser(userId)
    }

    // 🆕 4️⃣ Dashboard API
    @GetMapping("/dashboard")
    fun getDashboard(): AdminDashboardResponse {
        return adminService.getDashboard()
    }
}