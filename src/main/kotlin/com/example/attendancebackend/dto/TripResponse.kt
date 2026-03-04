package com.example.attendancebackend.dto

import java.time.LocalDateTime

data class TripResponse(
    val tripId: Long,
    val checkInTime: String,      // ISO string
    val checkOutTime: String?,    // Nullable
    val latitude: Double,
    val longitude: Double,
    val workingHours: String? = null,
    val address: String?
)
