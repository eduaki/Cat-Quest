package com.example.ui.components

import android.webkit.WebView
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.CatBrownMuted
import com.example.ui.theme.CatBrownText
import com.example.ui.theme.CatMint
import com.example.ui.theme.CatPeachLight
import com.example.ui.theme.CatPeachPrimary
import com.example.ui.theme.CatPink
import com.example.ui.theme.CatYellow
import com.example.ui.viewmodel.DayMissionsBarData

@Composable
fun WeekMissionsBarChart(
    weekData: List<DayMissionsBarData>,
    catName: String,
    modifier: Modifier = Modifier
) {
    var selectedDayIndex by remember {
        mutableStateOf(weekData.indexOfFirst { it.isToday }.takeIf { it >= 0 } ?: 0)
    }
    var showD3WebViewMode by remember { mutableStateOf(false) }

    val maxVal = remember(weekData) {
        val maxCompleted = weekData.maxOfOrNull { it.completedCount } ?: 3
        maxCompleted.coerceAtLeast(4)
    }

    val totalCompleted = remember(weekData) {
        weekData.sumOf { it.completedCount }
    }

    val bestDay = remember(weekData) {
        weekData.maxByOrNull { it.completedCount }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_missions_bar_chart"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header with Title & Mode Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CartoonPawPrint(size = 22.dp, color = CatPeachPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Progresso da Semana",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CatBrownText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = CatBrownText, modifier = Modifier.size(20.dp))
                        }
                        Text(
                            text = "Missões diárias concluídas com o $catName",
                            fontSize = 12.sp,
                            color = CatBrownMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (showD3WebViewMode) CatPeachPrimary else CatPeachLight.copy(alpha = 0.4f),
                    modifier = Modifier.clickable { showD3WebViewMode = !showD3WebViewMode }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showD3WebViewMode) "D3.js SVG" else "Native Chart",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showD3WebViewMode) Color.White else CatPeachPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (showD3WebViewMode) {
                D3BarChartWebView(
                    weekData = weekData,
                    catName = catName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                NativeComposeBarChart(
                    weekData = weekData,
                    maxVal = maxVal,
                    selectedIndex = selectedDayIndex,
                    onSelectIndex = { selectedDayIndex = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (weekData.isNotEmpty() && selectedDayIndex in weekData.indices) {
                val selectedDay = weekData[selectedDayIndex]
                SelectedDayTooltipCard(
                    day = selectedDay,
                    catName = catName
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryStatBox(
                    label = "Total na Semana",
                    value = "$totalCompleted",
                    icon = Icons.Default.Star,
                    color = CatPeachPrimary,
                    modifier = Modifier.weight(1f)
                )
                SummaryStatBox(
                    label = "Melhor Dia",
                    value = "${bestDay?.dayLabel ?: "Seg"} (${bestDay?.completedCount ?: 0})",
                    icon = Icons.Default.CheckCircle,
                    color = CatMint,
                    modifier = Modifier.weight(1f)
                )
                SummaryStatBox(
                    label = "Meta Mínima",
                    value = "3 / dia",
                    icon = Icons.Default.TrackChanges,
                    color = CatPink,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NativeComposeBarChart(
    weekData: List<DayMissionsBarData>,
    maxVal: Int,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Meta (3)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CatPink
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(CatPink.copy(alpha = 0.4f))
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            weekData.forEachIndexed { index, day ->
                val isSelected = index == selectedIndex
                val ratio = (day.completedCount.toFloat() / maxVal).coerceIn(0.04f, 1f)
                val animatedRatio by animateFloatAsState(
                    targetValue = ratio,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "bar_ratio_$index"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelectIndex(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "${day.completedCount}",
                        fontSize = if (isSelected) 12.sp else 10.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isSelected) CatPeachPrimary else CatBrownMuted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (isSelected) 0.62f else 0.52f)
                            .fillMaxHeight(animatedRatio)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(
                                        listOf(CatPeachPrimary, CatPink)
                                    )
                                } else if (day.completedCount >= 3) {
                                    Brush.verticalGradient(
                                        listOf(CatPeachLight, CatPeachPrimary.copy(alpha = 0.8f))
                                    )
                                } else if (day.completedCount > 0) {
                                    Brush.verticalGradient(
                                        listOf(CatYellow.copy(alpha = 0.6f), CatPeachLight)
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFFEEEEEE), Color(0xFFDDDDDD))
                                    )
                                }
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = day.dayLabel,
                        fontSize = 11.sp,
                        fontWeight = if (day.isToday) FontWeight.Black else if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (day.isToday) CatPeachPrimary else if (isSelected) CatBrownText else CatBrownMuted
                    )

                    Surface(
                        shape = CircleShape,
                        color = if (day.isToday) CatPeachPrimary else Color.Transparent,
                        modifier = Modifier.size(16.dp)
                    ) {
                        Text(
                            text = "${day.dayNumber}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (day.isToday) Color.White else CatBrownMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedDayTooltipCard(
    day: DayMissionsBarData,
    catName: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFF9F5),
        border = androidx.compose.foundation.BorderStroke(1.dp, CatPeachLight)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = when {
                        day.completedCount >= 3 -> Icons.Default.Star
                        day.completedCount > 0 -> Icons.Default.Pets
                        else -> Icons.Default.NightlightRound
                    },
                    contentDescription = null,
                    tint = if (day.completedCount >= 3) CatYellow else if (day.completedCount > 0) CatPeachPrimary else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "${day.dayLabel}, dia ${day.dayNumber} ${if (day.isToday) "(Hoje)" else ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CatBrownText
                    )
                    Text(
                        text = when {
                            day.completedCount >= 3 -> "Parabéns! Meta de cuidados atingida com louvor!"
                            day.completedCount > 0 -> "Ótimo progresso! O $catName agradece cada carinho."
                            else -> "Nenhuma missão registrada neste dia."
                        },
                        fontSize = 11.sp,
                        color = CatBrownMuted
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (day.completedCount >= 3) CatMint.copy(alpha = 0.2f) else CatPeachLight.copy(alpha = 0.4f)
            ) {
                Text(
                    text = "${day.completedCount} missões",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (day.completedCount >= 3) CatMint else CatPeachPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryStatBox(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFAF7F2)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = value,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = color
                )
            }
            Text(
                text = label,
                fontSize = 10.sp,
                color = CatBrownMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun D3BarChartWebView(
    weekData: List<DayMissionsBarData>,
    catName: String,
    modifier: Modifier = Modifier
) {
    val labelsJson = weekData.joinToString(prefix = "[", postfix = "]") { "\"${it.dayLabel}\"" }
    val valuesJson = weekData.joinToString(prefix = "[", postfix = "]") { "${it.completedCount}" }
    val todayIndex = weekData.indexOfFirst { it.isToday }.coerceAtLeast(0)

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <style>
                body { margin: 0; padding: 8px; background-color: #ffffff; font-family: sans-serif; overflow: hidden; }
                .bar { fill: #FF926B; rx: 6px; }
                .bar.selected { fill: #FF70A6; }
                .goal-line { stroke: #FF70A6; stroke-width: 1.5; stroke-dasharray: 4, 3; }
                .axis-label { fill: #8D7B68; font-size: 11px; text-anchor: middle; }
                .axis-label.today { fill: #FF926B; font-weight: bold; }
            </style>
        </head>
        <body>
            <svg id="chart" width="100%" height="180" viewBox="0 0 320 180"></svg>
            <script>
                (function() {
                    const labels = $labelsJson;
                    const values = $valuesJson;
                    const todayIdx = $todayIndex;
                    const maxVal = Math.max(4, ...values);
                    const svg = document.getElementById('chart');
                    const w = 320, h = 180, p = 25;
                    const cw = w - p*2, ch = h - p*2;
                    const bw = cw / labels.length * 0.6;
                    const step = cw / labels.length;
                    
                    values.forEach((v, i) => {
                        const bh = (v / maxVal) * ch;
                        const x = p + i * step + (step - bw) / 2;
                        const y = h - p - bh;
                        const r = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
                        r.setAttribute('x', x); r.setAttribute('y', y);
                        r.setAttribute('width', bw); r.setAttribute('height', bh);
                        r.setAttribute('class', i === todayIdx ? 'bar selected' : 'bar');
                        svg.appendChild(r);
                        const t = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                        t.setAttribute('x', x + bw/2); t.setAttribute('y', h - 5);
                        t.setAttribute('class', i === todayIdx ? 'axis-label today' : 'axis-label');
                        t.textContent = labels[i]; svg.appendChild(t);
                    });
                })();
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier
    )
}
