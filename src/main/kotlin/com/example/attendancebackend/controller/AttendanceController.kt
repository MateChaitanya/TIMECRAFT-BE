package com.example.attendancebackend.controller

import com.example.attendancebackend.dto.AttendanceRequest
import com.example.attendancebackend.model.Attendance
import com.example.attendancebackend.service.AttendanceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/attendance")
class AttendanceController(private val attendanceService: AttendanceService) {

    data class AttendanceResponse(
        val status: String,
        val message: String,
        val checkInTime: String? = null,
        val checkOutTime: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val workingHours: String? = null,
        val tempDeviceRequestLink: String? = null // ✨ new field
    )

    // ================= CHECK-IN =================
    @PostMapping("/check-in")
    fun checkIn(@RequestBody request: AttendanceRequest): ResponseEntity<AttendanceResponse> {
        return try {
            val attendance = attendanceService.checkIn(request)

            ResponseEntity.ok(
                AttendanceResponse(
                    status = "SUCCESS",
                    message = "Check-in successful",
                    checkInTime = attendance.checkInTime.toString(),
                    latitude = attendance.latitude,
                    longitude = attendance.longitude
                )
            )
        } catch (e: Exception) {
            val msg = e.message ?: "Check-in failed"
            val tempLink = if (msg.contains("Unauthorized device")) "/device-change/request" else null

            ResponseEntity.badRequest().body(
                AttendanceResponse(
                    status = "ERROR",
                    message = msg,
                    tempDeviceRequestLink = tempLink
                )
            )
        }
    }

    // ================= CHECK-OUT =================
    @PostMapping("/check-out")
    fun checkOut(@RequestBody request: AttendanceRequest): ResponseEntity<AttendanceResponse> {
        return try {
            val attendance = attendanceService.checkOut(request)
            ResponseEntity.ok(
                AttendanceResponse(
                    status = "SUCCESS",
                    message = "Check-out successful",
                    checkInTime = attendance.checkInTime.toString(),
                    checkOutTime = attendance.checkOutTime?.toString(),
                    latitude = attendance.latitude,
                    longitude = attendance.longitude,
                    workingHours = attendance.workingHours
                )
            )
        } catch (e: Exception) {
            val msg = e.message ?: "Check-out failed"
            val tempLink = if (msg.contains("Unauthorized device")) "/device-change/request" else null

            ResponseEntity.badRequest().body(
                AttendanceResponse(
                    status = "ERROR",
                    message = msg,
                    tempDeviceRequestLink = tempLink
                )
            )
        }
    }

    // ================= CURRENT TRIP =================
    @GetMapping("/current-trip/{userId}")
    fun getCurrentTrip(@PathVariable userId: Long): ResponseEntity<AttendanceResponse> {
        val attendance = attendanceService.getCurrentOpenTrip(userId)

        return if (attendance != null) {
            ResponseEntity.ok(
                AttendanceResponse(
                    status = "SUCCESS",
                    message = "Active trip found",
                    checkInTime = attendance.checkInTime.toString(),
                    checkOutTime = attendance.checkOutTime?.toString(),
                    latitude = attendance.latitude,
                    longitude = attendance.longitude,
                    workingHours = attendance.workingHours
                )
            )
        } else {
            ResponseEntity.ok(
                AttendanceResponse(
                    status = "SUCCESS",
                    message = "No active trip found"
                )
            )
        }
    }

    // ================= TODAY TRIPS =================
    @GetMapping("/today-trips/{userId}")
    fun getTodayTrips(@PathVariable userId: Long): ResponseEntity<List<AttendanceResponse>> {

        val trips = attendanceService.getAllTripsOfToday(userId)

        val response = trips.map {
            AttendanceResponse(
                status = "SUCCESS",
                message = "Trip record",
                checkInTime = it.checkInTime.toString(),
                checkOutTime = it.checkOutTime?.toString(),
                latitude = it.latitude,
                longitude = it.longitude,
                workingHours = it.workingHours
            )
        }

        return ResponseEntity.ok(response)
    }
}