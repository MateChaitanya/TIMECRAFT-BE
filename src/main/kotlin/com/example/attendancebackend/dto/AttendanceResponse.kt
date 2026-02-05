package com.example.attendancebackend.dto

import java.time.LocalDateTime

data class AttendanceResponse(
    val status: String,
    val message: String,
    val checkInTime: LocalDateTime? = null,
    val checkOutTime: LocalDateTime? = null,
    val workingHours: String? = null
)
