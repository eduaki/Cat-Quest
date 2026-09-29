package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CartoonCatFace
import com.example.ui.components.CartoonPawPrint
import com.example.ui.theme.CatBrownMuted
import com.example.ui.theme.CatBrownText
import com.example.ui.theme.CatMint
import com.example.ui.theme.CatPeachLight
import com.example.ui.theme.CatPeachPrimary
import com.example.ui.theme.CatPink
import com.example.ui.theme.CatPinkLight
import com.example.ui.theme.CatVanilla
import com.example.ui.theme.CatYellow
import com.example.ui.viewmodel.CatCareViewModel
import com.example.utils.CalendarDay
import com.example.utils.DateUtils

@Composable
fun CalendarScreen(
    viewModel: CatCareViewModel,
    onNavigateToCarousel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val completedDates by viewModel.datesWithCompletedMissions.collectAsStateWithLifecycle()
    val selectedDayMissions by viewModel.selectedDateMissions.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    val (year, month) = selectedYearMonth
    val monthName = DateUtils.formatMonthYear(year, month)
    val days = DateUtils.getDaysInMonth(year, month)
    val completedDatesSet = completedDates.toSet()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CatVanilla),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Month Navigation Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.previousMonth() },
                            modifier = Modifier.testTag("prev_month_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Mês Anterior",
                                tint = CatPeachPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = monthName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CatBrownText
                            )
                            Text(
                                text = "Calendário do ${profile.name} 🐾",
                                style = MaterialTheme.typography.bodySmall,
                                color = CatPeachPrimary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.nextMonth() },
                            modifier = Modifier.testTag("next_month_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Próximo Mês",
                                tint = CatPeachPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Days of week header
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val weekDays = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
                        weekDays.forEach { wDay ->
                            Text(
                                text = wDay,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (wDay == "Dom" || wDay == "Sáb") CatPink else CatBrownMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Grid (rendered inside non-nested Box/Column)
                    CalendarMonthGrid(
                        days = days,
                        selectedDate = selectedDate,
                        todayDate = viewModel.todayDate,
                        completedDates = completedDatesSet,
                        onDayClick = { dateStr ->
                            if (dateStr.isNotBlank()) {
                                viewModel.selectDate(dateStr)
                            }
                        }
                    )
                }
            }
        }

        // Summary bar & shortcut to monthly carousel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToCarousel() }
                    .testTag("open_recap_shortcut"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE0))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CartoonCatFace(
                        size = 46.dp,
                        skinType = profile.equippedSkin,
                        accessoryType = profile.equippedAccessory
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎵 Retrospectiva Musical de $monthName",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CatBrownText
                        )
                        Text(
                            text = "Toque para ver o carrossel de fotos com musiquinha!",
                            fontSize = 12.sp,
                            color = CatPeachPrimary
                        )
                    }
                    Text(text = "🐾 >", fontWeight = FontWeight.Bold, color = CatPeachPrimary)
                }
            }
        }

        // Selected Day Details Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CartoonPawPrint(size = 20.dp, color = CatPeachPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Missões de " + DateUtils.formatDayMonth(selectedDate),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CatBrownText
                )
            }
        }

        // Selected Day Missions
        items(selectedDayMissions, key = { it.id }) { mission ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = mission.getCategoryEnum().iconEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mission.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = CatBrownText
                        )
                        Text(
                            text = if (mission.isCompleted) "Comprovado com foto 🐾" else "Pendente",
                            fontSize = 11.sp,
                            color = if (mission.isCompleted) CatMint else CatPeachPrimary
                        )
                    }
                    if (mission.isCompleted) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CatMint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    days: List<CalendarDay>,
    selectedDate: String,
    todayDate: String,
    completedDates: Set<String>,
    onDayClick: (String) -> Unit
) {
    val rows = days.chunked(7)
    Column(modifier = Modifier.fillMaxWidth()) {
        rows.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day.isCurrentMonth) {
                            val isSelected = day.dateString == selectedDate
                            val isToday = day.dateString == todayDate
                            val hasCompleted = completedDates.contains(day.dateString)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isSelected -> CatPeachPrimary
                                            hasCompleted -> CatPinkLight.copy(alpha = 0.6f)
                                            isToday -> Color(0xFFFFECE0)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = if (isToday) 1.5.dp else 0.dp,
                                        color = if (isToday) CatPeachPrimary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onDayClick(day.dateString) }
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${day.dayNumber}",
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected || isToday || hasCompleted) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected -> Color.White
                                            hasCompleted -> CatBrownText
                                            isToday -> CatPeachPrimary
                                            else -> CatBrownText
                                        }
                                    )
                                    if (hasCompleted) {
                                        Text(
                                            text = "🐾",
                                            fontSize = 9.sp,
                                            lineHeight = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
