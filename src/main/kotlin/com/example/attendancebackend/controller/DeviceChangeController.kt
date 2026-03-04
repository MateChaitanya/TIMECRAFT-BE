package com.example.attendancebackend.controller

import com.example.attendancebackend.model.DeviceChangeRequest
import com.example.attendancebackend.service.DeviceChangeService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/device-change")
class DeviceChangeController(
    private val deviceChangeService: DeviceChangeService
) {

    // 👤 USER request temporary device
    @PostMapping("/request")
    fun requestDeviceChange(
        @RequestParam employeeId: Long,
        @RequestParam oldDeviceId: String,
        @RequestParam newDeviceId: String,
        @RequestParam(required = false) validDate: String? // optional
    ): DeviceChangeRequest {

        val date = validDate?.let { LocalDate.parse(it) } ?: LocalDate.now()
        return deviceChangeService.requestDeviceChange(employeeId, oldDeviceId, newDeviceId, date)
    }

    // 👨‍💼 ADMIN view pending requests
    @GetMapping("/pending")
    fun getPending(): List<DeviceChangeRequest> {
        return deviceChangeService.getPendingRequests()
    }

    // 👨‍💼 ADMIN approve request
    @PutMapping("/approve/{id}")
    fun approve(@PathVariable id: Long): DeviceChangeRequest {
        return deviceChangeService.approveRequest(id)
    }

    // 👨‍💼 ADMIN reject request
    @PutMapping("/reject/{id}")
    fun reject(@PathVariable id: Long): DeviceChangeRequest {
        return deviceChangeService.rejectRequest(id)
    }
}