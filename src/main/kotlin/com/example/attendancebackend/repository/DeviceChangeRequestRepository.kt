package com.example.attendancebackend.repository

import com.example.attendancebackend.model.DeviceChangeRequest
import com.example.attendancebackend.model.RequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceChangeRequestRepository : JpaRepository<DeviceChangeRequest, Long> {

    // ✅ Find requests by employee, new device, and status
    fun findByEmployee_EmployeeIdAndNewDeviceIdAndStatus(
        employeeId: Long,
        newDeviceId: String,
        status: RequestStatus
    ): List<DeviceChangeRequest>

    // ✅ Optional: Get top by requestedAt (most recent request)
    fun findTopByEmployee_EmployeeIdAndNewDeviceIdAndStatusOrderByRequestedAtDesc(
        employeeId: Long,
        newDeviceId: String,
        status: RequestStatus
    ): DeviceChangeRequest?

    // ✅ Optional: Get top by approvedAt (most recent approved request)
    fun findTopByEmployee_EmployeeIdAndNewDeviceIdAndStatusOrderByApprovedAtDesc(
        employeeId: Long,
        newDeviceId: String,
        status: RequestStatus
    ): DeviceChangeRequest?

    // ✅ Find all pending requests
    fun findByStatus(status: RequestStatus): List<DeviceChangeRequest>
}