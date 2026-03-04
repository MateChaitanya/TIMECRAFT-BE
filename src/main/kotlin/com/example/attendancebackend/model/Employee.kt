package com.example.attendancebackend.model

import jakarta.persistence.*

import jakarta.persistence.*

@Entity
@Table(name = "employee")
data class Employee(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    val employeeId: Long = 0,

    val name: String,
    val email: String,

    @Column(name = "role")
    var role: String? = "USER"     // ✅ NEW FIELD
)

