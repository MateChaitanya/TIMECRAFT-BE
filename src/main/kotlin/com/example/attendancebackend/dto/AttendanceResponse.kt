package com.example.attendancebackend.dto

import java.time.LocalDateTime

data class AttendanceResponse(
    val status: String,
    val message: String,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val workingHours: String? = null
)
