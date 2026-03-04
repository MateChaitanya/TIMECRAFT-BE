package com.example.attendancebackend.controller.dto

data class CheckInRequest(
    val userId: Int,
    val latitude: Double,
    val longitude: Double
)
