package com.example.attendancebackend.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "device_change_request")
data class DeviceChangeRequest(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "employee_id")
    val employee: Employee,

    val oldDeviceId: String,
    val newDeviceId: String,

    @Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.PENDING,

    val requestedAt: LocalDateTime = LocalDateTime.now(),

    var approvedAt: LocalDateTime? = null,

    // ✅ Add validDate for temporary usage
    var validDate: LocalDate? = null
)