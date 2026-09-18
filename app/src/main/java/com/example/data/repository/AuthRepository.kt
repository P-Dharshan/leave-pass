package com.example.data.repository

import com.example.data.model.StudentProfile
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {

    private val demoUsers: MutableList<UserAccount> = mutableListOf(
        UserAccount(
            id = "STU-1042",
            regNumber = "CS22B1042",
            dateOfBirth = "15-08-2003",
            alternateDobFormats = listOf("15082003", "15/08/2003", "2003-08-15"),
            role = UserRole.STUDENT,
            fullName = "Alex Morgan",
            department = "Computer Science & Engineering",
            email = "alex.morgan@college.edu",
            studentProfile = StudentProfile(
                id = "STU-2026-1042",
                fullName = "Alex Morgan",
                rollNumber = "CS22B1042",
                department = "Computer Science & Engineering",
                batch = "Class of 2026 (Sem VI)",
                attendancePercentage = 87.5,
                totalLeavesThisSemester = 2,
                hostelRoom = "Block C - Room 304 (Hostel Resident)",
                emergencyPhone = "+1 (555) 234-5678",
                parentPhone = "+1 (555) 987-6543",
                facultyAdvisor = "Dr. Sarah Jenkins (Assoc. Prof)",
                hodName = "Prof. Marcus Vance (Dept Head)"
            )
        ),
        UserAccount(
            id = "STU-1088",
            regNumber = "CS22B1088",
            dateOfBirth = "22-04-2004",
            alternateDobFormats = listOf("22042004", "22/04/2004", "2004-04-22"),
            role = UserRole.STUDENT,
            fullName = "Priya Sharma",
            department = "Computer Science & Engineering",
            email = "priya.sharma@college.edu",
            studentProfile = StudentProfile(
                id = "STU-2026-1088",
                fullName = "Priya Sharma",
                rollNumber = "CS22B1088",
                department = "Computer Science & Engineering",
                batch = "Class of 2026 (Sem VI)",
                attendancePercentage = 71.5,
                totalLeavesThisSemester = 5,
                hostelRoom = "Block A - Room 108 (Hostel Resident)",
                emergencyPhone = "+1 (555) 456-7890",
                parentPhone = "+1 (555) 321-6549",
                facultyAdvisor = "Dr. Sarah Jenkins (Assoc. Prof)",
                hodName = "Prof. Marcus Vance (Dept Head)"
            )
        ),
        UserAccount(
            id = "STU-2015",
            regNumber = "EC22B2015",
            dateOfBirth = "10-11-2003",
            alternateDobFormats = listOf("10112003", "10/11/2003", "2003-11-10"),
            role = UserRole.STUDENT,
            fullName = "David Chen",
            department = "Electronics & Communication",
            email = "david.chen@college.edu",
            studentProfile = StudentProfile(
                id = "STU-2026-2015",
                fullName = "David Chen",
                rollNumber = "EC22B2015",
                department = "Electronics & Communication",
                batch = "Class of 2026 (Sem VI)",
                attendancePercentage = 92.0,
                totalLeavesThisSemester = 1,
                hostelRoom = "Block B - Room 212 (Hostel Resident)",
                emergencyPhone = "+1 (555) 678-1234",
                parentPhone = "+1 (555) 876-5432",
                facultyAdvisor = "Dr. Alan Turing (Assoc. Prof)",
                hodName = "Prof. Marcus Vance (Dept Head)"
            )
        ),
        UserAccount(
            id = "FAC-CC-CSE",
            regNumber = "CC-CSE-A",
            dateOfBirth = "12-04-1982",
            alternateDobFormats = listOf("12041982", "12/04/1982", "cc123", "password123"),
            role = UserRole.CLASS_COORDINATOR,
            fullName = "Dr. Sarah Jenkins",
            department = "Computer Science & Engineering",
            email = "s.jenkins@college.edu"
        ),
        UserAccount(
            id = "FAC-HOD-CSE",
            regNumber = "HOD-CSE",
            dateOfBirth = "05-09-1975",
            alternateDobFormats = listOf("05091975", "05/09/1975", "hod123", "password123"),
            role = UserRole.HEAD_OF_DEPARTMENT,
            fullName = "Prof. Marcus Vance",
            department = "Computer Science & Engineering",
            email = "m.vance@college.edu"
        )
    )

    private val _currentUser = MutableStateFlow<UserAccount?>(demoUsers.first()) // default logged in as Alex Morgan
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    fun getAllDemoAccounts(): List<UserAccount> = demoUsers.toList()

    sealed class AuthResult {
        data class Success(val user: UserAccount) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    fun login(regNumber: String, passwordDob: String): AuthResult {
        val trimmedReg = regNumber.trim()
        val trimmedPass = passwordDob.trim()

        if (trimmedReg.isBlank()) {
            return AuthResult.Error("Please enter your Registration Number / ID.")
        }
        if (trimmedPass.isBlank()) {
            return AuthResult.Error("Please enter your Date of Birth / Password.")
        }

        val matchedUser = demoUsers.firstOrNull { it.regNumber.equals(trimmedReg, ignoreCase = true) }

        if (matchedUser != null) {
            if (matchedUser.matchesPassword(trimmedPass)) {
                _currentUser.value = matchedUser
                return AuthResult.Success(matchedUser)
            } else {
                return AuthResult.Error("Invalid Date of Birth / password for ${matchedUser.regNumber}. Expected DD-MM-YYYY (e.g. ${matchedUser.dateOfBirth})")
            }
        }

        // If not in demo list, check if user is entering a student reg format like "XX00X0000"
        // Dynamically register new student profile for seamless testing!
        val newStudent = UserAccount(
            id = "STU-${System.currentTimeMillis()}",
            regNumber = trimmedReg.uppercase(),
            dateOfBirth = trimmedPass,
            alternateDobFormats = listOf(trimmedPass.replace("-", "").replace("/", "")),
            role = UserRole.STUDENT,
            fullName = "Student ${trimmedReg.uppercase()}",
            department = "Computer Science & Engineering",
            email = "${trimmedReg.lowercase()}@college.edu",
            studentProfile = StudentProfile(
                id = "STU-GEN-${trimmedReg.uppercase()}",
                fullName = "Student ${trimmedReg.uppercase()}",
                rollNumber = trimmedReg.uppercase(),
                department = "Computer Science & Engineering",
                batch = "Class of 2026 (Sem VI)",
                attendancePercentage = 84.0,
                totalLeavesThisSemester = 0,
                hostelRoom = "Hostel Block C (Resident)",
                emergencyPhone = "+1 (555) 000-1111",
                parentPhone = "+1 (555) 222-3333",
                facultyAdvisor = "Dr. Sarah Jenkins (Assoc. Prof)",
                hodName = "Prof. Marcus Vance (Dept Head)"
            )
        )
        demoUsers.add(newStudent)
        _currentUser.value = newStudent
        return AuthResult.Success(newStudent)
    }

    fun quickSwitchAccount(user: UserAccount) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }
}
