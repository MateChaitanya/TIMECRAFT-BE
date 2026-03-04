package com.example.attendancebackend.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "attendance")
data class Attendance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val attendanceDate: LocalDate,

    val checkInTime: LocalDateTime,

    var checkOutTime: LocalDateTime? = null,

    var latitude: Double,
    var longitude: Double,

    val userId: Long,

    var workingHours: String? = null,
    var attendanceType: String? = null,

    @Column(name = "address")
    var address: String? = null

)
