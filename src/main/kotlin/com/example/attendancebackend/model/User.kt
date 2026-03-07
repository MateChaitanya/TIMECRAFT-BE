package com.example.attendancebackend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "employee_id")
    var employeeId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    var approvalStatus: ApprovalStatus = ApprovalStatus.PENDING,

    @Column(name = "unit_id")
    var unitId: Long? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null
)