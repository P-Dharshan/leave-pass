package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.LeaveCategory
import com.example.data.model.LeaveStatus
import com.example.data.model.NotificationType

class Converters {
    @TypeConverter
    fun fromLeaveCategory(category: LeaveCategory): String = category.name

    @TypeConverter
    fun toLeaveCategory(value: String): LeaveCategory = try {
        LeaveCategory.valueOf(value)
    } catch (e: Exception) {
        LeaveCategory.EMERGENCY
    }

    @TypeConverter
    fun fromLeaveStatus(status: LeaveStatus): String = status.name

    @TypeConverter
    fun toLeaveStatus(value: String): LeaveStatus = try {
        LeaveStatus.valueOf(value)
    } catch (e: Exception) {
        LeaveStatus.PENDING_REVIEW
    }

    @TypeConverter
    fun fromNotificationType(type: NotificationType): String = type.name

    @TypeConverter
    fun toNotificationType(value: String): NotificationType = try {
        NotificationType.valueOf(value)
    } catch (e: Exception) {
        NotificationType.SYSTEM_INFO
    }
}
