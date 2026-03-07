package com.example.attendancebackend.service

import com.example.attendancebackend.dto.AdminDashboardResponse
import com.example.attendancebackend.model.ApprovalStatus
import com.example.attendancebackend.model.User
import com.example.attendancebackend.repository.AttendanceRepository
import com.example.attendancebackend.repository.EmployeeRepository
import com.example.attendancebackend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
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

        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }

        // ✅ Find employee using email
        val employee = employeeRepository.findByEmail(user.email)

        // ❗ IMPORTANT VALIDATION
        if (employee == null) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Employee record not found. Please add employee details before approving this user."
            )
        }

        // assign employee id
        user.employeeId = employee.employeeId

        // approve user
        user.approvalStatus = ApprovalStatus.APPROVED

        return userRepository.save(user)
    }

    fun rejectUser(userId: Long): User {
        val user = userRepository.findById(userId).orElseThrow()
        user.approvalStatus = ApprovalStatus.REJECTED
        return userRepository.save(user)
    }

    fun getDashboard(): AdminDashboardResponse {

        val today = LocalDate.now()

        val totalEmployees = employeeRepository.count()

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