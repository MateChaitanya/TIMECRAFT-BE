package com.example.attendancebackend.service

import com.example.attendancebackend.dto.DeviceRequest
import com.example.attendancebackend.model.Device
import com.example.attendancebackend.repository.DeviceRepository
import com.example.attendancebackend.repository.EmployeeRepository
import org.springframework.stereotype.Service

@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val employeeRepository: EmployeeRepository
) {

    fun saveDevice(request: DeviceRequest): Device {

        //  Step 1: Fetch employee from DB
        val employee = employeeRepository.findById(request.employeeId)
            .orElseThrow { RuntimeException("Employee not found with id ${request.employeeId}") }

        //  Step 2: Map to entity
        val device = Device(
            deviceId = request.deviceId,
            employee = employee,
            osVersion = request.osVersion,
            imeiOrAndroidId = request.imeiOrAndroidId,
            appIntegrityStatus = request.appIntegrityStatus
        )

        //  Step 3: Save
        return deviceRepository.save(device)
    }
}
