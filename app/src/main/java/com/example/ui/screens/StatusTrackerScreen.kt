package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.ui.AdminAction
import com.example.ui.components.GatePassCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.StepTrackerView
import com.example.ui.theme.CollegiateBlue
import com.example.ui.theme.CollegiateNavy
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusTrackerScreen(
    requests: List<LeaveRequest>,
    selectedRequest: LeaveRequest?,
    searchQuery: String,
    statusFilter: LeaveStatus?,
    onSearchChange: (String) -> Unit,
    onStatusFilterChange: (LeaveStatus?) -> Unit,
    onSelectRequest: (LeaveRequest?) -> Unit,
    onSimulateAdminReview: (LeaveRequest, AdminAction, String, Context) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf<Long?>(null) }
    var showSimulationDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Request") },
            text = { Text("Are you sure you want to delete this leave application from your records?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm?.let { onDeleteRequest(it) }
                        showDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCrimson)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSimulationDialog && selectedRequest != null) {
        SimulateAdminReviewDialog(
            request = selectedRequest,
            onDismiss = { showSimulationDialog = false },
            onAction = { action, remarks ->
                onSimulateAdminReview(selectedRequest, action, remarks, context)
                showSimulationDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("status_tracker_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // If a request is actively selected for deep status tracking:
        if (selectedRequest != null) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { onSelectRequest(null) },
                        modifier = Modifier.testTag("back_to_list_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedRequest.applicationNo,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "Live Status & Administration Tracker",
                            fontSize = 12.sp,
                            color = SlateTextSecondary
                        )
                    }
                    IconButton(onClick = { showDeleteConfirm = selectedRequest.id }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusCrimson)
                    }
                }
            }

            // Status Overview Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedRequest.reasonTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            StatusBadge(
                                status = selectedRequest.status,
                                autoApproved = selectedRequest.autoApproved
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = selectedRequest.detailedReason,
                            fontSize = 13.sp,
                            color = SlateTextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SlateCardBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("CATEGORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)
                                Text(selectedRequest.category.displayName, fontSize = 12.sp, color = SlateTextPrimary)
                            }
                            Column {
                                Text("TIMEFRAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)
                                Text("${selectedRequest.startDate} - ${selectedRequest.endDate}", fontSize = 12.sp, color = SlateTextPrimary)
                            }
                            Column {
                                Text("DURATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)
                                Text("${selectedRequest.durationDays} day(s)", fontSize = 12.sp, color = SlateTextPrimary)
                            }
                        }

                        if (selectedRequest.reviewerRemarks != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Official Remarks / Audit Note",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CollegiateBlue
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = selectedRequest.reviewerRemarks,
                                        fontSize = 12.sp,
                                        color = SlateTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Real-Time Step Tracker Pipeline
            item {
                StepTrackerView(request = selectedRequest)
            }

            // If Approved: Display Official Digital Gate Pass
            if (selectedRequest.status == LeaveStatus.AUTO_APPROVED || selectedRequest.status == LeaveStatus.FINAL_APPROVED) {
                item {
                    Column {
                        Text(
                            text = "Official Security Gate Pass",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GatePassCard(request = selectedRequest)
                    }
                }
            }

            // Quick Administration Simulation Actions (to test live push notifications)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = CollegiateNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Simulate Administration / Professor Review",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CollegiateNavy
                            )
                        }
                        Text(
                            text = "Test instant push notifications and status updates as administration processes this request.",
                            fontSize = 11.sp,
                            color = SlateTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showSimulationDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("simulate_review_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CollegiateNavy)
                            ) {
                                Text("Process Review Action", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // Master List View
            item {
                Column {
                    Text(
                        text = "Real-Time Status Tracker",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Monitor faculty verification, administration sign-offs, and pass releases in real time.",
                        fontSize = 13.sp,
                        color = SlateTextSecondary
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tracker_search_input"),
                    placeholder = { Text("Search by ID (LP-...) or keyword") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = SlateTextMuted)
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Status Filter Chips
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = statusFilter == null,
                        onClick = { onStatusFilterChange(null) },
                        label = { Text("All (${requests.size})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = statusFilter == LeaveStatus.PENDING_REVIEW,
                        onClick = {
                            onStatusFilterChange(
                                if (statusFilter == LeaveStatus.PENDING_REVIEW) null else LeaveStatus.PENDING_REVIEW
                            )
                        },
                        label = { Text("Pending Review", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = statusFilter == LeaveStatus.AUTO_APPROVED,
                        onClick = {
                            onStatusFilterChange(
                                if (statusFilter == LeaveStatus.AUTO_APPROVED) null else LeaveStatus.AUTO_APPROVED
                            )
                        },
                        label = { Text("Auto-Approved", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = statusFilter == LeaveStatus.FINAL_APPROVED,
                        onClick = {
                            onStatusFilterChange(
                                if (statusFilter == LeaveStatus.FINAL_APPROVED) null else LeaveStatus.FINAL_APPROVED
                            )
                        },
                        label = { Text("Administration Approved", fontSize = 11.sp) }
                    )
                }
            }

            // Requests List
            if (requests.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No matching leave requests found",
                                fontWeight = FontWeight.Medium,
                                color = SlateTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try adjusting your search query or status filter",
                                fontSize = 12.sp,
                                color = SlateTextMuted
                            )
                        }
                    }
                }
            } else {
                items(requests) { request ->
                    LeaveTrackerCard(
                        request = request,
                        onClick = { onSelectRequest(request) }
                    )
                }
            }
        }
    }
}

@Composable
fun LeaveTrackerCard(
    request: LeaveRequest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("tracker_item_${request.applicationNo}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.applicationNo,
                    fontSize = 13.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CollegiateBlue
                )
                StatusBadge(status = request.status, autoApproved = request.autoApproved)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = request.reasonTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = request.currentStage,
                fontSize = 12.sp,
                color = CollegiateNavy,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SlateCardBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${request.startDate} to ${request.endDate} • ${request.durationDays}d",
                    fontSize = 12.sp,
                    color = SlateTextSecondary
                )
                Text(
                    text = "Tap for Pipeline & Gate Pass →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CollegiateBlue
                )
            }
        }
    }
}

@Composable
private fun SimulateAdminReviewDialog(
    request: LeaveRequest,
    onDismiss: () -> Unit,
    onAction: (AdminAction, String) -> Unit
) {
    var remarks by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Simulate Review: ${request.applicationNo}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Select an administrative action to trigger an immediate status change and push alert for student:",
                    fontSize = 12.sp,
                    color = SlateTextSecondary
                )

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Reviewer Remarks (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        onAction(AdminAction.ADMIN_APPROVE_FINAL, remarks.ifBlank { "Administration final clearance granted." })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusEmerald)
                ) {
                    Text("🎓 Final Administration Approve & Issue Pass")
                }

                Button(
                    onClick = {
                        onAction(AdminAction.FACULTY_ENDORSE, remarks.ifBlank { "Faculty Advisor endorsed request." })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CollegiateNavy)
                ) {
                    Text("👨‍🏫 Faculty Advisor Endorsement")
                }

                Button(
                    onClick = {
                        onAction(AdminAction.REQUEST_DOCUMENTS, remarks.ifBlank { "Please upload proof of medical prescription." })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusAmber)
                ) {
                    Text("⚠️ Request Supporting Document")
                }

                Button(
                    onClick = {
                        onAction(AdminAction.REJECT_REQUEST, remarks.ifBlank { "Declined due to ongoing mid-term examination week." })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCrimson)
                ) {
                    Text("❌ Decline Leave Application")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
