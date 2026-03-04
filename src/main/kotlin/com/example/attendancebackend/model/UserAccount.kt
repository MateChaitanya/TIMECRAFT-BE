package com.example.attendancebackend.model

import jakarta.persistence.*


@Entity
@Table(name = "users")
data class UserAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String = "",

    @Column(nullable = false, unique = true)
    val email: String = "",

    @Column(nullable = false)
    val password: String = "",

    @Column(name = "fingerprint_hash")
    var fingerprintHash: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    var approvalStatus: ApprovalStatus = ApprovalStatus.PENDING,

    @ManyToOne
    @JoinColumn(name = "employee_id")
    var employee: Employee? = null
)
