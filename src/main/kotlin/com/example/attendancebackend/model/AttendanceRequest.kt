package com.example.attendancebackend.model

data class AttendanceRequest(
    val userId: Long,
    val latitude: Double,
    val longitude: Double,
    val workingHours: Double? = null
)
