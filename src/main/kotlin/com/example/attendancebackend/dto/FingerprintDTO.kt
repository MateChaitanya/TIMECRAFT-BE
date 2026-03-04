package com.example.attendancebackend.dto

data class FingerprintDTO(
    val userId: Long,
    val fingerprintData: String // Could be base64-encoded template or hash
)
