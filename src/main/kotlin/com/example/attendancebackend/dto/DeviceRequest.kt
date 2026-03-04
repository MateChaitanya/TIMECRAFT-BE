package com.example.attendancebackend.dto

data class DeviceRequest(
    val deviceId: String,
    val employeeId: Long,
    val osVersion: String?,
    val imeiOrAndroidId: String?,
    val appIntegrityStatus: String?
)
