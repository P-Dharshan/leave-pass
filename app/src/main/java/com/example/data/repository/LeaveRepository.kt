package com.example.data.repository

import com.example.data.local.LeaveDao
import com.example.data.local.NotificationDao
import com.example.data.model.LeaveCategory
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.StudentProfile
import kotlinx.coroutines.flow.Flow

class LeaveRepository(
    private val leaveDao: LeaveDao,
    private val notificationDao: NotificationDao
) {
    val allRequests: Flow<List<LeaveRequest>> = leaveDao.getAllRequests()
    val activeRequests: Flow<List<LeaveRequest>> = leaveDao.getActiveRequests()
    val approvedRequests: Flow<List<LeaveRequest>> = leaveDao.getApprovedRequests()
    val allNotifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()

    fun getRequestById(id: Long): Flow<LeaveRequest?> = leaveDao.getRequestById(id)

    suspend fun insertRequest(request: LeaveRequest): Long = leaveDao.insertRequest(request)

    suspend fun updateRequest(request: LeaveRequest) = leaveDao.updateRequest(request)

    suspend fun deleteRequest(id: Long) = leaveDao.deleteById(id)

    suspend fun addNotification(notification: NotificationItem): Long =
        notificationDao.insertNotification(notification)

    suspend fun markNotificationRead(id: Long) = notificationDao.markAsRead(id)

    suspend fun markAllNotificationsRead() = notificationDao.markAllAsRead()

    suspend fun clearNotifications() = notificationDao.clearAll()

    suspend fun seedInitialDataIfNeeded(student: StudentProfile) {
        if (leaveDao.getCount() > 0) return

        val now = System.currentTimeMillis()
        val oneDayMillis = 86_400_000L

        // Sample 1: Auto-Approved Academic Hackathon Pass
        val req1 = LeaveRequest(
            applicationNo = "LP-2026-0814",
            studentName = student.fullName,
            rollNumber = student.rollNumber,
            department = student.department,
            semester = student.batch,
            hostelStatus = student.hostelRoom,
            category = LeaveCategory.ACADEMIC_EVENT,
            startDate = "2026-09-20",
            endDate = "2026-09-21",
            durationDays = 2,
            sessionType = "Full Day",
            reasonTitle = "MIT Hackathon 2026 Participation",
            detailedReason = "Selected as finalist in collegiate developer hackathon. Requires leave to travel and attend 36-hour build sprint with campus coding club.",
            parentContact = student.parentPhone,
            studentContact = student.emergencyPhone,
            facultyAdvisor = student.facultyAdvisor,
            proofFileName = "hackathon_invitation_letter.pdf",
            status = LeaveStatus.AUTO_APPROVED,
            currentStage = "Automated Security Gate Pass Active",
            autoApproved = true,
            autoApprovalNote = "System verified: Attendance 87.5% >= 75%, duration 2 days <= auto-cap, validated academic category.",
            reviewerRemarks = "Auto-cleared by College Academic Portal. Gate pass generated.",
            gatePassCode = "GP-ACAD-88492-OK",
            isGatePassScanned = false,
            createdAt = now - (oneDayMillis * 2),
            updatedAt = now - (oneDayMillis * 2)
        )
        val id1 = leaveDao.insertRequest(req1)

        notificationDao.insertNotification(
            NotificationItem(
                requestId = id1,
                applicationNo = "LP-2026-0814",
                title = "⚡ Request Auto-Approved!",
                message = "Your leave request LP-2026-0814 for MIT Hackathon 2026 has been automatically verified and approved! Your digital gate pass is ready.",
                type = NotificationType.AUTO_APPROVAL_SUCCESS,
                timestamp = now - (oneDayMillis * 2)
            )
        )

        // Sample 2: Currently in multi-stage review tracker (Medical Leave)
        val req2 = LeaveRequest(
            applicationNo = "LP-2026-0922",
            studentName = student.fullName,
            rollNumber = student.rollNumber,
            department = student.department,
            semester = student.batch,
            hostelStatus = student.hostelRoom,
            category = LeaveCategory.MEDICAL,
            startDate = "2026-09-24",
            endDate = "2026-09-27",
            durationDays = 4,
            sessionType = "Full Day",
            reasonTitle = "Viral Fever & Medical Rest",
            detailedReason = "Diagnosed with acute viral flu by campus physician. Doctor advised 4 days complete bed rest and clinical quarantine in home care.",
            parentContact = student.parentPhone,
            studentContact = student.emergencyPhone,
            facultyAdvisor = student.facultyAdvisor,
            proofFileName = "medical_prescription_clinic.pdf",
            status = LeaveStatus.FACULTY_APPROVED,
            currentStage = "Under Department HOD & Admin Clearance",
            autoApproved = false,
            autoApprovalNote = "Multi-day medical request (>2 days) routed to manual multi-step faculty workflow.",
            reviewerRemarks = "Dr. Sarah Jenkins: Endorsed medical document. Forwarded to HOD for final approval.",
            gatePassCode = "GP-MED-PROV-9912",
            isGatePassScanned = false,
            createdAt = now - (oneDayMillis * 1),
            updatedAt = now - (3600_000L * 4)
        )
        val id2 = leaveDao.insertRequest(req2)

        notificationDao.insertNotification(
            NotificationItem(
                requestId = id2,
                applicationNo = "LP-2026-0922",
                title = "Faculty Advisor Endorsed",
                message = "Dr. Sarah Jenkins has reviewed and approved your medical leave (LP-2026-0922). Request forwarded to Department HOD.",
                type = NotificationType.FACULTY_ENDORSED,
                timestamp = now - (3600_000L * 4)
            )
        )
    }
}
