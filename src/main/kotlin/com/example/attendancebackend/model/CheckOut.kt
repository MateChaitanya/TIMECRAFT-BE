package com.example.attendancebackend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "check_out")
data class CheckOut(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val checkOutId: Long = 0,

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    val trip: Trip,

    val timestamp: LocalDateTime = LocalDateTime.now(),

    val latitude: Double? = null,

    val longitude: Double? = null,

    val deviceId: String? = null
)
