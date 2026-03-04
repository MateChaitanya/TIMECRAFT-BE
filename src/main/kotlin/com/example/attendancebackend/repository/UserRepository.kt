//package com.example.attendancebackend.repository
//
//import com.example.attendancebackend.model.User
//import com.example.attendancebackend.model.ApprovalStatus
//import org.springframework.data.jpa.repository.JpaRepository
//import org.springframework.stereotype.Repository
//
//@Repository
//interface UserRepository : JpaRepository<User, Long> {
//
//    fun findByEmail(email: String): User?
//
//    fun existsByEmail(email: String): Boolean
//
//    fun findByApprovalStatus(status: ApprovalStatus): List<User>
//
//    // 🆕 Count pending approval users
//    fun countByApprovalStatus(status: ApprovalStatus): Long
//}

package com.example.attendancebackend.repository

import com.example.attendancebackend.model.User
import com.example.attendancebackend.model.ApprovalStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    fun findByApprovalStatus(status: ApprovalStatus): List<User>

    // 🆕 Count pending approval users
    fun countByApprovalStatus(status: ApprovalStatus): Long
}