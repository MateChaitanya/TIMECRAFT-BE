package com.example.attendancebackend.service

import com.example.attendancebackend.dto.AttendanceRequest
import com.example.attendancebackend.model.*
import com.example.attendancebackend.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional
class AttendanceService(
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository,
    private val tripRepository: TripRepository,
    private val timeLogRepository: TimeLogRepository,
    private val checkInRepository: CheckInRepository,
    private val attendanceRepository: AttendanceRepository,
    private val checkOutRepository: CheckOutRepository,
    private val deviceRepository: DeviceRepository,
    private val deviceChangeRequestRepository: DeviceChangeRequestRepository,
    private val geocodingService: GeocodingService,
    private val deviceChangeRequestService: DeviceChangeRequestService
) {

    // ================= DEVICE VALIDATION =================
    private fun validateDevice(employee: Employee, deviceId: String) {

        val deviceById = deviceRepository.findByDeviceId(deviceId)
        val deviceForEmployee = deviceRepository.findByEmployeeEmployeeId(employee.employeeId)

        // ===== FIRST TIME LOGIN (Permanent Registration) =====
        if (deviceById == null && deviceForEmployee == null) {
            val newDevice = Device(
                deviceId = deviceId,
                employee = employee,
                osVersion = "ANDROID",
                imeiOrAndroidId = deviceId,
                appIntegrityStatus = "VALID"
            )
            deviceRepository.save(newDevice)
            return
        }

        // ===== PERMANENT DEVICE MATCH =====
        if (deviceForEmployee != null && deviceForEmployee.deviceId == deviceId) {
            return
        }

        // ===== CHECK IF TEMPORARY DEVICE APPROVED FOR TODAY =====
        val approvedRequest = deviceChangeRequestRepository
            .findByEmployee_EmployeeIdAndNewDeviceIdAndStatus(
                employee.employeeId,
                deviceId,
                RequestStatus.APPROVED
            ).firstOrNull()

        if (approvedRequest != null && approvedRequest.approvedAt != null) {
            if (approvedRequest.approvedAt!!.toLocalDate().isEqual(LocalDate.now())) {
                // ✅ Allow temporary access for today
                return
            }
        }

        // ===== DEVICE BELONGS TO ANOTHER EMPLOYEE =====
        if (deviceById != null && deviceById.employee.employeeId != employee.employeeId) {

            val existingPending = deviceChangeRequestRepository
                .findByEmployee_EmployeeIdAndNewDeviceIdAndStatus(
                    employee.employeeId,
                    deviceId,
                    RequestStatus.PENDING
                ).firstOrNull()

            if (existingPending == null) {
                val newRequest = DeviceChangeRequest(
                    employee = employee,
                    oldDeviceId = deviceForEmployee?.deviceId ?: "UNKNOWN",
                    newDeviceId = deviceId,
                    status = RequestStatus.PENDING,
                    requestedAt = LocalDateTime.now()
                )
                deviceChangeRequestService.saveRequest(newRequest)
            }

            throw RuntimeException("This device belongs to another employee. Request sent to admin.")
        }

        // ===== DEVICE NOT REGISTERED BUT NOT APPROVED =====
        val existingPending = deviceChangeRequestRepository
            .findByEmployee_EmployeeIdAndNewDeviceIdAndStatus(
                employee.employeeId,
                deviceId,
                RequestStatus.PENDING
            ).firstOrNull()

        if (existingPending == null) {
            val newRequest = DeviceChangeRequest(
                employee = employee,
                oldDeviceId = deviceForEmployee?.deviceId ?: "UNKNOWN",
                newDeviceId = deviceId,
                status = RequestStatus.PENDING,
                requestedAt = LocalDateTime.now()
            )
            deviceChangeRequestService.saveRequest(newRequest)
        }

        throw RuntimeException("Temporary device not approved for today.")
    }

    // ===================== CHECK-IN =========================
    fun checkIn(request: AttendanceRequest): Attendance {

        val user = userRepository.findById(request.employeeId)
            .orElseThrow { RuntimeException("User not found") }

        val employee = employeeRepository.findById(
            user.employeeId ?: throw RuntimeException("User not linked to employee")
        ).orElseThrow { RuntimeException("Employee not found") }

        validateDevice(employee, request.deviceId)

        val existingTrip = tripRepository
            .findTopByEmployee_EmployeeIdAndStatusOrderByCheckInTimeDesc(
                employee.employeeId,
                TripStatus.Open
            )

        if (existingTrip != null) {
            throw RuntimeException("Already checked-in. Please checkout first.")
        }

        val now = LocalDateTime.now()
        val address = geocodingService.getAddressFromLatLng(request.latitude, request.longitude)

        val trip = Trip(
            employee = employee,
            checkInTime = now,
            startTime = now,
            status = TripStatus.Open,
            unitName = "DEFAULT"
        )

        val savedTrip = tripRepository.save(trip)

        checkInRepository.save(
            CheckIn(
                trip = savedTrip,
                timestamp = now,
                latitude = request.latitude,
                longitude = request.longitude,
                deviceId = request.deviceId
            )
        )

        timeLogRepository.save(
            TimeLog(
                trip = savedTrip,
                checkInTime = now,
                latitude = request.latitude,
                longitude = request.longitude
            )
        )

        val attendance = Attendance(
            attendanceDate = LocalDate.now(),
            userId = request.employeeId,
            checkInTime = now,
            latitude = request.latitude,
            longitude = request.longitude,
            attendanceType = "IN",
            address = address
        )

        return attendanceRepository.save(attendance)
    }

    // ===================== CHECK-OUT =========================
    fun checkOut(request: AttendanceRequest): Attendance {

        val user = userRepository.findById(request.employeeId)
            .orElseThrow { RuntimeException("User not found") }

        val employee = employeeRepository.findById(
            user.employeeId ?: throw RuntimeException("User not linked to employee")
        ).orElseThrow { RuntimeException("Employee not found") }

        validateDevice(employee, request.deviceId)

        val trip = tripRepository
            .findTopByEmployee_EmployeeIdAndStatusOrderByCheckInTimeDesc(
                employee.employeeId,
                TripStatus.Open
            ) ?: throw RuntimeException("No active check-in found")

        val attendance = attendanceRepository
            .findTopByUserIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(request.employeeId)
            ?: throw RuntimeException("Attendance not found")

        val now = LocalDateTime.now()
        val address = geocodingService.getAddressFromLatLng(request.latitude, request.longitude)

        trip.checkOutTime = now
        trip.endTime = now
        trip.status = TripStatus.Closed

        checkOutRepository.save(
            CheckOut(
                trip = trip,
                timestamp = now,
                latitude = request.latitude,
                longitude = request.longitude,
                deviceId = request.deviceId
            )
        )

        val timeLog = timeLogRepository
            .findTopByTrip_TripIdOrderByCheckInTimeDesc(trip.tripId)
            ?: throw RuntimeException("TimeLog not found")

        val duration = Duration.between(timeLog.checkInTime, now).toMinutes()

        timeLog.checkOutTime = now
        timeLog.durationMinutes = duration.toInt()
        timeLogRepository.save(timeLog)

        attendance.checkOutTime = now
        attendance.workingHours = "$duration minutes"
        attendance.attendanceType = "OUT"
        attendance.address = address

        trip.workingHours = "$duration minutes"
        tripRepository.save(trip)

        return attendanceRepository.save(attendance)
    }

    fun getCurrentOpenTrip(userId: Long): Attendance? {
        return attendanceRepository
            .findTopByUserIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(userId)
    }

    fun getAllTripsOfToday(userId: Long): List<Attendance> {
        return attendanceRepository
            .findAllByUserIdAndAttendanceDateOrderByCheckInTimeAsc(userId, LocalDate.now())
    }
}