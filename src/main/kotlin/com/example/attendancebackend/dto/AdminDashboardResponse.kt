package com.example.attendancebackend.dto

data class AdminDashboardResponse(
    val totalEmployees: Long,
    val presentToday: Long,
    val absentToday: Long,
    val pendingUsers: Long
)