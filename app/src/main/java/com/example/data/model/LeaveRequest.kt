package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LeaveCategory(val displayName: String, val badgeColorHex: Long) {
    MEDICAL("Medical / Health", 0xFF059669),
    ACADEMIC_EVENT("Academic / Hackathon / Conference", 0xFF2563EB),
    EMERGENCY("Emergency / Family", 0xFFDC2626),
    ON_DUTY("On-Duty (OD) / Sports", 0xFF7C3AED),
    WEEKEND_HOME_PASS("Weekend Home Pass", 0xFFD97706)
}

enum class LeaveStatus(val displayName: String, val stepIndex: Int) {
    PENDING_REVIEW("Pending Verification", 1),
    AUTO_APPROVED("Auto-Approved by System", 4),
    FACULTY_APPROVED("Faculty Endorsed", 2),
    ADMIN_IN_PROGRESS("Admin Processing", 3),
    FINAL_APPROVED("Fully Approved & Issued", 4),
    REJECTED("Request Declined", 0),
    ACTION_REQUIRED("Needs Document / Clarification", 1)
}

@Entity(tableName = "leave_requests")
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val applicationNo: String,
    val studentName: String,
    val rollNumber: String,
    val department: String,
    val semester: String,
    val hostelStatus: String,
    val category: LeaveCategory,
    val startDate: String,
    val endDate: String,
    val durationDays: Int,
    val sessionType: String = "Full Day",
    val reasonTitle: String,
    val detailedReason: String,
    val parentContact: String,
    val studentContact: String,
    val facultyAdvisor: String,
    val proofFileName: String? = null,
    val status: LeaveStatus = LeaveStatus.PENDING_REVIEW,
    val currentStage: String = "Submission Received",
    val autoApproved: Boolean = false,
    val autoApprovalNote: String? = null,
    val reviewerRemarks: String? = null,
    val gatePassCode: String = "",
    val isGatePassScanned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
