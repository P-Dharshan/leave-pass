package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.notification.NotificationHelper
import com.example.ui.AppTab
import com.example.ui.LeaveViewModel
import com.example.ui.screens.AdminApprovalScreen
import com.example.ui.screens.CoordinatorScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.RequestEditorScreen
import com.example.ui.screens.StatusTrackerScreen
import com.example.ui.theme.CollegiateBlue
import com.example.ui.theme.CollegiateNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald

class MainActivity : ComponentActivity() {

    private val viewModel: LeaveViewModel by viewModels {
        LeaveViewModel.provideFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.initChannel(this)

        val targetRequestId = intent.getLongExtra("OPEN_REQUEST_ID", -1L)
        if (targetRequestId != -1L) {
            viewModel.selectTab(AppTab.TRACKER)
        }

        setContent {
            MyApplicationTheme {
                MainAppScreen(
                    viewModel = viewModel,
                    initialRequestId = if (targetRequestId != -1L) targetRequestId else null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: LeaveViewModel,
    initialRequestId: Long? = null
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val studentProfile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val activeRequests by viewModel.activeRequests.collectAsStateWithLifecycle()
    val allRequests by viewModel.allRequests.collectAsStateWithLifecycle()
    val filteredRequests by viewModel.filteredRequests.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val autoApprovalRules by viewModel.autoApprovalRules.collectAsStateWithLifecycle()
    val selectedRequest by viewModel.selectedRequest.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showSwitchRoleDialog by remember { mutableStateOf(false) }

    // Request Notification permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(initialRequestId, allRequests) {
        if (initialRequestId != null && initialRequestId != -1L) {
            val found = allRequests.firstOrNull { it.id == initialRequestId }
            if (found != null) {
                viewModel.setSelectedRequest(found)
                viewModel.selectTab(AppTab.TRACKER)
            }
        }
    }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearFeedback()
        }
    }

    // Role switcher dialog
    if (showSwitchRoleDialog) {
        RoleSwitchDialog(
            currentAccount = currentUser,
            accounts = viewModel.demoAccounts,
            onSelect = { account ->
                viewModel.quickSwitchAccount(account)
                showSwitchRoleDialog = false
            },
            onDismiss = { showSwitchRoleDialog = false }
        )
    }

    // If not logged in, show Login Screen
    if (currentUser == null) {
        LoginScreen(
            demoAccounts = viewModel.demoAccounts,
            onLogin = { reg, pass -> viewModel.login(reg, pass) },
            errorMessage = loginError
        )
        return
    }

    val user = currentUser!!

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (user.role) {
                                    UserRole.STUDENT -> "🎓 ${user.fullName}"
                                    UserRole.CLASS_COORDINATOR -> "👨‍🏫 ${user.fullName}"
                                    UserRole.HEAD_OF_DEPARTMENT -> "🏛️ ${user.fullName}"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                        }
                        Text(
                            text = "${user.regNumber} • ${user.role.displayName}",
                            fontSize = 11.sp,
                            color = CollegiateBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSwitchRoleDialog = true },
                        modifier = Modifier.testTag("switch_role_button")
                    ) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = "Switch Account / Role",
                            tint = CollegiateNavy
                        )
                    }
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = StatusCrimson
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                if (user.role == UserRole.STUDENT) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.DASHBOARD,
                        onClick = { viewModel.selectTab(AppTab.DASHBOARD) },
                        icon = {
                            Icon(
                                if (currentTab == AppTab.DASHBOARD) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_dashboard"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.TRACKER,
                        onClick = { viewModel.selectTab(AppTab.TRACKER) },
                        icon = {
                            Icon(
                                if (currentTab == AppTab.TRACKER) Icons.Default.TrackChanges else Icons.Outlined.TrackChanges,
                                contentDescription = "Tracker"
                            )
                        },
                        label = { Text("Tracker", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_tracker"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.NEW_REQUEST,
                        onClick = { viewModel.selectTab(AppTab.NEW_REQUEST) },
                        icon = {
                            Icon(
                                if (currentTab == AppTab.NEW_REQUEST) Icons.Default.AddCircle else Icons.Outlined.AddCircleOutline,
                                contentDescription = "Apply"
                            )
                        },
                        label = { Text("Apply", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_new_request"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.NOTIFICATIONS,
                        onClick = { viewModel.selectTab(AppTab.NOTIFICATIONS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge { Text("$unreadCount") }
                                    }
                                }
                            ) {
                                Icon(
                                    if (currentTab == AppTab.NOTIFICATIONS) Icons.Default.Notifications else Icons.Outlined.Notifications,
                                    contentDescription = "Alerts"
                                )
                            }
                        },
                        label = { Text("Alerts", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_notifications"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )
                } else if (user.role == UserRole.CLASS_COORDINATOR) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.ADMIN_APPROVAL,
                        onClick = { viewModel.selectTab(AppTab.ADMIN_APPROVAL) },
                        icon = {
                            Icon(Icons.Default.HowToReg, contentDescription = "CC Reviews")
                        },
                        label = { Text("CC Reviews", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_cc_reviews"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.TRACKER,
                        onClick = { viewModel.selectTab(AppTab.TRACKER) },
                        icon = {
                            Icon(Icons.Default.TrackChanges, contentDescription = "Class Tracker")
                        },
                        label = { Text("Tracker", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_tracker"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.NOTIFICATIONS,
                        onClick = { viewModel.selectTab(AppTab.NOTIFICATIONS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge { Text("$unreadCount") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                            }
                        },
                        label = { Text("Alerts", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_notifications"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )
                } else {
                    // HEAD OF DEPARTMENT (HOD)
                    NavigationBarItem(
                        selected = currentTab == AppTab.ADMIN_APPROVAL,
                        onClick = { viewModel.selectTab(AppTab.ADMIN_APPROVAL) },
                        icon = {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "HOD Clearance")
                        },
                        label = { Text("HOD Portal", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_hod_clearance"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.TRACKER,
                        onClick = { viewModel.selectTab(AppTab.TRACKER) },
                        icon = {
                            Icon(Icons.Default.TrackChanges, contentDescription = "All Leaves")
                        },
                        label = { Text("Tracker", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_tracker"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.NOTIFICATIONS,
                        onClick = { viewModel.selectTab(AppTab.NOTIFICATIONS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge { Text("$unreadCount") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                            }
                        },
                        label = { Text("Alerts", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_notifications"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CollegiateNavy,
                            selectedTextColor = CollegiateNavy,
                            indicatorColor = CollegiateBlue.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.DASHBOARD -> {
                    DashboardScreen(
                        student = studentProfile,
                        activeRequests = activeRequests,
                        recentRequests = allRequests,
                        unreadNotifCount = unreadCount,
                        onNavigateTab = { viewModel.selectTab(it) },
                        onSelectRequest = { req ->
                            viewModel.setSelectedRequest(req)
                        }
                    )
                }
                AppTab.TRACKER -> {
                    StatusTrackerScreen(
                        requests = filteredRequests,
                        selectedRequest = selectedRequest,
                        searchQuery = searchQuery,
                        statusFilter = statusFilter,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onStatusFilterChange = { viewModel.setStatusFilter(it) },
                        onSelectRequest = { viewModel.setSelectedRequest(it) },
                        onSimulateAdminReview = { req, action, remarks, ctx ->
                            viewModel.processAdminReview(req, action, remarks, ctx)
                        },
                        onDeleteRequest = { viewModel.deleteRequest(it) }
                    )
                }
                AppTab.NEW_REQUEST -> {
                    RequestEditorScreen(
                        student = studentProfile,
                        rules = autoApprovalRules,
                        onSubmitRequest = { cat, start, end, dur, sess, title, reason, pContact, sContact, proof, ctx ->
                            viewModel.submitLeaveRequest(
                                category = cat,
                                startDate = start,
                                endDate = end,
                                durationDays = dur,
                                sessionType = sess,
                                reasonTitle = title,
                                detailedReason = reason,
                                parentContact = pContact,
                                studentContact = sContact,
                                proofFileName = proof,
                                context = ctx
                            )
                        }
                    )
                }
                AppTab.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onNotificationClick = { notif ->
                            viewModel.markNotificationAsRead(notif.id)
                            val req = allRequests.firstOrNull { it.id == notif.requestId }
                            if (req != null) {
                                viewModel.setSelectedRequest(req)
                                viewModel.selectTab(AppTab.TRACKER)
                            }
                        },
                        onMarkAllRead = { viewModel.markAllNotificationsAsRead() }
                    )
                }
                AppTab.ADMIN_APPROVAL -> {
                    if (user.role == UserRole.CLASS_COORDINATOR) {
                        CoordinatorScreen(
                            coordinator = user,
                            requests = allRequests,
                            onAction = { req, action, remarks, ctx ->
                                viewModel.processAdminReview(req, action, remarks, ctx)
                            },
                            onSelectRequest = { req ->
                                viewModel.setSelectedRequest(req)
                                viewModel.selectTab(AppTab.TRACKER)
                            },
                            onLogout = { viewModel.logout() }
                        )
                    } else {
                        AdminApprovalScreen(
                            rules = autoApprovalRules,
                            pendingRequests = allRequests.filter {
                                it.status == com.example.data.model.LeaveStatus.PENDING_REVIEW ||
                                        it.status == com.example.data.model.LeaveStatus.FACULTY_APPROVED ||
                                        it.status == com.example.data.model.LeaveStatus.ADMIN_IN_PROGRESS
                            },
                            isProcessing = isProcessing,
                            onUpdateRules = { viewModel.updateAutoApprovalRules(it) },
                            onRunBatchEngine = { ctx -> viewModel.runAutomatedBatchProcessing(ctx) },
                            onAction = { req, action, remarks, ctx ->
                                viewModel.processAdminReview(req, action, remarks, ctx)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSwitchDialog(
    currentAccount: UserAccount?,
    accounts: List<UserAccount>,
    onSelect: (UserAccount) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Switch Role / User Account", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Test the system across Student, Class Coordinator (CC), and HOD perspectives:",
                    fontSize = 12.sp,
                    color = SlateTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                accounts.forEach { account ->
                    val isCurrent = account.regNumber == currentAccount?.regNumber
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(account) }
                            .testTag("switch_to_${account.regNumber}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) CollegiateNavy.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            width = if (isCurrent) 1.5.dp else 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (isCurrent) CollegiateNavy else SlateCardBorder
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = when (account.role) {
                                            UserRole.STUDENT -> "🎓 Student"
                                            UserRole.CLASS_COORDINATOR -> "👨‍🏫 CC"
                                            UserRole.HEAD_OF_DEPARTMENT -> "🏛️ HOD"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CollegiateNavy
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = account.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = SlateTextPrimary
                                    )
                                }
                                Text(
                                    text = "Reg No: ${account.regNumber} • DOB: ${account.dateOfBirth}",
                                    fontSize = 11.sp,
                                    color = SlateTextMuted
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
