package com.example.attendancebackend.dto

data class UserRequest(
    val name: String,
    val email: String,
    val password: String,      // Added password
    val fingerprint: String
)
