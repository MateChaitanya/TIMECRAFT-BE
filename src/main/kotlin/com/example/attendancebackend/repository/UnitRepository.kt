package com.example.attendancebackend.repository

import com.example.attendancebackend.model.Unit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UnitRepository : JpaRepository<Unit, Long> {
    fun findByUnitName(unitName: String): Unit?
}
