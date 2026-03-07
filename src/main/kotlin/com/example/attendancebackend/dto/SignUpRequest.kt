package com.example.attendancebackend.dto

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val employeeId: Long
)
