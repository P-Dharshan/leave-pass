package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType {
    AUTO_APPROVAL_SUCCESS,
    ADMIN_APPROVED,
    FACULTY_ENDORSED,
    ACTION_REQUIRED,
    REQUEST_REJECTED,
    SYSTEM_INFO
}

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val requestId: Long,
    val applicationNo: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
