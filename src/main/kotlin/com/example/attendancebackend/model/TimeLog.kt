package com.example.attendancebackend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "time_log")
data class TimeLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val timeLogId: Long = 0,

    @ManyToOne
    @JoinColumn(name = "trip_id")
    val trip: Trip,

    @Column(name = "check_in_time")
    var checkInTime: LocalDateTime,

    @Column(name = "check_out_time")
    var checkOutTime: LocalDateTime? = null,

    var latitude: Double? = null,
    var longitude: Double? = null,
    var durationMinutes: Int? = null
)
