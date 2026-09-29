package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CatBrownText
import com.example.ui.theme.CatPeachPrimary
import com.example.ui.theme.CatPink
import com.example.ui.theme.CatPinkLight
import com.example.ui.theme.CatYellow

@Composable
fun CartoonCatFace(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    skinType: String = "WHITE",
    accessoryType: String = "NONE"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cat_animation")
    val earWiggle by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ear_wiggle"
    )

    val furColor = when (skinType) {
        "GINGER" -> Color(0xFFFFB74D)
        "BLACK" -> Color(0xFF37474F)
        "SIAMESE" -> Color(0xFFFFF8E7)
        "CALICO" -> Color(0xFFFFFDF8)
        else -> Color.White
    }

    val outlineColor = when (skinType) {
        "BLACK" -> Color(0xFF212121)
        else -> CatBrownText
    }

    val eyeColor = when (skinType) {
        "BLACK" -> Color(0xFFFFD54F) // Shiny golden cat eyes for black cat
        "SIAMESE" -> Color(0xFF29B6F6) // Sapphire blue eyes for Siamese
        else -> CatBrownText
    }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h * 0.55f

        // Ears with wiggle
        val leftEar = Path().apply {
            moveTo(cx - w * 0.35f, cy - h * 0.15f)
            lineTo(cx - w * 0.42f, cy - h * 0.48f + earWiggle * 2f)
            lineTo(cx - w * 0.12f, cy - h * 0.32f)
            close()
        }
        val rightEar = Path().apply {
            moveTo(cx + w * 0.35f, cy - h * 0.15f)
            lineTo(cx + w * 0.42f, cy - h * 0.48f - earWiggle * 2f)
            lineTo(cx + w * 0.12f, cy - h * 0.32f)
            close()
        }

        val earOuterColor = if (skinType == "SIAMESE") Color(0xFF5D4037) else furColor
        drawPath(leftEar, color = earOuterColor)
        drawPath(leftEar, color = outlineColor, style = Stroke(width = w * 0.035f))
        drawPath(rightEar, color = earOuterColor)
        drawPath(rightEar, color = outlineColor, style = Stroke(width = w * 0.035f))

        // Inner pink ears
        val leftInnerEar = Path().apply {
            moveTo(cx - w * 0.33f, cy - h * 0.18f)
            lineTo(cx - w * 0.38f, cy - h * 0.42f)
            lineTo(cx - w * 0.16f, cy - h * 0.30f)
            close()
        }
        val rightInnerEar = Path().apply {
            moveTo(cx + w * 0.33f, cy - h * 0.18f)
            lineTo(cx + w * 0.38f, cy - h * 0.42f)
            lineTo(cx + w * 0.16f, cy - h * 0.30f)
            close()
        }
        drawPath(leftInnerEar, color = CatPinkLight)
        drawPath(rightInnerEar, color = CatPinkLight)

        // Cat Head
        val headRadiusX = w * 0.44f
        val headRadiusY = h * 0.38f
        drawRoundRect(
            color = furColor,
            topLeft = Offset(cx - headRadiusX, cy - headRadiusY),
            size = Size(headRadiusX * 2, headRadiusY * 2),
            cornerRadius = CornerRadius(w * 0.4f, h * 0.35f)
        )

        // Special patterns for Siamese / Calico
        if (skinType == "SIAMESE") {
            // Siamese nose mask
            drawCircle(
                color = Color(0xFF5D4037),
                radius = w * 0.18f,
                center = Offset(cx, cy + h * 0.04f)
            )
        } else if (skinType == "CALICO") {
            // Calico orange spot
            drawCircle(color = Color(0xFFFF9800), radius = w * 0.14f, center = Offset(cx - w * 0.25f, cy - h * 0.18f))
            // Calico black spot
            drawCircle(color = Color(0xFF37474F), radius = w * 0.12f, center = Offset(cx + w * 0.22f, cy - h * 0.16f))
        }

        drawRoundRect(
            color = outlineColor,
            topLeft = Offset(cx - headRadiusX, cy - headRadiusY),
            size = Size(headRadiusX * 2, headRadiusY * 2),
            cornerRadius = CornerRadius(w * 0.4f, h * 0.35f),
            style = Stroke(width = w * 0.035f)
        )

        // Cute rosy cheeks
        drawCircle(
            color = CatPinkLight.copy(alpha = 0.8f),
            radius = w * 0.08f,
            center = Offset(cx - w * 0.26f, cy + h * 0.08f)
        )
        drawCircle(
            color = CatPinkLight.copy(alpha = 0.8f),
            radius = w * 0.08f,
            center = Offset(cx + w * 0.26f, cy + h * 0.08f)
        )

        // Eyes
        val eyeRadius = w * 0.05f
        if (accessoryType == "SUNGLASSES") {
            // Sunglasses
            val glassesY = cy - h * 0.04f
            drawRoundRect(
                color = Color(0xFF212121),
                topLeft = Offset(cx - w * 0.26f, glassesY - h * 0.04f),
                size = Size(w * 0.22f, h * 0.10f),
                cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
            )
            drawRoundRect(
                color = Color(0xFF212121),
                topLeft = Offset(cx + w * 0.04f, glassesY - h * 0.04f),
                size = Size(w * 0.22f, h * 0.10f),
                cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
            )
            // Bridge
            drawLine(
                color = Color(0xFF212121),
                start = Offset(cx - w * 0.05f, glassesY),
                end = Offset(cx + w * 0.05f, glassesY),
                strokeWidth = w * 0.03f
            )
        } else {
            drawCircle(color = eyeColor, radius = eyeRadius, center = Offset(cx - w * 0.15f, cy - h * 0.04f))
            drawCircle(color = eyeColor, radius = eyeRadius, center = Offset(cx + w * 0.15f, cy - h * 0.04f))

            // Eye shines
            drawCircle(color = Color.White, radius = eyeRadius * 0.4f, center = Offset(cx - w * 0.16f, cy - h * 0.06f))
            drawCircle(color = Color.White, radius = eyeRadius * 0.4f, center = Offset(cx + w * 0.14f, cy - h * 0.06f))
        }

        // Pink nose
        val nosePath = Path().apply {
            moveTo(cx, cy + h * 0.02f)
            lineTo(cx - w * 0.04f, cy + h * 0.06f)
            lineTo(cx + w * 0.04f, cy + h * 0.06f)
            close()
        }
        drawPath(nosePath, color = CatPink)

        // Mouth (w shape)
        val mouthY = cy + h * 0.06f
        val mouthColor = if (skinType == "BLACK") Color(0xFFEEEEEE) else outlineColor
        val mouthW = Path().apply {
            moveTo(cx, mouthY)
            quadraticTo(cx - w * 0.05f, mouthY + h * 0.06f, cx - w * 0.10f, mouthY + h * 0.02f)
            moveTo(cx, mouthY)
            quadraticTo(cx + w * 0.05f, mouthY + h * 0.06f, cx + w * 0.10f, mouthY + h * 0.02f)
        }
        drawPath(mouthW, color = mouthColor, style = Stroke(width = w * 0.025f, cap = StrokeCap.Round))

        // Whiskers
        val whiskerColor = if (skinType == "BLACK") Color(0xFFB0BEC5) else outlineColor.copy(alpha = 0.7f)
        drawLine(color = whiskerColor, start = Offset(cx - w * 0.24f, cy), end = Offset(cx - w * 0.42f, cy - h * 0.04f), strokeWidth = w * 0.02f, cap = StrokeCap.Round)
        drawLine(color = whiskerColor, start = Offset(cx - w * 0.24f, cy + h * 0.05f), end = Offset(cx - w * 0.44f, cy + h * 0.06f), strokeWidth = w * 0.02f, cap = StrokeCap.Round)
        drawLine(color = whiskerColor, start = Offset(cx + w * 0.24f, cy), end = Offset(cx + w * 0.42f, cy - h * 0.04f), strokeWidth = w * 0.02f, cap = StrokeCap.Round)
        drawLine(color = whiskerColor, start = Offset(cx + w * 0.24f, cy + h * 0.05f), end = Offset(cx + w * 0.44f, cy + h * 0.06f), strokeWidth = w * 0.02f, cap = StrokeCap.Round)

        // Accessories on top of head
        when (accessoryType) {
            "CROWN" -> {
                val crownPath = Path().apply {
                    moveTo(cx - w * 0.20f, cy - h * 0.35f)
                    lineTo(cx - w * 0.22f, cy - h * 0.52f)
                    lineTo(cx - w * 0.10f, cy - h * 0.42f)
                    lineTo(cx, cy - h * 0.56f)
                    lineTo(cx + w * 0.10f, cy - h * 0.42f)
                    lineTo(cx + w * 0.22f, cy - h * 0.52f)
                    lineTo(cx + w * 0.20f, cy - h * 0.35f)
                    close()
                }
                drawPath(crownPath, color = Color(0xFFFFD54F))
                drawPath(crownPath, color = outlineColor, style = Stroke(width = w * 0.025f))
                // Crown rubies
                drawCircle(color = Color(0xFFE91E63), radius = w * 0.025f, center = Offset(cx, cy - h * 0.44f))
            }
            "BOW" -> {
                // Kawaii pink bow near right ear
                val bowCx = cx + w * 0.24f
                val bowCy = cy - h * 0.32f
                val bowPath = Path().apply {
                    moveTo(bowCx, bowCy)
                    lineTo(bowCx - w * 0.12f, bowCy - h * 0.08f)
                    lineTo(bowCx - w * 0.12f, bowCy + h * 0.08f)
                    close()
                    moveTo(bowCx, bowCy)
                    lineTo(bowCx + w * 0.12f, bowCy - h * 0.08f)
                    lineTo(bowCx + w * 0.12f, bowCy + h * 0.08f)
                    close()
                }
                drawPath(bowPath, color = CatPink)
                drawCircle(color = Color(0xFFFF4081), radius = w * 0.04f, center = Offset(bowCx, bowCy))
            }
            "TOPHAT" -> {
                val hatBase = Path().apply {
                    moveTo(cx - w * 0.25f, cy - h * 0.36f)
                    lineTo(cx + w * 0.25f, cy - h * 0.36f)
                }
                drawLine(color = Color(0xFF263238), start = Offset(cx - w * 0.24f, cy - h * 0.35f), end = Offset(cx + w * 0.24f, cy - h * 0.35f), strokeWidth = w * 0.04f, cap = StrokeCap.Round)
                drawRoundRect(
                    color = Color(0xFF263238),
                    topLeft = Offset(cx - w * 0.16f, cy - h * 0.58f),
                    size = Size(w * 0.32f, h * 0.23f),
                    cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
                )
                // Red ribbon band
                drawRect(
                    color = Color(0xFFE53935),
                    topLeft = Offset(cx - w * 0.16f, cy - h * 0.41f),
                    size = Size(w * 0.32f, h * 0.05f)
                )
            }
            "CAP" -> {
                drawRoundRect(
                    color = Color(0xFF1E88E5),
                    topLeft = Offset(cx - w * 0.22f, cy - h * 0.48f),
                    size = Size(w * 0.44f, h * 0.16f),
                    cornerRadius = CornerRadius(w * 0.1f, w * 0.1f)
                )
                // Visor
                drawLine(
                    color = Color(0xFF1565C0),
                    start = Offset(cx - w * 0.25f, cy - h * 0.36f),
                    end = Offset(cx + w * 0.25f, cy - h * 0.36f),
                    strokeWidth = w * 0.04f,
                    cap = StrokeCap.Round
                )
            }
            "WITCH" -> {
                val witchHat = Path().apply {
                    moveTo(cx - w * 0.25f, cy - h * 0.34f)
                    lineTo(cx, cy - h * 0.62f)
                    lineTo(cx + w * 0.25f, cy - h * 0.34f)
                    close()
                }
                drawPath(witchHat, color = Color(0xFF5E35B1))
                drawLine(color = Color(0xFF4527A0), start = Offset(cx - w * 0.28f, cy - h * 0.34f), end = Offset(cx + w * 0.28f, cy - h * 0.34f), strokeWidth = w * 0.035f, cap = StrokeCap.Round)
                drawCircle(color = Color(0xFFFFD54F), radius = w * 0.035f, center = Offset(cx, cy - h * 0.42f))
            }
            "FLOWERS" -> {
                val flowerY = cy - h * 0.35f
                val flowerColors = listOf(Color(0xFFFF80AB), Color(0xFFFFD54F), Color(0xFF80DEEA))
                val offsets = listOf(-w * 0.15f, 0f, w * 0.15f)
                for (i in 0..2) {
                    drawCircle(color = flowerColors[i], radius = w * 0.05f, center = Offset(cx + offsets[i], flowerY))
                    drawCircle(color = Color.White, radius = w * 0.02f, center = Offset(cx + offsets[i], flowerY))
                }
            }
        }
    }
}

@Composable
fun CartoonPawPrint(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = CatPeachPrimary
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Main pad
        drawCircle(
            color = color,
            radius = w * 0.28f,
            center = Offset(w * 0.5f, h * 0.65f)
        )

        // Toe pads
        drawCircle(color = color, radius = w * 0.12f, center = Offset(w * 0.22f, h * 0.32f))
        drawCircle(color = color, radius = w * 0.14f, center = Offset(w * 0.40f, h * 0.20f))
        drawCircle(color = color, radius = w * 0.14f, center = Offset(w * 0.60f, h * 0.20f))
        drawCircle(color = color, radius = w * 0.12f, center = Offset(w * 0.78f, h * 0.32f))
    }
}

@Composable
fun FloatingMusicNotes(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "music_notes")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "notes_float"
    )

    if (isPlaying) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val notes = listOf("♪", "♫", "♩", "♬")
            notes.forEachIndexed { index, symbol ->
                val offsetPhase = (floatAnim + (index * 0.25f)) % 1f
                val yOffset = (-15f * kotlin.math.sin(offsetPhase * Math.PI)).toFloat()
                val alpha = (kotlin.math.sin(offsetPhase * Math.PI)).toFloat().coerceIn(0.2f, 1f)

                Text(
                    text = symbol,
                    fontSize = (18 + (index % 2) * 4).sp,
                    fontWeight = FontWeight.Bold,
                    color = CatPink.copy(alpha = alpha),
                    modifier = Modifier
                        .offset(y = yOffset.dp)
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}
