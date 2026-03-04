package com.example.attendancebackend.service

import com.example.attendancebackend.model.DeviceChangeRequest
import com.example.attendancebackend.model.RequestStatus
import com.example.attendancebackend.repository.DeviceChangeRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class DeviceChangeRequestService(
    private val deviceChangeRequestRepository: DeviceChangeRequestRepository
) {

    // Save new request
    fun saveRequest(request: DeviceChangeRequest): DeviceChangeRequest {
        return deviceChangeRequestRepository.save(request)
    }

    // Get all pending requests (admin dashboard)
    fun getPendingRequests(): List<DeviceChangeRequest> {
        return deviceChangeRequestRepository.findByStatus(RequestStatus.PENDING)
            .sortedByDescending { it.requestedAt } // sort by requestedAt
    }

    // Approve a request
    fun approveRequest(id: Long): DeviceChangeRequest {
        val request = deviceChangeRequestRepository.findById(id)
            .orElseThrow { RuntimeException("Request not found") }

        request.status = RequestStatus.APPROVED
        request.approvedAt = LocalDateTime.now()
        return deviceChangeRequestRepository.save(request)
    }

    // Reject a request
    fun rejectRequest(id: Long): DeviceChangeRequest {
        val request = deviceChangeRequestRepository.findById(id)
            .orElseThrow { RuntimeException("Request not found") }

        request.status = RequestStatus.REJECTED
        return deviceChangeRequestRepository.save(request)
    }
}