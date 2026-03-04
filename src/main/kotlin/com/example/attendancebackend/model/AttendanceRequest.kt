package com.example.attendancebackend.model

data class AttendanceRequest(
    val userId: Long,
    val latitude: Double,
    val longitude: Double,
    val deviceId: String,
    val osVersion: String?,
    val imeiOrAndroidId: String?,
    val appIntegrityStatus: String?
)


