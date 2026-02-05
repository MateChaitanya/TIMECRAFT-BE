package com.example.attendancebackend.repository

import com.example.attendancebackend.model.Attendance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AttendanceRepository : JpaRepository<Attendance, Long> {

    // Get the latest active check-in for a user
    @Query("""
        SELECT a FROM Attendance a
        WHERE a.user.id = :userId
        AND a.checkOutTime IS NULL
        ORDER BY a.checkInTime DESC
    """)
    fun findActiveAttendances(@Param("userId") userId: Long): List<Attendance>
}
