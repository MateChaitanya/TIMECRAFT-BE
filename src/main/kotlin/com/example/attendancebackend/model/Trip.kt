package com.example.attendancebackend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "trip")
data class Trip(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    val tripId: Long = 0,

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
    val employee: Employee,

    @Column(name = "unit_id")
    var unitId: Long? = null,

    @Column(name = "start_time")
    var startTime: LocalDateTime? = null,

    @Column(name = "end_time")
    var endTime: LocalDateTime? = null,

    @Column(name = "trip_purpose")
    var tripPurpose: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: TripStatus = TripStatus.Open,

    @Column(name = "check_in_time", nullable = false)
    var checkInTime: LocalDateTime,

    @Column(name = "check_out_time")
    var checkOutTime: LocalDateTime? = null,

    @Column(name = "unit_name", nullable = false)
    var unitName: String,

    @Column(name = "working_hours")
    var workingHours: String? = null
)