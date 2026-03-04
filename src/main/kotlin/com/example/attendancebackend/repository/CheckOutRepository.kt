package com.example.attendancebackend.repository

import com.example.attendancebackend.model.CheckOut
import org.springframework.data.jpa.repository.JpaRepository

interface CheckOutRepository : JpaRepository<CheckOut, Long>
