package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveStatus
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberLight
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusCrimsonLight
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusEmeraldLight
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusPurpleLight

@Composable
fun StatusBadge(
    status: LeaveStatus,
    autoApproved: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor, icon) = when {
        autoApproved || status == LeaveStatus.AUTO_APPROVED -> {
            Quadruple(
                StatusEmeraldLight,
                StatusEmerald,
                StatusEmerald.copy(alpha = 0.3f),
                Icons.Default.Bolt
            )
        }
        status == LeaveStatus.FINAL_APPROVED -> {
            Quadruple(
                StatusEmeraldLight,
                StatusEmerald,
                StatusEmerald.copy(alpha = 0.3f),
                Icons.Default.Verified
            )
        }
        status == LeaveStatus.FACULTY_APPROVED -> {
            Quadruple(
                StatusPurpleLight,
                StatusPurple,
                StatusPurple.copy(alpha = 0.3f),
                Icons.Default.CheckCircle
            )
        }
        status == LeaveStatus.PENDING_REVIEW || status == LeaveStatus.ADMIN_IN_PROGRESS -> {
            Quadruple(
                StatusAmberLight,
                StatusAmber,
                StatusAmber.copy(alpha = 0.3f),
                Icons.Default.HourglassTop
            )
        }
        status == LeaveStatus.REJECTED -> {
            Quadruple(
                StatusCrimsonLight,
                StatusCrimson,
                StatusCrimson.copy(alpha = 0.3f),
                Icons.Default.ErrorOutline
            )
        }
        else -> {
            Quadruple(
                StatusAmberLight,
                StatusAmber,
                StatusAmber.copy(alpha = 0.3f),
                Icons.Default.HourglassTop
            )
        }
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(100.dp))
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (autoApproved) "Auto-Approved" else status.displayName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
