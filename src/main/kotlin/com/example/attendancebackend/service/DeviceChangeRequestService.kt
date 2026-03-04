package com.example.attendancebackend.service

import com.example.attendancebackend.model.DeviceChangeRequest
import com.example.attendancebackend.model.Employee
import com.example.attendancebackend.model.RequestStatus
import com.example.attendancebackend.repository.DeviceChangeRequestRepository
import com.example.attendancebackend.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class DeviceChangeRequestService(
    private val deviceChangeRequestRepository: DeviceChangeRequestRepository,
    private val employeeRepository: EmployeeRepository
) {

    // ✅ SAVE REQUEST IN NEW TRANSACTION
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveRequest(request: DeviceChangeRequest): DeviceChangeRequest {
        return deviceChangeRequestRepository.save(request)
    }

    // ✅ GET ALL PENDING REQUESTS
    fun getPendingRequests(): List<DeviceChangeRequest> {
        return deviceChangeRequestRepository.findByStatus(RequestStatus.PENDING)
    }

    // ✅ GET EMPLOYEE BY ID
    fun getEmployee(employeeId: Long): Employee {
        return employeeRepository.findById(employeeId)
            .orElseThrow { RuntimeException("Employee not found") }
    }
}