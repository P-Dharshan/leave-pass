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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AutoApprovalRules
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.ui.AdminAction
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CollegiateBlue
import com.example.ui.theme.CollegiateNavy
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusEmeraldLight

@Composable
fun AdminApprovalScreen(
    rules: AutoApprovalRules,
    pendingRequests: List<LeaveRequest>,
    isProcessing: Boolean,
    onUpdateRules: (AutoApprovalRules) -> Unit,
    onRunBatchEngine: (Context) -> Unit,
    onAction: (LeaveRequest, AdminAction, String, Context) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showRuleSettings by remember { mutableStateOf(false) }

    var minAttendance by remember(rules) { mutableDoubleStateOf(rules.minAttendanceRequired) }
    var maxDays by remember(rules) { mutableIntStateOf(rules.maxLeaveDaysAllowed) }
    var autoEnabled by remember(rules) { mutableStateOf(rules.isAutoApprovalEnabled) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_approval_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Automated Professor & Admin Portal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
                Text(
                    text = "Configure institutional rules and execute real-time automated approvals.",
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )
            }
        }

        // Automated Rule Engine Runner Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CollegiateNavy)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Automated Rule Engine",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Attendance >= ${rules.minAttendanceRequired}% • Max ${rules.maxLeaveDaysAllowed} Days",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = rules.isAutoApprovalEnabled,
                            onCheckedChange = {
                                onUpdateRules(rules.copy(isAutoApprovalEnabled = it))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onRunBatchEngine(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_batch_engine_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusEmerald),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Evaluating Policy Rules...")
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Automated Batch Approvals", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showRuleSettings = !showRuleSettings },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showRuleSettings) "Hide Rule Policy Settings" else "Configure Rule Policy", color = Color.White)
                    }
                }
            }
        }

        // Expanded Rule Settings Card
        if (showRuleSettings) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "APPROVAL POLICY CRITERIA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Minimum Attendance Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Minimum Attendance Threshold", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("${minAttendance.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CollegiateNavy)
                        }
                        Slider(
                            value = minAttendance.toFloat(),
                            onValueChange = { minAttendance = it.toDouble() },
                            valueRange = 60f..95f,
                            steps = 6
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Max Duration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Max Fast-Track Days", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("$maxDays days", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CollegiateNavy)
                        }
                        Slider(
                            value = maxDays.toFloat(),
                            onValueChange = { maxDays = it.toInt() },
                            valueRange = 1f..5f,
                            steps = 3
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onUpdateRules(
                                    rules.copy(
                                        minAttendanceRequired = minAttendance,
                                        maxLeaveDaysAllowed = maxDays,
                                        isAutoApprovalEnabled = autoEnabled
                                    )
                                )
                                showRuleSettings = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CollegiateNavy)
                        ) {
                            Text("Save Policy Rules")
                        }
                    }
                }
            }
        }

        // Pending Queue Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pending Applications (${pendingRequests.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
            }
        }

        if (pendingRequests.isEmpty()) {
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
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusEmerald,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "All applications reviewed & processed!",
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "No pending leave requests in administration queue.",
                            fontSize = 12.sp,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        } else {
            items(pendingRequests) { req ->
                AdminPendingRequestCard(
                    request = req,
                    onApprove = {
                        onAction(
                            req,
                            AdminAction.ADMIN_APPROVE_FINAL,
                            "Administration clearance signed. Gate Pass issued.",
                            context
                        )
                    },
                    onFacultyEndorse = {
                        onAction(
                            req,
                            AdminAction.FACULTY_ENDORSE,
                            "Faculty Advisor validated student request.",
                            context
                        )
                    },
                    onReject = {
                        onAction(
                            req,
                            AdminAction.REJECT_REQUEST,
                            "Declined by Professor: attendance or academic conflict.",
                            context
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AdminPendingRequestCard(
    request: LeaveRequest,
    onApprove: () -> Unit,
    onFacultyEndorse: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_req_${request.applicationNo}"),
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Text(
                text = "${request.department} • ${request.category.displayName}",
                fontSize = 12.sp,
                color = SlateTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"${request.reasonTitle}\"",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = CollegiateNavy
            )
            Text(
                text = request.detailedReason,
                fontSize = 12.sp,
                color = SlateTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dates: ${request.startDate} to ${request.endDate} (${request.durationDays} days)",
                fontSize = 11.sp,
                color = SlateTextMuted
            )

            if (request.proofFileName != null) {
                Text(
                    text = "📎 Attachment: ${request.proofFileName}",
                    fontSize = 11.sp,
                    color = CollegiateBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SlateCardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusEmerald)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve Pass", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onFacultyEndorse,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Endorse", fontSize = 12.sp)
                }

                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(0.9f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCrimson)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Decline", fontSize = 12.sp)
                }
            }
        }
    }
}
