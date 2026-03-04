package com.example.attendancebackend.model

import jakarta.persistence.*

@Entity
@Table(name = "device")
data class Device(

    @Id
    @Column(name = "device_id")
    var deviceId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    var employee: Employee,

    @Column(name = "os_version")
    val osVersion: String? = null,

    @Column(name = "imei_or_android_id")
    val imeiOrAndroidId: String? = null,

    @Column(name = "app_integrity_status")
    val appIntegrityStatus: String? = null
)
