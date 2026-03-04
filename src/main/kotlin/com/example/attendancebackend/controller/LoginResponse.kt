data class LoginResponse(
    val id: Long,
    val name: String,
    val email: String,
    val employeeId: Long?,
    val approvalStatus: String,
    val message: String,
    val role: String?
)