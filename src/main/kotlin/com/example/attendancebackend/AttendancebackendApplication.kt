package com.example.attendancebackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import jakarta.annotation.PostConstruct
import java.util.TimeZone

@SpringBootApplication
class AttendancebackendApplication {

	// This will force JVM + Hibernate + MySQL to use IST
	@PostConstruct
	fun init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"))
		println(" Server TimeZone set to IST: " + TimeZone.getDefault().id)
	}
}

fun main(args: Array<String>) {
	runApplication<AttendancebackendApplication>(*args)
}
