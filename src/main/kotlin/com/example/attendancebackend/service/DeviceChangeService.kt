package com.example.attendancebackend.service

import com.example.attendancebackend.model.*
import com.example.attendancebackend.repository.DeviceChangeRequestRepository
import com.example.attendancebackend.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional
class DeviceChangeService(
    private val deviceChangeRequestRepository: DeviceChangeRequestRepository,
    private val employeeRepository: EmployeeRepository
) {

    // 📌 USER requests device (temporary device now supported)
    fun requestDeviceChange(
        employeeId: Long,
        oldDeviceId: String,
        newDeviceId: String,
        validDate: LocalDate = LocalDate.now() // default to today
    ): DeviceChangeRequest {

        val employee = employeeRepository.findById(employeeId)
            .orElseThrow { RuntimeException("Employee not found") }

        val request = DeviceChangeRequest(
            employee = employee,
            oldDeviceId = oldDeviceId,
            newDeviceId = newDeviceId,
            status = RequestStatus.PENDING,
            requestedAt = LocalDateTime.now(),
            validDate = validDate
        )

        // ✅ Save request to DB so admin can approve
        return deviceChangeRequestRepository.saveAndFlush(request)
    }

    // 👨‍💼 ADMIN get pending requests
    fun getPendingRequests(): List<DeviceChangeRequest> {
        return deviceChangeRequestRepository.findByStatus(RequestStatus.PENDING)
    }

    // 👨‍💼 ADMIN approve request
    fun approveRequest(requestId: Long): DeviceChangeRequest {
        val request = deviceChangeRequestRepository.findById(requestId)
            .orElseThrow { RuntimeException("Request not found") }

        request.status = RequestStatus.APPROVED
        request.approvedAt = LocalDateTime.now()

        return deviceChangeRequestRepository.save(request)
    }

    // 👨‍💼 ADMIN reject request
    fun rejectRequest(requestId: Long): DeviceChangeRequest {
        val request = deviceChangeRequestRepository.findById(requestId)
            .orElseThrow { RuntimeException("Request not found") }

        request.status = RequestStatus.REJECTED
        return deviceChangeRequestRepository.save(request)
    }
}