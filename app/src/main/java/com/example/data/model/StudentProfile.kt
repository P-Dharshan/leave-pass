package com.example.data.model

data class StudentProfile(
    val id: String = "STU-2026-1042",
    val fullName: String = "Alex Morgan",
    val rollNumber: String = "CS22B1042",
    val department: String = "Computer Science & Engineering",
    val batch: String = "Class of 2026 (Sem VI)",
    val attendancePercentage: Double = 87.5,
    val totalLeavesThisSemester: Int = 2,
    val hostelRoom: String = "Block C - Room 304 (Hostel Resident)",
    val emergencyPhone: String = "+1 (555) 234-5678",
    val parentPhone: String = "+1 (555) 987-6543",
    val facultyAdvisor: String = "Dr. Sarah Jenkins (Assoc. Prof)",
    val hodName: String = "Prof. Marcus Vance (Dept Head)"
)
