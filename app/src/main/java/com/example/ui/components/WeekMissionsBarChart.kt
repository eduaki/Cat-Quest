package com.example.ui.components

import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.CatPinkLight
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
                        Text(
                            text = "Progresso da Semana 📊",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CatBrownText
                        )
                        Text(
                            text = "Missões diárias concluídas com o $catName",
                            fontSize = 12.sp,
                            color = CatBrownMuted
                        )
                    }
                }

                // D3 / Recharts Mode toggle badge
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
                            text = if (showD3WebViewMode) "D3.js SVG" else "Recharts Style",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showD3WebViewMode) Color.White else CatPeachPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (showD3WebViewMode) {
                // Interactive D3.js SVG Chart Rendered inside WebView
                D3BarChartWebView(
                    weekData = weekData,
                    catName = catName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                // Native Jetpack Compose Bar Chart with Recharts/D3 Design
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

            // Interactive Tooltip Card for Selected Day
            if (weekData.isNotEmpty() && selectedDayIndex in weekData.indices) {
                val selectedDay = weekData[selectedDayIndex]
                SelectedDayTooltipCard(
                    day = selectedDay,
                    catName = catName
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryStatBox(
                    label = "Total na Semana",
                    value = "$totalCompleted ✨",
                    color = CatPeachPrimary,
                    modifier = Modifier.weight(1f)
                )
                SummaryStatBox(
                    label = "Melhor Dia",
                    value = "${bestDay?.dayLabel ?: "Seg"} (${bestDay?.completedCount ?: 0})",
                    color = CatMint,
                    modifier = Modifier.weight(1f)
                )
                SummaryStatBox(
                    label = "Meta Mínima",
                    value = "3 / dia 🎯",
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
        // Goal Guide line (Target = 3)
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

        // Main Bars Row
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
                    // Count number above bar
                    Text(
                        text = "${day.completedCount}",
                        fontSize = if (isSelected) 12.sp else 10.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isSelected) CatPeachPrimary else CatBrownMuted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Bar Pillar
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
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) CatPeachPrimary else Color.Transparent,
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Day of Week Label
                    Text(
                        text = day.dayLabel,
                        fontSize = 11.sp,
                        fontWeight = if (day.isToday) FontWeight.Black else if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (day.isToday) CatPeachPrimary else if (isSelected) CatBrownText else CatBrownMuted
                    )

                    // Day number with Today marker
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (day.completedCount >= 3) "🏆" else if (day.completedCount > 0) "🐾" else "💤",
                    fontSize = 20.sp
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
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = color
            )
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
                body {
                    margin: 0;
                    padding: 8px;
                    background-color: #ffffff;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                    overflow: hidden;
                    box-sizing: border-box;
                }
                .bar {
                    fill: url(#peachGradient);
                    rx: 6px;
                    ry: 6px;
                    transition: all 0.3s ease;
                    cursor: pointer;
                }
                .bar:hover, .bar.selected {
                    fill: url(#pinkGradient);
                }
                .goal-line {
                    stroke: #FF70A6;
                    stroke-width: 1.5;
                    stroke-dasharray: 4, 3;
                }
                .axis-label {
                    fill: #8D7B68;
                    font-size: 11px;
                    text-anchor: middle;
                    font-weight: 500;
                }
                .axis-label.today {
                    fill: #FF926B;
                    font-weight: bold;
                }
                .val-label {
                    fill: #8D7B68;
                    font-size: 10px;
                    font-weight: bold;
                    text-anchor: middle;
                }
                .grid-line {
                    stroke: #F4EAE0;
                    stroke-width: 1;
                }
            </style>
        </head>
        <body>
            <svg id="chart" width="100%" height="180" viewBox="0 0 320 180">
                <defs>
                    <linearGradient id="peachGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                        <stop offset="0%" stop-color="#FF926B" />
                        <stop offset="100%" stop-color="#FFB396" />
                    </linearGradient>
                    <linearGradient id="pinkGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                        <stop offset="0%" stop-color="#FF70A6" />
                        <stop offset="100%" stop-color="#FF926B" />
                    </linearGradient>
                </defs>
            </svg>
            <script>
                (function() {
                    const labels = $labelsJson;
                    const values = $valuesJson;
                    const todayIdx = $todayIndex;
                    const maxVal = Math.max(4, ...values);
                    
                    const svg = document.getElementById('chart');
                    const w = 320;
                    const h = 180;
                    const padding = { top: 20, right: 15, bottom: 30, left: 25 };
                    const chartW = w - padding.left - padding.right;
                    const chartH = h - padding.top - padding.bottom;
                    
                    const barW = chartW / labels.length * 0.55;
                    const step = chartW / labels.length;
                    
                    // Gridlines & Goal (target 3)
                    const goalY = padding.top + chartH - (3 / maxVal) * chartH;
                    const goalLine = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                    goalLine.setAttribute('x1', padding.left);
                    goalLine.setAttribute('y1', goalY);
                    goalLine.setAttribute('x2', w - padding.right);
                    goalLine.setAttribute('y2', goalY);
                    goalLine.setAttribute('class', 'goal-line');
                    svg.appendChild(goalLine);

                    // Goal text
                    const goalTxt = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                    goalTxt.setAttribute('x', padding.left);
                    goalTxt.setAttribute('y', goalY - 4);
                    goalTxt.setAttribute('fill', '#FF70A6');
                    goalTxt.setAttribute('font-size', '9');
                    goalTxt.setAttribute('font-weight', 'bold');
                    goalTxt.textContent = 'Meta (3)';
                    svg.appendChild(goalTxt);
                    
                    // Render Bars
                    labels.forEach((label, i) => {
                        const val = values[i];
                        const barH = (val / maxVal) * chartH;
                        const x = padding.left + i * step + (step - barW) / 2;
                        const y = padding.top + chartH - barH;
                        
                        // Bar rect
                        const rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
                        rect.setAttribute('x', x);
                        rect.setAttribute('y', Math.min(y, padding.top + chartH - 4));
                        rect.setAttribute('width', barW);
                        rect.setAttribute('height', Math.max(4, barH));
                        rect.setAttribute('class', i === todayIdx ? 'bar selected' : 'bar');
                        svg.appendChild(rect);
                        
                        // Value text
                        const valTxt = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                        valTxt.setAttribute('x', x + barW / 2);
                        valTxt.setAttribute('y', y - 4);
                        valTxt.setAttribute('class', 'val-label');
                        valTxt.textContent = val;
                        svg.appendChild(valTxt);
                        
                        // Label text
                        const lblTxt = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                        lblTxt.setAttribute('x', x + barW / 2);
                        lblTxt.setAttribute('y', h - 10);
                        lblTxt.setAttribute('class', i === todayIdx ? 'axis-label today' : 'axis-label');
                        lblTxt.textContent = label;
                        svg.appendChild(lblTxt);
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
                setBackgroundColor(0)
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}
