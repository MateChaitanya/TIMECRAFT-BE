package com.example.attendancebackend.model

import jakarta.persistence.*

@Entity
@Table(name = "unit")
data class Unit(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val unitId: Long = 0,

    val unitName: String,

    @Enumerated(EnumType.STRING)
    val unitType: UnitType,

    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val allowedRadius: Double? = null
)

enum class UnitType {
    Office, Plant, Site, Client_Location
}
