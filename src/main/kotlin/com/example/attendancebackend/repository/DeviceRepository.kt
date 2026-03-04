package com.example.attendancebackend.repository

import com.example.attendancebackend.model.Device
import com.example.attendancebackend.model.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : JpaRepository<Device, Long> {

    fun findByDeviceId(deviceId: String): Device?

    // 🔥 ADD THIS METHOD (IMPORTANT)
    fun findByEmployee(employee: Employee): Device?

    // (Optional but already used in AttendanceService)
    fun findByEmployeeEmployeeId(employeeId: Long): Device?
}