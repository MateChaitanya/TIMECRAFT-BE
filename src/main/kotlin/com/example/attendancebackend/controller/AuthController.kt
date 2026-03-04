//package com.example.attendancebackend.controller
//
//import com.example.attendancebackend.model.ApprovalStatus
//import com.example.attendancebackend.model.User
//import com.example.attendancebackend.repository.UserRepository
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//
//@RestController
//@RequestMapping("/auth")
//class AuthController(
//    private val userRepository: UserRepository
//) {
//
//    // ================= DTO CLASSES =================
//
//    data class SignupRequest(
//        val name: String,
//        val email: String,
//        val password: String,
//        val fingerprintHash: String
//    )
//
//    data class LoginRequest(
//        val email: String,
//        val password: String
//    )
//
//    data class LoginResponse(
//        val userId: Long,
//        val name: String,
//        val email: String,
//        val employeeId: Long?,
//        val approvalStatus: String,
//        val message: String
//    )
//
//    // ================= SIGNUP =================
//
//    @PostMapping("/signup")
//    fun signup(@RequestBody request: SignupRequest): ResponseEntity<Map<String, String>> {
//
//        val email = request.email.trim().lowercase()
//
//        if (userRepository.existsByEmail(email)) {
//            return ResponseEntity.badRequest()
//                .body(mapOf("message" to "Email already exists"))
//        }
//
//        val newUser = User(
//            name = request.name.trim(),
//            email = email,
//            password = request.password.trim(),  // TODO: hash later
//            fingerprintHash = request.fingerprintHash,
//            approvalStatus = ApprovalStatus.PENDING
//        )
//
//        userRepository.save(newUser)
//
//        return ResponseEntity.ok(
//            mapOf("message" to "Signup successful. Waiting for admin approval")
//        )
//    }
//
//    // ================= LOGIN =================
//
//    @PostMapping("/login")
//    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
//
//        val email = request.email.trim().lowercase()
//        val password = request.password.trim()
//
//        val existingUser = userRepository.findByEmail(email)
//            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(
//                    LoginResponse(
//                        0,
//                        "",
//                        "",
//                        null,
//                        "",
//                        "Invalid email or password"
//                    )
//                )
//
//        // 🔐 Password check
//        if (existingUser.password != password) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(
//                    LoginResponse(
//                        0,
//                        "",
//                        "",
//                        null,
//                        "",
//                        "Invalid email or password"
//                    )
//                )
//        }
//
//        // 🚫 Approval check (VERY IMPORTANT)
//        if (existingUser.approvalStatus != ApprovalStatus.APPROVED) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                .body(
//                    LoginResponse(
//                        existingUser.userId,
//                        existingUser.name,
//                        existingUser.email,
//                        existingUser.employeeId,
//                        existingUser.approvalStatus.name,
//                        "Your account is not approved yet. Please contact admin."
//                    )
//                )
//        }
//
//        // ✅ SUCCESS LOGIN
//        return ResponseEntity.ok(
//            LoginResponse(
//                existingUser.userId,
//                existingUser.name,
//                existingUser.email,
//                existingUser.employeeId,
//                existingUser.approvalStatus.name,
//                "Login successful"
//            )
//        )
//    }
//}

package com.example.attendancebackend.controller

import com.example.attendancebackend.model.ApprovalStatus
import com.example.attendancebackend.model.User
import com.example.attendancebackend.repository.EmployeeRepository
import com.example.attendancebackend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository
) {

    // ================= DTO CLASSES =================

    data class SignupRequest(
        val name: String,
        val email: String,
        val password: String,
        val fingerprintHash: String
    )

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class LoginResponse(
        val userId: Long,
        val name: String,
        val email: String,
        val employeeId: Long?,
        val role: String?,                 // ✅ Added role
        val approvalStatus: String,
        val message: String
    )

    // ================= SIGNUP =================

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<Map<String, String>> {

        val email = request.email.trim().lowercase()

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                .body(mapOf("message" to "Email already exists"))
        }

        val newUser = User(
            name = request.name.trim(),
            email = email,
            password = request.password.trim(),  // TODO: hash later
            fingerprintHash = request.fingerprintHash,
            approvalStatus = ApprovalStatus.PENDING
        )

        userRepository.save(newUser)

        return ResponseEntity.ok(
            mapOf("message" to "Signup successful. Waiting for admin approval")
        )
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {

        val email = request.email.trim().lowercase()
        val password = request.password.trim()

        val existingUser = userRepository.findByEmail(email)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                    LoginResponse(
                        0,
                        "",
                        "",
                        null,
                        null,
                        "",
                        "Invalid email or password"
                    )
                )

        // 🔐 Password check
        if (existingUser.password != password) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                    LoginResponse(
                        0,
                        "",
                        "",
                        null,
                        null,
                        "",
                        "Invalid email or password"
                    )
                )
        }

        // 🚫 Approval check
        if (existingUser.approvalStatus != ApprovalStatus.APPROVED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                    LoginResponse(
                        existingUser.userId,
                        existingUser.name,
                        existingUser.email,
                        existingUser.employeeId,
                        null,
                        existingUser.approvalStatus.name,
                        "Your account is not approved yet. Please contact admin."
                    )
                )
        }

        // ================= ROLE FETCH =================

        var role: String? = null

        val empId = existingUser.employeeId
        if (empId != null) {
            val employee = employeeRepository.findById(empId).orElse(null)
            role = employee?.role  // ✅ fetch role from Employee table
        }

        // ================= SUCCESS LOGIN =================

        return ResponseEntity.ok(
            LoginResponse(
                existingUser.userId,
                existingUser.name,
                existingUser.email,
                existingUser.employeeId,
                role,
                existingUser.approvalStatus.name,
                "Login successful"
            )
        )
    }
}