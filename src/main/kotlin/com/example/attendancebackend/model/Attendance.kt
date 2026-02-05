package com.example.attendancebackend.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "attendance")
class Attendance(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,

    var attendanceType: String,

    var checkInTime: LocalDateTime?,

    var checkOutTime: LocalDateTime?,

    var attendanceDate: LocalDate,

    var workingHours: String?,

    var latitude: Double,

    var longitude: Double
)
