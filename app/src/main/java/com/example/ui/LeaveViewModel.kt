package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AutoApprovalRules
import com.example.data.model.LeaveCategory
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.StudentProfile
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.repository.AuthRepository
import com.example.data.repository.LeaveRepository
import com.example.notification.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppTab {
    DASHBOARD,
    TRACKER,
    NEW_REQUEST,
    NOTIFICATIONS,
    ADMIN_APPROVAL
}

enum class AdminAction {
    FACULTY_ENDORSE,
    ADMIN_APPROVE_FINAL,
    REJECT_REQUEST,
    REQUEST_DOCUMENTS
}

class LeaveViewModel(
    application: Application,
    private val repository: LeaveRepository,
    private val authRepository: AuthRepository = AuthRepository()
) : AndroidViewModel(application) {

    val currentUser: StateFlow<UserAccount?> = authRepository.currentUser
    val demoAccounts: List<UserAccount> get() = authRepository.getAllDemoAccounts()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _studentProfile = MutableStateFlow(
        authRepository.currentUser.value?.studentProfile ?: StudentProfile()
    )
    val studentProfile: StateFlow<StudentProfile> = _studentProfile.asStateFlow()

    private val _autoApprovalRules = MutableStateFlow(AutoApprovalRules())
    val autoApprovalRules: StateFlow<AutoApprovalRules> = _autoApprovalRules.asStateFlow()

    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _selectedRequest = MutableStateFlow<LeaveRequest?>(null)
    val selectedRequest: StateFlow<LeaveRequest?> = _selectedRequest.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<LeaveStatus?>(null)
    val selectedStatusFilter: StateFlow<LeaveStatus?> = _selectedStatusFilter.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    val allRequests: StateFlow<List<LeaveRequest>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredRequests: StateFlow<List<LeaveRequest>> = combine(
        allRequests,
        _searchQuery,
        _selectedStatusFilter
    ) { list, query, filter ->
        list.filter { req ->
            val matchesQuery = query.isBlank() ||
                    req.applicationNo.contains(query, ignoreCase = true) ||
                    req.reasonTitle.contains(query, ignoreCase = true) ||
                    req.category.displayName.contains(query, ignoreCase = true)
            val matchesFilter = filter == null || req.status == filter
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeRequests: StateFlow<List<LeaveRequest>> = repository.activeRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val approvedRequests: StateFlow<List<LeaveRequest>> = repository.approvedRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded(_studentProfile.value)
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSelectedRequest(request: LeaveRequest?) {
        _selectedRequest.value = request
    }

    fun login(regNumber: String, passwordDob: String): Boolean {
        _loginError.value = null
        when (val result = authRepository.login(regNumber, passwordDob)) {
            is AuthRepository.AuthResult.Success -> {
                val user = result.user
                if (user.studentProfile != null) {
                    _studentProfile.value = user.studentProfile
                }
                _feedbackMessage.value = "Signed in successfully as ${user.fullName} (${user.role.displayName})"
                when (user.role) {
                    UserRole.STUDENT -> _currentTab.value = AppTab.DASHBOARD
                    UserRole.CLASS_COORDINATOR -> _currentTab.value = AppTab.ADMIN_APPROVAL
                    UserRole.HEAD_OF_DEPARTMENT -> _currentTab.value = AppTab.ADMIN_APPROVAL
                }
                return true
            }
            is AuthRepository.AuthResult.Error -> {
                _loginError.value = result.message
                return false
            }
        }
    }

    fun quickSwitchAccount(user: UserAccount) {
        authRepository.quickSwitchAccount(user)
        if (user.studentProfile != null) {
            _studentProfile.value = user.studentProfile
        }
        _feedbackMessage.value = "Switched to ${user.fullName} (${user.role.displayName})"
        when (user.role) {
            UserRole.STUDENT -> _currentTab.value = AppTab.DASHBOARD
            UserRole.CLASS_COORDINATOR -> _currentTab.value = AppTab.ADMIN_APPROVAL
            UserRole.HEAD_OF_DEPARTMENT -> _currentTab.value = AppTab.ADMIN_APPROVAL
        }
    }

    fun logout() {
        authRepository.logout()
        _selectedRequest.value = null
        _feedbackMessage.value = "Logged out successfully."
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: LeaveStatus?) {
        _selectedStatusFilter.value = status
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun updateAutoApprovalRules(rules: AutoApprovalRules) {
        _autoApprovalRules.value = rules
        _feedbackMessage.value = "Automated Approval Policy updated successfully."
    }

    fun submitLeaveRequest(
        category: LeaveCategory,
        startDate: String,
        endDate: String,
        durationDays: Int,
        sessionType: String,
        reasonTitle: String,
        detailedReason: String,
        parentContact: String,
        studentContact: String,
        proofFileName: String?,
        context: Context
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            val student = _studentProfile.value
            val rules = _autoApprovalRules.value

            val (isEligibleForAutoApprove, evaluationReason) = rules.evaluateEligibility(
                student = student,
                category = category,
                durationDays = durationDays,
                hasProof = !proofFileName.isNullOrBlank()
            )

            val applicationNo = "LP-${System.currentTimeMillis().toString().takeLast(6)}"
            val now = System.currentTimeMillis()

            val newRequest: LeaveRequest
            val notifTitle: String
            val notifBody: String
            val notifType: NotificationType

            if (isEligibleForAutoApprove) {
                val gatePass = "GP-${category.name.take(4)}-${Random.nextInt(10000, 99999)}"
                newRequest = LeaveRequest(
                    applicationNo = applicationNo,
                    studentName = student.fullName,
                    rollNumber = student.rollNumber,
                    department = student.department,
                    semester = student.batch,
                    hostelStatus = student.hostelRoom,
                    category = category,
                    startDate = startDate,
                    endDate = endDate,
                    durationDays = durationDays,
                    sessionType = sessionType,
                    reasonTitle = reasonTitle,
                    detailedReason = detailedReason,
                    parentContact = parentContact,
                    studentContact = studentContact,
                    facultyAdvisor = student.facultyAdvisor,
                    proofFileName = proofFileName,
                    status = LeaveStatus.AUTO_APPROVED,
                    currentStage = "Automated System Verification: Approved & Gate Pass Ready",
                    autoApproved = true,
                    autoApprovalNote = evaluationReason,
                    reviewerRemarks = "Automated Collegiate System: All policy criteria met. Instant permission confirmed.",
                    gatePassCode = gatePass,
                    createdAt = now,
                    updatedAt = now
                )
                notifTitle = "⚡ Leave Instantly Auto-Approved!"
                notifBody = "Your leave ($applicationNo) has been automatically approved. Digital gate pass is active."
                notifType = NotificationType.AUTO_APPROVAL_SUCCESS
            } else {
                newRequest = LeaveRequest(
                    applicationNo = applicationNo,
                    studentName = student.fullName,
                    rollNumber = student.rollNumber,
                    department = student.department,
                    semester = student.batch,
                    hostelStatus = student.hostelRoom,
                    category = category,
                    startDate = startDate,
                    endDate = endDate,
                    durationDays = durationDays,
                    sessionType = sessionType,
                    reasonTitle = reasonTitle,
                    detailedReason = detailedReason,
                    parentContact = parentContact,
                    studentContact = studentContact,
                    facultyAdvisor = student.facultyAdvisor,
                    proofFileName = proofFileName,
                    status = LeaveStatus.PENDING_REVIEW,
                    currentStage = "Step 1/4: Submitted to Faculty Advisor (${student.facultyAdvisor})",
                    autoApproved = false,
                    autoApprovalNote = evaluationReason,
                    reviewerRemarks = "Awaiting initial faculty advisor sign-off.",
                    gatePassCode = "GP-PROV-${Random.nextInt(1000, 9999)}",
                    createdAt = now,
                    updatedAt = now
                )
                notifTitle = "📋 Leave Application Submitted"
                notifBody = "Request $applicationNo submitted. Track progress in the real-time status monitor."
                notifType = NotificationType.SYSTEM_INFO
            }

            val insertedId = repository.insertRequest(newRequest)
            val createdRequestWithId = newRequest.copy(id = insertedId)

            repository.addNotification(
                NotificationItem(
                    requestId = insertedId,
                    applicationNo = applicationNo,
                    title = notifTitle,
                    message = notifBody,
                    type = notifType,
                    timestamp = now
                )
            )

            // Send real push notification immediately
            NotificationHelper.sendPushNotification(
                context = context,
                title = notifTitle,
                message = notifBody,
                requestId = insertedId
            )

            _isProcessing.value = false
            _selectedRequest.value = createdRequestWithId
            _currentTab.value = AppTab.TRACKER
            _feedbackMessage.value = if (isEligibleForAutoApprove) {
                "Application Auto-Approved! Security Gate Pass generated."
            } else {
                "Request submitted successfully. Notification alert dispatched."
            }
        }
    }

    fun processAdminReview(
        request: LeaveRequest,
        action: AdminAction,
        remarks: String,
        context: Context
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            val now = System.currentTimeMillis()

            val updatedRequest = when (action) {
                AdminAction.FACULTY_ENDORSE -> {
                    request.copy(
                        status = LeaveStatus.FACULTY_APPROVED,
                        currentStage = "Step 2/4: Faculty Endorsed -> Forwarded to Head of Department",
                        reviewerRemarks = remarks.ifBlank { "Dr. Sarah Jenkins: Validated request. Approved." },
                        updatedAt = now
                    )
                }
                AdminAction.ADMIN_APPROVE_FINAL -> {
                    val passCode = if (request.gatePassCode.startsWith("GP-PASS")) {
                        request.gatePassCode
                    } else {
                        "GP-AUTH-${Random.nextInt(10000, 99999)}-ADMIN"
                    }
                    request.copy(
                        status = LeaveStatus.FINAL_APPROVED,
                        currentStage = "Step 4/4: Administration Approved & Official Gate Pass Issued",
                        reviewerRemarks = remarks.ifBlank { "Administration Office: Leave granted. Gate clearance issued." },
                        gatePassCode = passCode,
                        updatedAt = now
                    )
                }
                AdminAction.REJECT_REQUEST -> {
                    request.copy(
                        status = LeaveStatus.REJECTED,
                        currentStage = "Application Processed: Declined",
                        reviewerRemarks = remarks.ifBlank { "Declined: Insufficient academic justification or schedule conflict." },
                        updatedAt = now
                    )
                }
                AdminAction.REQUEST_DOCUMENTS -> {
                    request.copy(
                        status = LeaveStatus.ACTION_REQUIRED,
                        currentStage = "Action Required: Supporting Document Missing",
                        reviewerRemarks = remarks.ifBlank { "Please attach verified medical certificate or event registration copy." },
                        updatedAt = now
                    )
                }
            }

            repository.updateRequest(updatedRequest)
            _selectedRequest.value = updatedRequest

            val (notifTitle, notifBody, notifType) = when (action) {
                AdminAction.FACULTY_ENDORSE -> Triple(
                    "👨‍🏫 Faculty Advisor Endorsed",
                    "Your leave application ${request.applicationNo} was endorsed and sent for administration clearance.",
                    NotificationType.FACULTY_ENDORSED
                )
                AdminAction.ADMIN_APPROVE_FINAL -> Triple(
                    "🎓 Official Administration Approval",
                    "Your leave ${request.applicationNo} has been officially approved! Digital Gate Pass is now active.",
                    NotificationType.ADMIN_APPROVED
                )
                AdminAction.REJECT_REQUEST -> Triple(
                    "❌ Leave Request Declined",
                    "Application ${request.applicationNo} was not approved: ${updatedRequest.reviewerRemarks}",
                    NotificationType.REQUEST_REJECTED
                )
                AdminAction.REQUEST_DOCUMENTS -> Triple(
                    "⚠️ Additional Document Required",
                    "Action needed on ${request.applicationNo}: ${updatedRequest.reviewerRemarks}",
                    NotificationType.ACTION_REQUIRED
                )
            }

            repository.addNotification(
                NotificationItem(
                    requestId = request.id,
                    applicationNo = request.applicationNo,
                    title = notifTitle,
                    message = notifBody,
                    type = notifType,
                    timestamp = now
                )
            )

            // Alert student immediately with push notification
            NotificationHelper.sendPushNotification(
                context = context,
                title = notifTitle,
                message = notifBody,
                requestId = request.id
            )

            _isProcessing.value = false
            _feedbackMessage.value = "Request updated and push alert sent to student."
        }
    }

    fun runAutomatedBatchProcessing(context: Context) {
        viewModelScope.launch {
            _isProcessing.value = true
            val pendingList = allRequests.value.filter {
                it.status == LeaveStatus.PENDING_REVIEW || it.status == LeaveStatus.FACULTY_APPROVED
            }

            if (pendingList.isEmpty()) {
                _isProcessing.value = false
                _feedbackMessage.value = "No pending requests awaiting processing."
                return@launch
            }

            var autoApprovedCount = 0
            val rules = _autoApprovalRules.value
            val student = _studentProfile.value

            for (req in pendingList) {
                delay(300) // Brief simulation step
                val (eligible, reason) = rules.evaluateEligibility(
                    student = student,
                    category = req.category,
                    durationDays = req.durationDays,
                    hasProof = !req.proofFileName.isNullOrBlank()
                )

                if (eligible) {
                    val passCode = "GP-AUTO-${Random.nextInt(10000, 99999)}"
                    val updated = req.copy(
                        status = LeaveStatus.AUTO_APPROVED,
                        currentStage = "Automated Administration Batch Engine: Verified & Passed",
                        autoApproved = true,
                        autoApprovalNote = reason,
                        reviewerRemarks = "Auto-cleared via Automated Approval Rule Engine.",
                        gatePassCode = passCode,
                        updatedAt = System.currentTimeMillis()
                    )
                    repository.updateRequest(updated)

                    val notifTitle = "⚡ Automated Approval Alert"
                    val notifMsg = "Your leave ${req.applicationNo} was automatically cleared by the administration engine."
                    repository.addNotification(
                        NotificationItem(
                            requestId = req.id,
                            applicationNo = req.applicationNo,
                            title = notifTitle,
                            message = notifMsg,
                            type = NotificationType.AUTO_APPROVAL_SUCCESS,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    NotificationHelper.sendPushNotification(
                        context = context,
                        title = notifTitle,
                        message = notifMsg,
                        requestId = req.id
                    )
                    autoApprovedCount++
                }
            }

            _isProcessing.value = false
            _feedbackMessage.value = "Automated Engine processed ${pendingList.size} requests ($autoApprovedCount auto-approved with alerts sent)."
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun deleteRequest(id: Long) {
        viewModelScope.launch {
            repository.deleteRequest(id)
            if (_selectedRequest.value?.id == id) {
                _selectedRequest.value = null
            }
            _feedbackMessage.value = "Request removed from record."
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val database = AppDatabase.getDatabase(application)
                    val repository = LeaveRepository(database.leaveDao(), database.notificationDao())
                    return LeaveViewModel(application, repository) as T
                }
            }
    }
}
