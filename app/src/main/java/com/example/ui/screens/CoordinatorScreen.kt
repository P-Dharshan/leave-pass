package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.example.data.model.UserAccount
import com.example.ui.AdminAction
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CollegiateBlue
import com.example.ui.theme.CollegiateNavy
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald

@Composable
fun CoordinatorScreen(
    coordinator: UserAccount,
    requests: List<LeaveRequest>,
    onAction: (LeaveRequest, AdminAction, String, Context) -> Unit,
    onSelectRequest: (LeaveRequest) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showOnlyPending by remember { mutableStateOf(true) }

    val displayedRequests = if (showOnlyPending) {
        requests.filter { it.status == LeaveStatus.PENDING_REVIEW }
    } else {
        requests
    }

    val pendingCount = requests.count { it.status == LeaveStatus.PENDING_REVIEW }
    val endorsedCount = requests.count { it.status == LeaveStatus.FACULTY_APPROVED || it.status == LeaveStatus.FINAL_APPROVED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("coordinator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Coordinator Header Profile
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CollegiateNavy)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CLASS COORDINATOR (CC) PORTAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = coordinator.fullName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${coordinator.regNumber} • ${coordinator.department}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                        OutlinedButton(
                            onClick = onLogout,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Logout", color = Color.White, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Summary Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Awaiting CC Review", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("$pendingCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Endorsed / Cleared", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("$endorsedCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Section Title and Filter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Class Student Applications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )

                FilterChip(
                    selected = showOnlyPending,
                    onClick = { showOnlyPending = !showOnlyPending },
                    label = { Text(if (showOnlyPending) "Showing Pending" else "Showing All", fontSize = 11.sp) }
                )
            }
        }

        if (displayedRequests.isEmpty()) {
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
                        Icon(
                            Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = StatusEmerald,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No requests currently awaiting CC endorsement",
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "All student applications in your class have been reviewed.",
                            fontSize = 12.sp,
                            color = SlateTextMuted
                        )
                    }
                }
            }
        } else {
            items(displayedRequests) { req ->
                CoordinatorRequestCard(
                    request = req,
                    onEndorse = {
                        onAction(
                            req,
                            AdminAction.FACULTY_ENDORSE,
                            "Endorsed by Class Coordinator (${coordinator.fullName}). Forwarded to HOD for final clearance.",
                            context
                        )
                    },
                    onRequestProof = {
                        onAction(
                            req,
                            AdminAction.REQUEST_DOCUMENTS,
                            "Class Coordinator requested verified medical prescription or parent letter.",
                            context
                        )
                    },
                    onDecline = {
                        onAction(
                            req,
                            AdminAction.REJECT_REQUEST,
                            "Declined by Class Coordinator: Continuous absence conflict.",
                            context
                        )
                    },
                    onSelect = { onSelectRequest(req) }
                )
            }
        }
    }
}

@Composable
private fun CoordinatorRequestCard(
    request: LeaveRequest,
    onEndorse: () -> Unit,
    onRequestProof: () -> Unit,
    onDecline: () -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cc_req_${request.applicationNo}"),
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
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CollegiateBlue
                )
                StatusBadge(status = request.status, autoApproved = request.autoApproved)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${request.studentName} (${request.rollNumber})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = "Reason: ${request.reasonTitle}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = CollegiateNavy
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = request.detailedReason,
                fontSize = 12.sp,
                color = SlateTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dates: ${request.startDate} to ${request.endDate} (${request.durationDays}d)",
                    fontSize = 11.sp,
                    color = SlateTextMuted
                )
                if (request.proofFileName != null) {
                    Text(
                        text = "📎 ${request.proofFileName}",
                        fontSize = 11.sp,
                        color = CollegiateBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (request.status == LeaveStatus.PENDING_REVIEW) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = SlateCardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onEndorse,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusEmerald)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Endorse to HOD", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onRequestProof,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Request Proof", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onDecline,
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCrimson)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Decline", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
