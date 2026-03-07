//package com.example.attendancebackend.controller
//
//import com.example.attendancebackend.dto.SignUpRequest
//import com.example.attendancebackend.model.ApprovalStatus
//import com.example.attendancebackend.model.User
//import com.example.attendancebackend.repository.EmployeeRepository
//import com.example.attendancebackend.repository.UserRepository
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//
//@RestController
//@RequestMapping("/api/auth")
//class SignUpController(
//    private val userRepo: UserRepository,
//    private val employeeRepo: EmployeeRepository
//) {
//
//    data class SignUpResponse(
//        val success: Boolean,
//        val message: String
//    )
//
//    @PostMapping("/signup")
//    fun signUp(@RequestBody req: SignUpRequest): ResponseEntity<SignUpResponse> {
//
//        if (userRepo.existsByEmail(req.email)) {
//            return ResponseEntity.badRequest()
//                .body(SignUpResponse(false, "Email already exists"))
//        }
//
//        val employee = employeeRepo.findById(req.employeeId)
//            .orElse(null) ?: return ResponseEntity.badRequest()
//            .body(SignUpResponse(false, "Employee not found"))
//
//        val user = User(
//            name = req.name,
//            email = req.email,
//            password = req.password,
//            employeeId = employee.employeeId,   //  LINKED PROPERLY
//            approvalStatus = ApprovalStatus.PENDING,
//            fingerprintHash = req.biometricToken ?: ""
//        )
//
//        userRepo.save(user)
//
//        return ResponseEntity.ok(
//            SignUpResponse(true, "Registration successful. Waiting for admin approval.")
//        )
//    }
//}


package com.example.attendancebackend.controller

import com.example.attendancebackend.dto.SignUpRequest
import com.example.attendancebackend.model.ApprovalStatus
import com.example.attendancebackend.model.User
import com.example.attendancebackend.repository.EmployeeRepository
import com.example.attendancebackend.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class SignUpController(
    private val userRepo: UserRepository,
    private val employeeRepo: EmployeeRepository
) {

    data class SignUpResponse(
        val success: Boolean,
        val message: String
    )

    @PostMapping("/signup")
    fun signUp(@RequestBody req: SignUpRequest): ResponseEntity<SignUpResponse> {

        if (userRepo.existsByEmail(req.email)) {
            return ResponseEntity.badRequest()
                .body(SignUpResponse(false, "Email already exists"))
        }

        val employee = employeeRepo.findById(req.employeeId)
            .orElse(null) ?: return ResponseEntity.badRequest()
            .body(SignUpResponse(false, "Employee not found"))

        val user = User(
            name = req.name,
            email = req.email,
            password = req.password,
            employeeId = employee.employeeId,
            approvalStatus = ApprovalStatus.PENDING
        )

        userRepo.save(user)

        return ResponseEntity.ok(
            SignUpResponse(true, "Registration successful. Waiting for admin approval.")
        )
    }
}