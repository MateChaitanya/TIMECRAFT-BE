package com.example.attendancebackend.repository

import com.example.attendancebackend.model.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : JpaRepository<Employee, Long> {

    fun findByEmail(email: String): Employee?

    // 🆕 Total employee count
    fun countBy(): Long
}