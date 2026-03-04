package com.example.attendancebackend.repository

import com.example.attendancebackend.model.CheckIn
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CheckInRepository : JpaRepository<CheckIn, Long>
