package com.example.attendancebackend.model

import jakarta.persistence.*
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "fingerprint_hash", nullable = false)
    var fingerprintHash: String,

    @Column(name = "employee_id")
    var employeeId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    var approvalStatus: ApprovalStatus = ApprovalStatus.PENDING,

    // ✅ New column
    @Column(name = "unit_id")
    var unitId: Long? = null
)