package com.example.attendancebackend.service

import com.example.attendancebackend.dto.AdminDashboardResponse
import com.example.attendancebackend.model.ApprovalStatus
import com.example.attendancebackend.model.User
import com.example.attendancebackend.repository.AttendanceRepository
import com.example.attendancebackend.repository.EmployeeRepository
import com.example.attendancebackend.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository,
    private val attendanceRepository: AttendanceRepository
) {

    fun getPendingUsers(): List<User> {
        return userRepository.findByApprovalStatus(ApprovalStatus.PENDING)
    }

    fun approveUser(userId: Long): User {
        val user = userRepository.findById(userId).orElseThrow()
        user.approvalStatus = ApprovalStatus.APPROVED
        return userRepository.save(user)
    }

    fun rejectUser(userId: Long): User {
        val user = userRepository.findById(userId).orElseThrow()
        user.approvalStatus = ApprovalStatus.REJECTED
        return userRepository.save(user)
    }

    // ✅ FINAL DASHBOARD LOGIC
    fun getDashboard(): AdminDashboardResponse {

        val today = LocalDate.now()

        val totalEmployees = employeeRepository.count()

        // 🔥 KEY FIX HERE
        val presentToday = attendanceRepository.countUniqueEmployeesPresent(today)

        val pendingUsers = userRepository.countByApprovalStatus(ApprovalStatus.PENDING)

        val absentToday = (totalEmployees - presentToday).coerceAtLeast(0)

        return AdminDashboardResponse(
            totalEmployees = totalEmployees,
            presentToday = presentToday,
            absentToday = absentToday,
            pendingUsers = pendingUsers
        )
    }
}