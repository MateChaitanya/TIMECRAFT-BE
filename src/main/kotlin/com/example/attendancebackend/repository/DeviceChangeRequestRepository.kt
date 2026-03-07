package com.example.attendancebackend.repository

import com.example.attendancebackend.model.DeviceChangeRequest
import com.example.attendancebackend.model.RequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceChangeRequestRepository : JpaRepository<DeviceChangeRequest, Long> {

    fun findByEmployee_EmployeeIdAndNewDeviceIdAndStatus(
        employeeId: Long,
        newDeviceId: String,
        status: RequestStatus
    ): List<DeviceChangeRequest>

    fun findTopByEmployee_EmployeeIdAndNewDeviceIdAndStatusOrderByRequestedAtDesc(
        employeeId: Long,
        newDeviceId: String,
        status: RequestStatus
    ): DeviceChangeRequest?

    fun findTopByEmployee_EmployeeIdAndNewDeviceIdAndStatusOrderByApprovedAtDesc(
        employeeId: Long,
        newDeviceId: String,
        status: RequestStatus
    ): DeviceChangeRequest?

    fun findByStatus(status: RequestStatus): List<DeviceChangeRequest>
}