package com.example.attendancebackend.dto

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String   // ✅ ADD THIS
)