package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AutoApprovalRules
import com.example.data.model.LeaveCategory
import com.example.data.model.StudentProfile
import com.example.ui.theme.CollegiateBlue
import com.example.ui.theme.CollegiateNavy
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusEmeraldLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RequestEditorScreen(
    student: StudentProfile,
    rules: AutoApprovalRules,
    onSubmitRequest: (
        category: LeaveCategory,
        startDate: String,
        endDate: String,
        durationDays: Int,
        sessionType: String,
        reasonTitle: String,
        detailedReason: String,
        parentContact: String,
        studentContact: String,
        proofFileName: String?,
        context: Context
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf(LeaveCategory.ACADEMIC_EVENT) }
    var startDateText by remember { mutableStateOf(getFormattedDate(1)) }
    var endDateText by remember { mutableStateOf(getFormattedDate(2)) }
    var durationDays by remember { mutableIntStateOf(2) }
    var sessionType by remember { mutableStateOf("Full Day") }

    var reasonTitle by remember { mutableStateOf("Participation in Collegiate Hackathon") }
    var detailedReason by remember {
        mutableStateOf(
            "I request permission to be absent from college lectures to participate as an officially shortlisted finalist in the Hackathon. I will complete all make-up lab assignments promptly."
        )
    }
    var parentContact by remember { mutableStateOf(student.parentPhone) }
    var studentContact by remember { mutableStateOf(student.emergencyPhone) }
    var attachedProof by remember { mutableStateOf<String?>("hackathon_acceptance_letter.pdf") }

    // Live Auto-Approval Evaluation
    val autoApprovalCheck by remember(selectedCategory, durationDays, attachedProof, rules) {
        derivedStateOf {
            rules.evaluateEligibility(
                student = student,
                category = selectedCategory,
                durationDays = durationDays,
                hasProof = !attachedProof.isNullOrBlank()
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("request_editor_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "Leave Permission Editor",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
                Text(
                    text = "Fill in your leave details. Qualifying requests are instantly auto-approved.",
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )
            }
        }

        // Live Auto-Approval Status Banner
        item {
            val (isEligible, reasonNote) = autoApprovalCheck
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEligible) StatusEmeraldLight else Color(0xFFFEF3C7)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isEligible) StatusEmerald.copy(alpha = 0.4f) else StatusAmber.copy(alpha = 0.4f)
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isEligible) Icons.Default.Bolt else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isEligible) StatusEmerald else StatusAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEligible) "⚡ Auto-Approval Eligible" else "Manual Faculty Review Required",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isEligible) StatusEmerald else StatusAmber
                        )
                        Text(
                            text = reasonNote,
                            fontSize = 11.sp,
                            color = SlateTextPrimary.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Quick Preset Templates
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Quick Presets",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PresetChip(label = "Hackathon Finals (2 Days)") {
                            selectedCategory = LeaveCategory.ACADEMIC_EVENT
                            startDateText = getFormattedDate(1)
                            endDateText = getFormattedDate(2)
                            durationDays = 2
                            reasonTitle = "Collegiate Hackathon Finals"
                            detailedReason = "Shortlisted for developer hackathon finals. Representing our college tech club."
                            attachedProof = "hackathon_invitation.pdf"
                        }
                        PresetChip(label = "Fever / Health Clinic (1 Day)") {
                            selectedCategory = LeaveCategory.MEDICAL
                            startDateText = getFormattedDate(0)
                            endDateText = getFormattedDate(0)
                            durationDays = 1
                            reasonTitle = "Severe Viral Fever & Medical Rest"
                            detailedReason = "Suffering from high fever and headache. Doctor advised 1 day rest in hostel room."
                            attachedProof = "clinic_prescription.pdf"
                        }
                        PresetChip(label = "Weekend Home Pass (2 Days)") {
                            selectedCategory = LeaveCategory.WEEKEND_HOME_PASS
                            startDateText = getFormattedDate(3)
                            endDateText = getFormattedDate(4)
                            durationDays = 2
                            reasonTitle = "Weekend Visit to Hometown"
                            detailedReason = "Visiting parents for the upcoming weekend. Will return before Sunday evening hostel roll call."
                            attachedProof = "parent_consent_sms.png"
                        }
                        PresetChip(label = "Sports / On-Duty Event (1 Day)") {
                            selectedCategory = LeaveCategory.ON_DUTY
                            startDateText = getFormattedDate(2)
                            endDateText = getFormattedDate(2)
                            durationDays = 1
                            reasonTitle = "Inter-College Tournament"
                            detailedReason = "Selected to represent university basketball team in tournament matches."
                            attachedProof = "athletics_roster.pdf"
                        }
                    }
                }
            }
        }

        // Section: Category Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "LEAVE CATEGORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LeaveCategory.values().forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category.displayName, fontSize = 12.sp) },
                                leadingIcon = if (selectedCategory == category) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CollegiateNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: Schedule & Duration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SCHEDULE & DURATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = startDateText,
                            onValueChange = { startDateText = it },
                            label = { Text("From Date") },
                            modifier = Modifier.weight(1f).testTag("input_start_date"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endDateText,
                            onValueChange = { endDateText = it },
                            label = { Text("To Date") },
                            modifier = Modifier.weight(1f).testTag("input_end_date"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = durationDays.toString(),
                            onValueChange = {
                                durationDays = it.toIntOrNull()?.coerceIn(1, 30) ?: 1
                            },
                            label = { Text("Duration (Days)") },
                            modifier = Modifier.weight(1f).testTag("input_duration_days"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        // Quick duration adjustment chips
                        Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(1, 2, 3, 5).forEach { d ->
                                FilterChip(
                                    selected = durationDays == d,
                                    onClick = {
                                        durationDays = d
                                        endDateText = getFormattedDate(d)
                                    },
                                    label = { Text("${d}d", fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Session Selector
                    Text(
                        text = "Session Type:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateTextSecondary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        listOf("Full Day", "Morning Half", "Afternoon Half").forEach { session ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { sessionType = session }
                            ) {
                                RadioButton(
                                    selected = sessionType == session,
                                    onClick = { sessionType = session }
                                )
                                Text(session, fontSize = 12.sp, color = SlateTextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Section: Reason & Letter Draft
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LEAVE REASON & LETTER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextMuted
                        )

                        // One-click formal letter generator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    detailedReason = generateCollegiateLetter(
                                        student = student,
                                        category = selectedCategory,
                                        startDate = startDateText,
                                        endDate = endDateText,
                                        days = durationDays,
                                        title = reasonTitle
                                    )
                                }
                                .background(CollegiateBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CollegiateBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Auto-Draft Formal Letter",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CollegiateBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = reasonTitle,
                        onValueChange = { reasonTitle = it },
                        label = { Text("Subject / Purpose") },
                        modifier = Modifier.fillMaxWidth().testTag("input_reason_title"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = detailedReason,
                        onValueChange = { detailedReason = it },
                        label = { Text("Detailed Explanation / Formal Request Letter") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("input_detailed_reason"),
                        maxLines = 6
                    )
                }
            }
        }

        // Section: Proof Attachment & Contacts
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PROOF DOCUMENT & EMERGENCY CONTACT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated document attachment pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                            .border(1.dp, SlateCardBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = CollegiateBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = attachedProof ?: "No document attached",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = SlateTextPrimary
                            )
                            Text(
                                text = "Supporting medical cert or event ticket increases auto-approval speed",
                                fontSize = 10.sp,
                                color = SlateTextMuted
                            )
                        }
                        if (attachedProof != null) {
                            IconButton(onClick = { attachedProof = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                            }
                        } else {
                            OutlinedButton(
                                onClick = { attachedProof = "verified_permission_proof.pdf" },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Attach PDF", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = parentContact,
                            onValueChange = { parentContact = it },
                            label = { Text("Parent Phone") },
                            modifier = Modifier.weight(1f).testTag("input_parent_contact"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = studentContact,
                            onValueChange = { studentContact = it },
                            label = { Text("Student Mobile") },
                            modifier = Modifier.weight(1f).testTag("input_student_contact"),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Primary Submit Action
        item {
            val (isEligible, _) = autoApprovalCheck
            Button(
                onClick = {
                    onSubmitRequest(
                        selectedCategory,
                        startDateText,
                        endDateText,
                        durationDays,
                        sessionType,
                        reasonTitle.ifBlank { "Permission Request" },
                        detailedReason.ifBlank { "General leave permission requested." },
                        parentContact,
                        studentContact,
                        attachedProof,
                        context
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_leave_request_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEligible) StatusEmerald else CollegiateNavy
                )
            ) {
                Icon(
                    imageVector = if (isEligible) Icons.Default.Bolt else Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEligible) "Submit & Fast-Track Auto-Approve" else "Submit for Faculty Review",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = SlateTextPrimary, fontWeight = FontWeight.Medium)
    }
}

private fun getFormattedDate(daysOffset: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, daysOffset)
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(cal.time)
}

private fun generateCollegiateLetter(
    student: StudentProfile,
    category: LeaveCategory,
    startDate: String,
    endDate: String,
    days: Int,
    title: String
): String {
    return """To:
The Faculty Advisor (${student.facultyAdvisor})
Department of ${student.department}

Respected Professor,

I am writing to formally request permission for leave of absence for $days day(s), from $startDate to $endDate. 

The purpose of this leave is: $title.

I assure you that I will be in contact with my class peers to catch up with all missed lecture notes and complete laboratory assignments immediately upon my return. Necessary supporting proof is attached.

Yours respectfully,
${student.fullName}
Roll No: ${student.rollNumber}
${student.batch}"""
}
