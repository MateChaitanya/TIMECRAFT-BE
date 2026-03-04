package com.example.attendancebackend.dto

data class AttendanceRequest(
    val employeeId: Long,
    val latitude: Double,
    val longitude: Double,

    val deviceId: String,
    val osVersion: String?,
    val imeiOrAndroidId: String?,
    val appIntegrityStatus: String?
)
