package com.example.attendancebackend.repository

import com.example.attendancebackend.model.Trip
import com.example.attendancebackend.model.TripStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TripRepository : JpaRepository<Trip, Long> {

    // Find last open trip for employee
    fun findTopByEmployee_EmployeeIdAndStatusOrderByCheckInTimeDesc(
        employeeId: Long,
        status: TripStatus = TripStatus.Open
    ): Trip?

    // Find trips for today
    fun findAllByEmployee_EmployeeIdAndCheckInTimeBetween(
        employeeId: Long,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Trip>
}
