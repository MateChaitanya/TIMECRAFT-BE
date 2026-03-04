package com.example.attendancebackend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "trip")
data class Trip(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val tripId: Long = 0,

    @ManyToOne
    @JoinColumn(name = "employee_id")
    val employee: Employee,

    @Column(name = "check_in_time")
    var checkInTime: LocalDateTime,

    @Column(name = "check_out_time")
    var checkOutTime: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    var status: TripStatus,

    @Column(name = "unit_name")
    var unitName: String? = null,

    @Column(name = "start_time")
    var startTime: LocalDateTime? = null,

    @Column(name = "end_time")
    var endTime: LocalDateTime? = null,


    @Column(name = "working_hours")
    var workingHours: String? = null
)
