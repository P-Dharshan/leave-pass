package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveRequest
import com.example.data.model.LeaveStatus
import com.example.ui.theme.CollegiateNavy
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusPurple
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StepTrackerView(
    request: LeaveRequest,
    modifier: Modifier = Modifier
) {
    val isAutoApproved = request.autoApproved || request.status == LeaveStatus.AUTO_APPROVED
    val isRejected = request.status == LeaveStatus.REJECTED
    val isFinalApproved = request.status == LeaveStatus.FINAL_APPROVED
    val isFacultyApproved = request.status == LeaveStatus.FACULTY_APPROVED

    // Pulse animation for active pending stage
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Real-Time Approval Pipeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                if (isAutoApproved) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(StatusEmerald.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            tint = StatusEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Fast-Track Engine",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusEmerald
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1: Submission
            TrackerStepItem(
                stepNumber = 1,
                title = "Application Submitted",
                subtitle = "Logged by ${request.studentName} (${request.rollNumber})",
                timestamp = formatTimestamp(request.createdAt),
                isCompleted = true,
                isActive = false,
                isFailed = false,
                isLast = false
            )

            // Step 2: Faculty Advisor / Rule Engine
            val step2Completed = isAutoApproved || isFacultyApproved || isFinalApproved
            val step2Active = request.status == LeaveStatus.PENDING_REVIEW && !isRejected
            val step2Failed = isRejected && !step2Completed

            TrackerStepItem(
                stepNumber = 2,
                title = if (isAutoApproved) "Automated Policy Engine" else "Faculty Advisor Verification",
                subtitle = if (isAutoApproved) {
                    request.autoApprovalNote ?: "Verified attendance & duration against institutional rules"
                } else {
                    request.facultyAdvisor
                },
                timestamp = if (step2Completed) formatTimestamp(request.updatedAt) else "In Queue",
                isCompleted = step2Completed,
                isActive = step2Active,
                isFailed = step2Failed,
                isLast = false,
                pulseAlpha = if (step2Active) pulseAlpha else 1f
            )

            // Step 3: Department HOD Review
            val step3Completed = isAutoApproved || isFinalApproved
            val step3Active = isFacultyApproved && !isRejected
            val step3Failed = isRejected && isFacultyApproved

            TrackerStepItem(
                stepNumber = 3,
                title = "Department Head (HOD) Review",
                subtitle = if (isAutoApproved) "Fast-Track Auto Cleared" else "Academic & Attendance Clearance",
                timestamp = if (step3Completed) formatTimestamp(request.updatedAt) else if (step3Active) "Pending Sign-off" else "Waiting for Step 2",
                isCompleted = step3Completed,
                isActive = step3Active,
                isFailed = step3Failed,
                isLast = false,
                pulseAlpha = if (step3Active) pulseAlpha else 1f
            )

            // Step 4: Administration & Security Gate Pass
            val step4Completed = isAutoApproved || isFinalApproved
            val step4Active = (request.status == LeaveStatus.ADMIN_IN_PROGRESS)
            val step4Failed = isRejected && !step4Completed

            TrackerStepItem(
                stepNumber = 4,
                title = "Administration & Gate Pass",
                subtitle = if (step4Completed) {
                    "Official Gate Pass Issued: ${request.gatePassCode}"
                } else if (isRejected) {
                    request.reviewerRemarks ?: "Declined by Administration"
                } else {
                    "Security clearance & campus hostel release"
                },
                timestamp = if (step4Completed) formatTimestamp(request.updatedAt) else if (step4Failed) "Declined" else "Awaiting clearance",
                isCompleted = step4Completed,
                isActive = step4Active,
                isFailed = step4Failed,
                isLast = true,
                pulseAlpha = if (step4Active) pulseAlpha else 1f
            )
        }
    }
}

@Composable
private fun TrackerStepItem(
    stepNumber: Int,
    title: String,
    subtitle: String,
    timestamp: String,
    isCompleted: Boolean,
    isActive: Boolean,
    isFailed: Boolean,
    isLast: Boolean,
    pulseAlpha: Float = 1f
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Step indicator & vertical connector line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> StatusEmerald
                            isFailed -> StatusCrimson
                            isActive -> StatusAmber
                            else -> SlateCardBorder
                        }
                    )
                    .then(
                        if (isActive) Modifier.alpha(pulseAlpha) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    isFailed -> Icon(
                        Icons.Default.Close,
                        contentDescription = "Failed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    isActive -> Icon(
                        Icons.Default.HourglassBottom,
                        contentDescription = "In Progress",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    else -> Text(
                        text = "$stepNumber",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                        .background(
                            if (isCompleted) StatusEmerald.copy(alpha = 0.6f)
                            else SlateCardBorder
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Step text details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (!isLast) 12.dp else 0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontWeight = if (isActive || isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp,
                    color = when {
                        isFailed -> StatusCrimson
                        isActive -> CollegiateNavy
                        isCompleted -> SlateTextPrimary
                        else -> SlateTextSecondary
                    }
                )
            }
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = SlateTextSecondary,
                lineHeight = 16.sp
            )
            Text(
                text = timestamp,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) StatusAmber else SlateTextMuted
            )
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
}
