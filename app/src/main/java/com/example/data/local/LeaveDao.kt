package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LeaveRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveDao {
    @Query("SELECT * FROM leave_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE id = :id LIMIT 1")
    fun getRequestById(id: Long): Flow<LeaveRequest?>

    @Query("SELECT * FROM leave_requests WHERE status IN ('PENDING_REVIEW', 'FACULTY_APPROVED', 'ADMIN_IN_PROGRESS') ORDER BY createdAt DESC")
    fun getActiveRequests(): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests WHERE status IN ('AUTO_APPROVED', 'FINAL_APPROVED') ORDER BY updatedAt DESC")
    fun getApprovedRequests(): Flow<List<LeaveRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: LeaveRequest): Long

    @Update
    suspend fun updateRequest(request: LeaveRequest)

    @Delete
    suspend fun deleteRequest(request: LeaveRequest)

    @Query("DELETE FROM leave_requests WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM leave_requests")
    suspend fun getCount(): Int
}
