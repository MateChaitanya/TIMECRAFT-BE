package com.example.attendancebackend.repository

import com.example.attendancebackend.model.Attendance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AttendanceRepository : JpaRepository<Attendance, Long> {

    // 🔹 Get current open attendance (check-in not checked out)
    fun findTopByUserIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(userId: Long): Attendance?

    // 🔹 Get all today's attendance entries
    fun findAllByUserIdAndAttendanceDateOrderByCheckInTimeAsc(
        userId: Long,
        attendanceDate: LocalDate
    ): List<Attendance>

    // 🔥 FINAL FIX — count unique employees present today
    @Query(
        """
        SELECT COUNT(DISTINCT a.userId)
        FROM Attendance a
        WHERE a.attendanceDate = :date
        """
    )
    fun countUniqueEmployeesPresent(@Param("date") date: LocalDate): Long


    // =========================================================
    // 🔥 NEW ADDITION (Helpful for Admin & Reports)
    // =========================================================

    // 🔹 Count all attendance records for a specific date
    fun countByAttendanceDate(attendanceDate: LocalDate): Long

    // 🔹 Get all attendance for a specific date (admin usage)
    fun findAllByAttendanceDate(attendanceDate: LocalDate): List<Attendance>
}