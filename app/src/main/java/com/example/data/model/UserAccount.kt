package com.example.data.model

enum class UserRole(val displayName: String, val badgeTitle: String) {
    STUDENT("Student", "Student Portal"),
    CLASS_COORDINATOR("Class Coordinator (CC)", "Faculty / CC Portal"),
    HEAD_OF_DEPARTMENT("Head of Department (HOD)", "HOD Administration")
}

data class UserAccount(
    val id: String,
    val regNumber: String,
    val dateOfBirth: String, // e.g. "15-08-2003"
    val alternateDobFormats: List<String> = emptyList(),
    val role: UserRole,
    val fullName: String,
    val department: String,
    val email: String,
    val studentProfile: StudentProfile? = null
) {
    fun matchesPassword(enteredPassword: String): Boolean {
        val cleanInput = enteredPassword.trim().replace("/", "").replace("-", "")
        val cleanDob = dateOfBirth.trim().replace("/", "").replace("-", "")

        if (cleanInput.equals(cleanDob, ignoreCase = true)) return true
        if (enteredPassword.trim().equals(dateOfBirth.trim(), ignoreCase = true)) return true

        return alternateDobFormats.any { alt ->
            alt.equals(enteredPassword.trim(), ignoreCase = true) ||
                    alt.replace("/", "").replace("-", "").equals(cleanInput, ignoreCase = true)
        }
    }
}
