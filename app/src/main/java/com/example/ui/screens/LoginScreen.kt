package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.theme.CollegiateBlue
import com.example.ui.theme.CollegiateNavy
import com.example.ui.theme.CollegiateNavyDark
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusCrimson
import com.example.ui.theme.StatusEmerald

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    demoAccounts: List<UserAccount>,
    onLogin: (regNumber: String, passwordDob: String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    var selectedRoleIndex by remember { mutableIntStateOf(0) }
    val roles = listOf(UserRole.STUDENT, UserRole.CLASS_COORDINATOR, UserRole.HEAD_OF_DEPARTMENT)
    val activeRole = roles[selectedRoleIndex]

    var regNumber by remember { mutableStateOf("CS22B1042") }
    var passwordDob by remember { mutableStateOf("15-08-2003") }
    var passwordVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Collegiate Crest Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CollegiateNavy)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(CollegiateNavy, CollegiateNavyDark)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "COLLEGE LEAVE PORTAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = "Authentication & Gate Pass",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Login with Registration Number and Date of Birth",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        // Role Tabs
        item {
            TabRow(
                selectedTabIndex = selectedRoleIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
            ) {
                roles.forEachIndexed { index, role ->
                    Tab(
                        selected = selectedRoleIndex == index,
                        onClick = {
                            selectedRoleIndex = index
                            // Pre-fill matching role demo
                            val defaultForRole = demoAccounts.firstOrNull { it.role == role }
                            if (defaultForRole != null) {
                                regNumber = defaultForRole.regNumber
                                passwordDob = defaultForRole.dateOfBirth
                            }
                        },
                        text = {
                            Text(
                                text = when (role) {
                                    UserRole.STUDENT -> "Student"
                                    UserRole.CLASS_COORDINATOR -> "CC (Advisor)"
                                    UserRole.HEAD_OF_DEPARTMENT -> "HOD"
                                },
                                fontSize = 12.sp,
                                fontWeight = if (selectedRoleIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }

        // Error Banner
        if (errorMessage != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(StatusCrimson.copy(alpha = 0.4f))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = StatusCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = StatusCrimson,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Credentials Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = when (activeRole) {
                            UserRole.STUDENT -> "STUDENT CREDENTIALS"
                            UserRole.CLASS_COORDINATOR -> "CLASS COORDINATOR CREDENTIALS"
                            UserRole.HEAD_OF_DEPARTMENT -> "HEAD OF DEPARTMENT (HOD) CREDENTIALS"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Field 1: Reg Number / ID
                    OutlinedTextField(
                        value = regNumber,
                        onValueChange = { regNumber = it },
                        label = {
                            Text(
                                when (activeRole) {
                                    UserRole.STUDENT -> "Student Registration / Roll Number"
                                    UserRole.CLASS_COORDINATOR -> "Coordinator ID (e.g. CC-CSE-A)"
                                    UserRole.HEAD_OF_DEPARTMENT -> "HOD ID (e.g. HOD-CSE)"
                                }
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = CollegiateBlue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_reg_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field 2: Password (Date of Birth)
                    OutlinedTextField(
                        value = passwordDob,
                        onValueChange = { passwordDob = it },
                        label = {
                            Text(
                                when (activeRole) {
                                    UserRole.STUDENT -> "Password (Date of Birth: DD-MM-YYYY)"
                                    UserRole.CLASS_COORDINATOR -> "Password / Date of Birth"
                                    UserRole.HEAD_OF_DEPARTMENT -> "Password / Date of Birth"
                                }
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = CollegiateBlue)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_dob_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onLogin(regNumber, passwordDob) }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (activeRole == UserRole.STUDENT) {
                            "Students: Enter your Date of Birth in DD-MM-YYYY (e.g. 15-08-2003) or DDMMYYYY format."
                        } else {
                            "Faculty & HOD: Enter your assigned faculty DOB (e.g. 12-04-1982) or coordinator password."
                        },
                        fontSize = 11.sp,
                        color = SlateTextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sign In Button
                    Button(
                        onClick = { onLogin(regNumber, passwordDob) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CollegiateNavy)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign In to ${activeRole.badgeTitle}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Quick Demo Accounts Carousel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ONE-TAP DEMO ACCOUNTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CollegiateBlue,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap any account to instantly fill credentials and sign in:",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        demoAccounts.forEach { account ->
                            DemoAccountChip(
                                account = account,
                                isSelected = regNumber == account.regNumber,
                                onClick = {
                                    regNumber = account.regNumber
                                    passwordDob = account.dateOfBirth
                                    selectedRoleIndex = when (account.role) {
                                        UserRole.STUDENT -> 0
                                        UserRole.CLASS_COORDINATOR -> 1
                                        UserRole.HEAD_OF_DEPARTMENT -> 2
                                    }
                                    // Instant sign-in on tap for maximum testing ease
                                    onLogin(account.regNumber, account.dateOfBirth)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoAccountChip(
    account: UserAccount,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val roleBadge = when (account.role) {
        UserRole.STUDENT -> "🎓 Student"
        UserRole.CLASS_COORDINATOR -> "👨‍🏫 CC"
        UserRole.HEAD_OF_DEPARTMENT -> "🏛️ HOD"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CollegiateNavy.copy(alpha = 0.1f) else Color.White)
            .border(
                1.dp,
                if (isSelected) CollegiateNavy else SlateCardBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("demo_chip_${account.regNumber}")
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = roleBadge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CollegiateNavy
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = account.fullName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
            }
            Text(
                text = "Reg: ${account.regNumber} • DOB: ${account.dateOfBirth}",
                fontSize = 10.sp,
                color = SlateTextMuted
            )
        }
    }
}
