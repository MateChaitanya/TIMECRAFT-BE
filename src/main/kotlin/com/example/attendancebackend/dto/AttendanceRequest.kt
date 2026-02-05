package com.example.attendancebackend.dto

data class AttendanceRequest(
    val userId: Long,
    val latitude: Double,
    val longitude: Double
)

