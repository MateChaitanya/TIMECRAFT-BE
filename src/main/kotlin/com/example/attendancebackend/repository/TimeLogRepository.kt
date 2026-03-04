package com.example.attendancebackend.repository

import com.example.attendancebackend.model.TimeLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TimeLogRepository : JpaRepository<TimeLog, Long> {
    fun findTopByTrip_TripIdOrderByCheckInTimeDesc(tripId: Long): TimeLog?
}
