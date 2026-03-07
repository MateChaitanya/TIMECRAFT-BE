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

    // ================= GET EMPLOYEE FROM USER =================
    private fun getEmployeeFromUser(userId: Long): Employee {

        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found with id $userId") }

        val employeeId = user.employeeId
            ?: throw RuntimeException("User is not linked to employee")

        return employeeRepository.findById(employeeId)
            .orElseThrow { RuntimeException("Employee not found with id $employeeId") }
    }


    // ================= DEVICE VALIDATION =================
    private fun validateDevice(employee: Employee, deviceId: String) {

        val deviceById = deviceRepository.findByDeviceId(deviceId)
        val deviceForEmployee = deviceRepository.findByEmployeeEmployeeId(employee.employeeId)

        // ===== FIRST LOGIN =====
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

        // ===== APPROVED TEMP DEVICE =====
        val approvedRequest =
            deviceChangeRequestRepository
                .findTopByEmployee_EmployeeIdAndNewDeviceIdAndStatusOrderByApprovedAtDesc(
                    employee.employeeId,
                    deviceId,
                    RequestStatus.APPROVED
                )

        if (approvedRequest != null &&
            approvedRequest.validDate != null &&
            approvedRequest.validDate!!.isEqual(LocalDate.now())
        ) {
            return
        }

        // ===== DEVICE BELONGS TO OTHER EMPLOYEE =====
        if (deviceById != null && deviceById.employee.employeeId != employee.employeeId) {

            createDeviceChangeRequest(employee, deviceForEmployee?.deviceId, deviceId)

            throw RuntimeException("Device belongs to another employee. Request sent to admin.")
        }

        // ===== UNKNOWN DEVICE =====
        createDeviceChangeRequest(employee, deviceForEmployee?.deviceId, deviceId)

        throw RuntimeException("Temporary device not approved.")
    }


    // ================= CREATE DEVICE CHANGE REQUEST =================
    private fun createDeviceChangeRequest(
        employee: Employee,
        oldDevice: String?,
        newDevice: String
    ) {

        val existingPending =
            deviceChangeRequestRepository
                .findTopByEmployee_EmployeeIdAndNewDeviceIdAndStatusOrderByRequestedAtDesc(
                    employee.employeeId,
                    newDevice,
                    RequestStatus.PENDING
                )

        if (existingPending == null) {

            val request = DeviceChangeRequest(
                employee = employee,
                oldDeviceId = oldDevice ?: "UNKNOWN",
                newDeviceId = newDevice,
                status = RequestStatus.PENDING,
                requestedAt = LocalDateTime.now()
            )

            deviceChangeRequestService.saveRequest(request)
        }
    }


    // ================= CHECK IN =================
    fun checkIn(request: AttendanceRequest): Attendance {

        val employee = getEmployeeFromUser(request.employeeId)

        validateDevice(employee, request.deviceId)

        val existingTrip =
            tripRepository.findTopByEmployee_EmployeeIdAndStatusOrderByCheckInTimeDesc(
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


    // ================= CHECK OUT =================
    fun checkOut(request: AttendanceRequest): Attendance {

        val employee = getEmployeeFromUser(request.employeeId)

        validateDevice(employee, request.deviceId)

        val trip =
            tripRepository.findTopByEmployee_EmployeeIdAndStatusOrderByCheckInTimeDesc(
                employee.employeeId,
                TripStatus.Open
            ) ?: throw RuntimeException("No active check-in found")

        val attendance =
            attendanceRepository
                .findTopByUserIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(request.employeeId)
                ?: throw RuntimeException("Attendance record not found")

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

        val timeLog =
            timeLogRepository.findTopByTrip_TripIdOrderByCheckInTimeDesc(trip.tripId)
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


    // ================= CURRENT OPEN TRIP =================
    fun getCurrentOpenTrip(userId: Long): Attendance? {

        return attendanceRepository
            .findTopByUserIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(userId)
    }


    // ================= TODAY TRIPS =================
    fun getAllTripsOfToday(userId: Long): List<Attendance> {

        return attendanceRepository
            .findAllByUserIdAndAttendanceDateOrderByCheckInTimeAsc(
                userId,
                LocalDate.now()
            )
    }
}