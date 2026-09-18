package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveRequest
import com.example.ui.theme.CollegiateBlue
import com.example.ui.theme.CollegiateNavy
import com.example.ui.theme.CollegiateNavyDark
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.StatusEmerald
import com.example.ui.theme.StatusEmeraldLight

@Composable
fun GatePassCard(
    request: LeaveRequest,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gate_pass_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.5.dp)
    ) {
        Column {
            // Header: Collegiate Navy with Official Security Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CollegiateNavy)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CAMPUS SECURITY GATE PASS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Official Exit & Re-Entry Clearance",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(StatusEmerald, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ACTIVE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Student & Leave Information
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STUDENT NAME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextMuted
                        )
                        Text(
                            text = request.studentName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${request.rollNumber} • ${request.department}",
                            fontSize = 12.sp,
                            color = SlateTextSecondary
                        )
                        Text(
                            text = request.hostelStatus,
                            fontSize = 11.sp,
                            color = CollegiateBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Canvas-rendered stylized QR Code Pattern
                    CanvasQrCode(
                        seed = request.gatePassCode.hashCode(),
                        modifier = Modifier
                            .size(70.dp)
                            .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SlateCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Date & Time validity
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "VALID FROM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextMuted
                        )
                        Text(
                            text = request.startDate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = request.sessionType,
                            fontSize = 11.sp,
                            color = SlateTextSecondary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "VALID UNTIL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextMuted
                        )
                        Text(
                            text = request.endDate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "${request.durationDays} day(s) duration",
                            fontSize = 11.sp,
                            color = SlateTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Barcode simulation on Canvas
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                        .padding(vertical = 8.dp)
                ) {
                    CanvasBarcode(
                        code = request.gatePassCode,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(30.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = request.gatePassCode,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CollegiateNavyDark,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Official Verification Stamp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StatusEmeraldLight, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = StatusEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (request.autoApproved) {
                            "Digitally signed by Collegiate Automated Permission Engine"
                        } else {
                            "Verified by Head of Department & Dean of Student Affairs"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = StatusEmerald
                    )
                }
            }
        }
    }
}

@Composable
private fun CanvasQrCode(
    seed: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val size = this.size.width
        val cols = 7
        val cellSize = size / cols

        // Draw corner finder patterns
        drawRect(Color.Black, Offset(0f, 0f), Size(cellSize * 2.5f, cellSize * 2.5f))
        drawRect(Color.White, Offset(cellSize * 0.5f, cellSize * 0.5f), Size(cellSize * 1.5f, cellSize * 1.5f))
        drawRect(Color.Black, Offset(cellSize * 0.8f, cellSize * 0.8f), Size(cellSize * 0.9f, cellSize * 0.9f))

        drawRect(Color.Black, Offset(size - cellSize * 2.5f, 0f), Size(cellSize * 2.5f, cellSize * 2.5f))
        drawRect(Color.White, Offset(size - cellSize * 2f, cellSize * 0.5f), Size(cellSize * 1.5f, cellSize * 1.5f))
        drawRect(Color.Black, Offset(size - cellSize * 1.7f, cellSize * 0.8f), Size(cellSize * 0.9f, cellSize * 0.9f))

        drawRect(Color.Black, Offset(0f, size - cellSize * 2.5f), Size(cellSize * 2.5f, cellSize * 2.5f))
        drawRect(Color.White, Offset(cellSize * 0.5f, size - cellSize * 2f), Size(cellSize * 1.5f, cellSize * 1.5f))
        drawRect(Color.Black, Offset(cellSize * 0.8f, size - cellSize * 1.7f), Size(cellSize * 0.9f, cellSize * 0.9f))

        // Draw pseudo-random data dots based on seed
        val random = java.util.Random(seed.toLong())
        for (row in 0 until cols) {
            for (col in 0 until cols) {
                // skip finder corners
                if ((row < 3 && col < 3) || (row < 3 && col >= cols - 3) || (row >= cols - 3 && col < 3)) {
                    continue
                }
                if (random.nextBoolean()) {
                    drawRect(
                        Color.Black,
                        Offset(col * cellSize + cellSize * 0.1f, row * cellSize + cellSize * 0.1f),
                        Size(cellSize * 0.8f, cellSize * 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CanvasBarcode(
    code: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val totalWidth = size.width
        val barCount = 42
        val unit = totalWidth / barCount
        val rand = java.util.Random(code.hashCode().toLong())

        var x = 0f
        while (x < totalWidth) {
            val barWidth = if (rand.nextBoolean()) unit * 1.5f else unit * 0.8f
            drawRect(
                color = Color.Black,
                topLeft = Offset(x, 0f),
                size = Size(barWidth.coerceAtMost(totalWidth - x), size.height)
            )
            x += barWidth + (unit * if (rand.nextBoolean()) 1.2f else 0.8f)
        }
    }
}
