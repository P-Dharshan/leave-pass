package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AutoApprovalRules
import com.example.data.model.LeaveCategory
import com.example.data.model.StudentProfile
import com.example.data.model.UserRole
import com.example.data.repository.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LeavePass", appName)
  }

  @Test
  fun `auto approval rules allow qualifying academic request`() {
    val student = StudentProfile(attendancePercentage = 88.0, totalLeavesThisSemester = 1)
    val rules = AutoApprovalRules()
    val (isEligible, _) = rules.evaluateEligibility(
      student = student,
      category = LeaveCategory.ACADEMIC_EVENT,
      durationDays = 2,
      hasProof = true
    )
    assertTrue("High attendance 2-day academic request should be auto-approved", isEligible)
  }

  @Test
  fun `student login with reg number and dob password succeeds`() {
    val authRepo = AuthRepository()
    val result = authRepo.login("CS22B1042", "15-08-2003")
    assertTrue(result is AuthRepository.AuthResult.Success)
    val user = (result as AuthRepository.AuthResult.Success).user
    assertEquals(UserRole.STUDENT, user.role)
    assertEquals("Alex Morgan", user.fullName)

    // Test compact DOB format without hyphens
    val compactResult = authRepo.login("CS22B1042", "15082003")
    assertTrue(compactResult is AuthRepository.AuthResult.Success)
  }

  @Test
  fun `class coordinator login succeeds with CC ID and dob or key`() {
    val authRepo = AuthRepository()
    val result = authRepo.login("CC-CSE-A", "12-04-1982")
    assertTrue(result is AuthRepository.AuthResult.Success)
    val user = (result as AuthRepository.AuthResult.Success).user
    assertEquals(UserRole.CLASS_COORDINATOR, user.role)
    assertEquals("Dr. Sarah Jenkins", user.fullName)
  }

  @Test
  fun `hod login succeeds with HOD ID and dob`() {
    val authRepo = AuthRepository()
    val result = authRepo.login("HOD-CSE", "05-09-1975")
    assertTrue(result is AuthRepository.AuthResult.Success)
    val user = (result as AuthRepository.AuthResult.Success).user
    assertEquals(UserRole.HEAD_OF_DEPARTMENT, user.role)
    assertEquals("Prof. Marcus Vance", user.fullName)
  }
}

