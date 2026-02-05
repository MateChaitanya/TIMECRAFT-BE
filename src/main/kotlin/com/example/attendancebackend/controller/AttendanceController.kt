package com.example.attendancebackend.controller

import com.example.attendancebackend.dto.AttendanceRequest
import com.example.attendancebackend.model.Attendance
import com.example.attendancebackend.repository.AttendanceRepository
import com.example.attendancebackend.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/attendance")
class AttendanceController(
    private val attendanceRepo: AttendanceRepository,
    private val userRepo: UserRepository
) {

    data class ApiResponse(
        val status: String,
        val message: String,
        val checkInTime: LocalDateTime? = null,
        val checkOutTime: LocalDateTime? = null,
        val workingHours: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null
    )

    // ================= CHECK-IN =================
    @PostMapping("/check-in")
    fun checkIn(@RequestBody req: AttendanceRequest): ResponseEntity<ApiResponse> {

        val user = userRepo.findById(req.userId).orElse(null)
            ?: return ResponseEntity.badRequest()
                .body(ApiResponse("ERROR", "User not found"))

        val now = LocalDateTime.now()

        val attendance = Attendance(
            user = user,
            attendanceType = "CHECK_IN",
            checkInTime = now,
            checkOutTime = null,
            attendanceDate = LocalDate.now(),
            workingHours = null,
            latitude = req.latitude,
            longitude = req.longitude
        )

        attendanceRepo.save(attendance)

        return ResponseEntity.ok(
            ApiResponse(
                "SUCCESS",
                "Check-in successful",
                checkInTime = now,
                latitude = req.latitude,
                longitude = req.longitude
            )
        )
    }
    @PostMapping("/check-out")
    fun checkOut(@RequestBody req: AttendanceRequest): ResponseEntity<ApiResponse> {

        val activeAttendances = attendanceRepo.findActiveAttendances(req.userId)

        if (activeAttendances.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse("ERROR", "No active check-in found"))
        }

        // Pick the latest active check-in
        val attendance = activeAttendances.first()

        val checkInTime = attendance.checkInTime
            ?: return ResponseEntity.badRequest()
                .body(ApiResponse("ERROR", "Invalid check-in data"))

        val checkOutTime = LocalDateTime.now()
        val duration = Duration.between(checkInTime, checkOutTime)
        val hoursWorked = duration.toMinutes() / 60.0

        attendance.checkOutTime = checkOutTime
        attendance.attendanceType = "CHECK_OUT"
        attendance.workingHours = String.format("%.2f", hoursWorked)
        attendance.latitude = req.latitude
        attendance.longitude = req.longitude

        attendanceRepo.save(attendance)

        return ResponseEntity.ok(
            ApiResponse(
                "SUCCESS",
                "Check-out successful",
                checkInTime = checkInTime,
                checkOutTime = checkOutTime,
                workingHours = attendance.workingHours,
                latitude = req.latitude,
                longitude = req.longitude
            )
        )
    }
}