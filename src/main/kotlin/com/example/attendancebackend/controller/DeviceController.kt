package com.example.attendancebackend.controller

import com.example.attendancebackend.dto.DeviceRequest
import com.example.attendancebackend.model.Device
import com.example.attendancebackend.service.DeviceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/device")
class DeviceController(
    private val deviceService: DeviceService
) {

    @PostMapping("/register")
    fun registerDevice(@RequestBody request: DeviceRequest): ResponseEntity<Device> {

        val savedDevice = deviceService.saveDevice(request)

        return ResponseEntity.ok(savedDevice)
    }
}
