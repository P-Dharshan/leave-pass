package com.example.data.model

data class AutoApprovalRules(
    val isAutoApprovalEnabled: Boolean = true,
    val minAttendanceRequired: Double = 75.0,
    val maxLeaveDaysAllowed: Int = 2,
    val maxTotalSemesterLeaves: Int = 5,
    val allowAcademicHackathon: Boolean = true,
    val allowSingleDayMedical: Boolean = true,
    val allowWeekendHomePass: Boolean = true,
    val requireProofForMedical: Boolean = true
) {
    fun evaluateEligibility(
        student: StudentProfile,
        category: LeaveCategory,
        durationDays: Int,
        hasProof: Boolean
    ): Pair<Boolean, String> {
        if (!isAutoApprovalEnabled) {
            return Pair(false, "Automated approvals are currently paused by administration.")
        }
        if (student.attendancePercentage < minAttendanceRequired) {
            return Pair(
                false,
                "Attendance (${student.attendancePercentage}%) is below the minimum ${minAttendanceRequired}% threshold for auto-approval. Manual professor review required."
            )
        }
        if (durationDays > maxLeaveDaysAllowed) {
            return Pair(
                false,
                "Duration ($durationDays days) exceeds the maximum auto-approval limit ($maxLeaveDaysAllowed days). Forwarded to Department HOD."
            )
        }
        if (student.totalLeavesThisSemester >= maxTotalSemesterLeaves) {
            return Pair(
                false,
                "Semester leave allowance cap reached (${student.totalLeavesThisSemester}/$maxTotalSemesterLeaves taken). Requires Dean of Student Affairs approval."
            )
        }

        when (category) {
            LeaveCategory.ACADEMIC_EVENT -> {
                if (!allowAcademicHackathon) {
                    return Pair(false, "Academic leave requires faculty advisor letter endorsement.")
                }
            }
            LeaveCategory.MEDICAL -> {
                if (!allowSingleDayMedical) {
                    return Pair(false, "Medical leave requires Health Center officer verification.")
                }
                if (requireProofForMedical && !hasProof) {
                    return Pair(false, "Medical prescription/doctor's note attachment is mandatory for fast-track auto-approval.")
                }
            }
            LeaveCategory.WEEKEND_HOME_PASS -> {
                if (!allowWeekendHomePass) {
                    return Pair(false, "Hostel warden sign-off required for weekend passes.")
                }
            }
            LeaveCategory.EMERGENCY -> {
                return Pair(false, "Emergency leaves require faculty confirmation with parent contact.")
            }
            LeaveCategory.ON_DUTY -> {
                return Pair(false, "On-Duty leaves require athletic/cultural department coordinator sign-off.")
            }
        }

        return Pair(
            true,
            "Criteria verified: Attendance (${student.attendancePercentage}%) >= ${minAttendanceRequired}%, duration ($durationDays day) within policy, valid student standing."
        )
    }
}
