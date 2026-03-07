//package com.example.attendancebackend.controller
//
//import com.example.attendancebackend.dto.FingerprintDTO
//import com.example.attendancebackend.repository.UserRepository
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//
//@RestController
//@RequestMapping("/fingerprint")
//class FingerprintController(
//    private val userRepository: UserRepository
//) {
//
//    @PostMapping("/register")
//    fun registerFingerprint(@RequestBody dto: FingerprintDTO): ResponseEntity<Any> {
//        val user = userRepository.findById(dto.userId).orElse(null)
//            ?: return ResponseEntity.badRequest().body("User not found")
//
//        user.fingerprintHash = dto.fingerprintData
//        userRepository.save(user)
//
//        return ResponseEntity.ok("Fingerprint registered successfully")
//    }
//
//    @PostMapping("/verify")
//    fun verifyFingerprint(@RequestBody dto: FingerprintDTO): ResponseEntity<Any> {
//        val user = userRepository.findById(dto.userId).orElse(null)
//            ?: return ResponseEntity.badRequest().body("User not found")
//
//        return if (user.fingerprintHash == dto.fingerprintData) {
//            ResponseEntity.ok("Fingerprint verified")
//        } else {
//            ResponseEntity.badRequest().body("Fingerprint mismatch")
//        }
//    }
//}
