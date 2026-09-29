package com.example.ui.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.data.models.CatMission
import com.example.ui.components.CartoonCatFace
import com.example.ui.components.CartoonPawPrint
import com.example.ui.components.FloatingMusicNotes
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
import com.example.utils.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.absoluteValue

@Composable
fun MonthRecapCarouselScreen(
    viewModel: CatCareViewModel,
    onNavigateToMural: () -> Unit,
    onNavigateToMissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val photos by viewModel.monthPhotos.collectAsStateWithLifecycle()
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()

    val (year, month) = selectedYearMonth
    val monthName = DateUtils.formatMonthYear(year, month)

    // Audio & Soundtrack States
    val isSynthPlaying by viewModel.synthesizer.isPlaying.collectAsStateWithLifecycle()
    val isSynthMuted by viewModel.synthesizer.isMuted.collectAsStateWithLifecycle()
    val currentMelodyIndex by viewModel.synthesizer.currentMelodyIndex.collectAsStateWithLifecycle()

    var customAudioUri by remember { mutableStateOf<Uri?>(null) }
    var customAudioTitle by remember { mutableStateOf<String?>(null) }
    var customMediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isCustomAudioPlaying by remember { mutableStateOf(false) }

    // Combined Soundtrack Play state
    val isAudioPlaying = if (customAudioUri != null) isCustomAudioPlaying else isSynthPlaying

    // Pager & Slideshow Controls
    var isAutoPlayEnabled by remember { mutableStateOf(true) }
    var zoomMission by remember { mutableStateOf<CatMission?>(null) }

    // Custom Audio Picker launcher
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                // Pause synthesizer if it was playing
                viewModel.synthesizer.pause()

                // Release previous media player if active
                customMediaPlayer?.stop()
                customMediaPlayer?.release()

                val newPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(context, uri)
                    isLooping = true
                    prepare()
                    start()
                }

                customMediaPlayer = newPlayer
                customAudioUri = uri
                isCustomAudioPlaying = true

                // Extract filename
                val fileName = getFileNameFromUri(context, uri) ?: "Minha Trilha Sonora"
                customAudioTitle = fileName
                Toast.makeText(context, "Trilha personalizada carregada: $fileName 🎵", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Erro ao carregar áudio: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Auto-start music if photos exist and user has not muted/stopped
    LaunchedEffect(photos.isNotEmpty()) {
        if (photos.isNotEmpty() && !isSynthPlaying && customAudioUri == null) {
            viewModel.synthesizer.play(currentMelodyIndex)
        }
    }

    // Safely stop all sound playback when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.synthesizer.pause()
            try {
                customMediaPlayer?.stop()
                customMediaPlayer?.release()
                customMediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // HorizontalPager State
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { photos.size.coerceAtLeast(1) }
    )

    // Slideshow Auto-Advance Effect
    LaunchedEffect(isAutoPlayEnabled, photos.size, pagerState.currentPage) {
        if (isAutoPlayEnabled && photos.size > 1) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % photos.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    // Fullscreen Zoom Dialog
    if (zoomMission != null) {
        RecapPhotoZoomDialog(
            mission = zoomMission!!,
            catName = profile.name,
            onDismiss = { zoomMission = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CatVanilla)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        // Month Selector Bar (Previous Month < > Next Month)
        MonthSelectorHeader(
            monthName = monthName,
            onPreviousMonth = {
                val newMonth = if (month == 1) 12 else month - 1
                val newYear = if (month == 1) year - 1 else year
                viewModel.selectMonth(newYear, newMonth)
            },
            onNextMonth = {
                val newMonth = if (month == 12) 1 else month + 1
                val newYear = if (month == 12) year + 1 else year
                viewModel.selectMonth(newYear, newMonth)
            }
        )

        // Soundtrack Console Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFFF0EB), Color(0xFFFFFAED))
                        )
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with floating notes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CartoonPawPrint(size = 26.dp, color = CatPeachPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🎵 Trilha Sonora da Retrospectiva",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CatBrownText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FloatingMusicNotes(isPlaying = isAudioPlaying)
                    }
                    CartoonPawPrint(size = 26.dp, color = CatPeachPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Soundtrack Controls Bar
                SoundtrackPlayerBar(
                    isPlaying = isAudioPlaying,
                    isCustom = customAudioUri != null,
                    trackTitle = customAudioTitle ?: viewModel.synthesizer.availableMelodies[currentMelodyIndex].name,
                    onTogglePlay = {
                        if (customAudioUri != null) {
                            val player = customMediaPlayer
                            if (player != null) {
                                if (player.isPlaying) {
                                    player.pause()
                                    isCustomAudioPlaying = false
                                } else {
                                    player.start()
                                    isCustomAudioPlaying = true
                                }
                            }
                        } else {
                            viewModel.synthesizer.togglePlayPause()
                        }
                    },
                    onSelectCustomAudio = {
                        audioPickerLauncher.launch("audio/*")
                    },
                    onResetToMelodies = {
                        try {
                            customMediaPlayer?.stop()
                            customMediaPlayer?.release()
                            customMediaPlayer = null
                            customAudioUri = null
                            customAudioTitle = null
                            isCustomAudioPlaying = false
                            viewModel.synthesizer.play(currentMelodyIndex)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )

                // Melody selector chips (visible if using built-in tunes)
                if (customAudioUri == null) {
                    Text(
                        text = "Melodias Fofas Felinas:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CatBrownMuted,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        itemsIndexed(viewModel.synthesizer.availableMelodies) { index, melody ->
                            FilterChip(
                                selected = index == currentMelodyIndex,
                                onClick = { viewModel.synthesizer.selectMelody(index) },
                                label = { Text(melody.name, fontSize = 11.sp) },
                                modifier = Modifier.padding(horizontal = 4.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CatPeachPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Carousel of Photos Section
        if (photos.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Carousel Top Bar with Navigation Arrows & Page Counter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (pagerState.currentPage > 0) {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                            } else {
                                scope.launch { pagerState.animateScrollToPage(photos.size - 1) }
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, CircleShape)
                            .shadow(2.dp, CircleShape)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = CatBrownText)
                    }

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = "📸 Foto ${pagerState.currentPage + 1} de ${photos.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CatBrownText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val next = (pagerState.currentPage + 1) % photos.size
                            scope.launch { pagerState.animateScrollToPage(next) }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, CircleShape)
                            .shadow(2.dp, CircleShape)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Próxima", tint = CatBrownText)
                    }
                }

                // 3D Carousel (HorizontalPager)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(430.dp)
                        .testTag("month_photos_carousel"),
                    contentPadding = PaddingValues(horizontal = 36.dp),
                    pageSpacing = 16.dp
                ) { page ->
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    val mission = photos[page]

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                val scale = lerp(0.86f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                scaleX = scale
                                scaleY = scale
                                alpha = lerp(0.65f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                val tiltDirection = if (page < pagerState.currentPage) -1 else 1
                                rotationZ = (pageOffset * 3.5f) * tiltDirection
                            }
                            .clickable { zoomMission = mission }
                    ) {
                        PolaroidPhotoCard(
                            mission = mission,
                            catName = profile.name,
                            skinType = profile.equippedSkin,
                            accessoryType = profile.equippedAccessory,
                            pageIndex = page
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Page Indicator Dots
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    photos.forEachIndexed { index, _ ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (isSelected) 10.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) CatPeachPrimary else CatPeachLight)
                                .clickable {
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                }
                        )
                    }
                }

                // Auto-advance slideshow toggle button
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { isAutoPlayEnabled = !isAutoPlayEnabled },
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAutoPlayEnabled) "⏱️ Carrossel Automático Ativo" else "⏸️ Carrossel em Pausa",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAutoPlayEnabled) CatPeachPrimary else CatBrownMuted
                        )
                    }
                }
            }
        } else {
            // Empty State with option to generate cute sample memories
            EmptyRecapCard(
                monthName = monthName,
                catName = profile.name,
                skinType = profile.equippedSkin,
                accessoryType = profile.equippedAccessory,
                onGenerateSample = {
                    viewModel.generateSampleMonthMemories(context)
                    Toast.makeText(context, "Retrospectiva fofa de demonstração gerada! ✨", Toast.LENGTH_SHORT).show()
                },
                onAddProofClick = onNavigateToMissions
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Monthly Stats & Care Score
        RecapStatsCard(
            totalPhotos = photos.size,
            streakDays = streakDays,
            catName = profile.name
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Mural & Share
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigateToMural,
                modifier = Modifier
                    .weight(1f)
                    .testTag("open_mural_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🖼️ Ver no Mural", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { viewModel.shareCurrentMural(context) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("share_recap_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Compartilhar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MonthSelectorHeader(
    monthName: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Mês Anterior",
                    tint = CatPeachPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Mês da Retrospectiva",
                    fontSize = 11.sp,
                    color = CatBrownMuted,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = monthName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CatBrownText
                )
            }

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Próximo Mês",
                    tint = CatPeachPrimary
                )
            }
        }
    }
}

@Composable
private fun SoundtrackPlayerBar(
    isPlaying: Boolean,
    isCustom: Boolean,
    trackTitle: String,
    onTogglePlay: () -> Unit,
    onSelectCustomAudio: () -> Unit,
    onResetToMelodies: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_spin"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CatPinkLight)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause Circle Button
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(44.dp)
                        .background(CatPeachPrimary, CircleShape)
                        .testTag("toggle_music_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Tocar",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Track Info & Spinning Vinyl Icon
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(if (isPlaying) rotation else 0f)
                                .clip(CircleShape)
                                .background(CatBrownText),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CatPeachPrimary)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = trackTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CatBrownText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = if (isCustom) "Arquivo de áudio personalizado 🎧" else "Trilha sonora musical fofa 🎶",
                        fontSize = 11.sp,
                        color = CatBrownMuted
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Custom audio picker button
                IconButton(
                    onClick = onSelectCustomAudio,
                    modifier = Modifier
                        .size(38.dp)
                        .background(CatPeachLight.copy(alpha = 0.4f), CircleShape)
                        .testTag("pick_custom_audio_button")
                ) {
                    Icon(
                        Icons.Default.UploadFile,
                        contentDescription = "Adicionar Música do Celular",
                        tint = CatPeachPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (isCustom) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Restaurar melodias de caixinha de música 🐾",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CatPeachPrimary,
                        modifier = Modifier.clickable { onResetToMelodies() }
                    )
                }
            }
        }
    }
}

@Composable
private fun PolaroidPhotoCard(
    mission: CatMission,
    catName: String,
    skinType: String = "WHITE",
    accessoryType: String = "NONE",
    pageIndex: Int = 0
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Washi tape decorative sticker
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(14.dp)
                    .background(
                        if (pageIndex % 2 == 0) CatPinkLight else CatPeachLight,
                        RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Main Polaroid Photo Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9F6F0))
                    .border(1.dp, Color(0xFFEBE5DF), RoundedCornerShape(12.dp))
            ) {
                val photoFile = remember(mission.photoPath) {
                    mission.photoPath?.let { File(it) }
                }

                if (photoFile != null && photoFile.exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(photoFile),
                        contentDescription = mission.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CartoonCatFace(
                            size = 110.dp,
                            skinType = skinType,
                            accessoryType = accessoryType
                        )
                    }
                }

                // Date stamp sticker in bottom corner
                Surface(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "📅 ${DateUtils.formatDayMonth(mission.date)}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Category emoji badge
                Surface(
                    color = CatPeachPrimary,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = mission.getCategoryEnum().iconEmoji,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Polaroid caption
            Text(
                text = "${mission.getCategoryEnum().iconEmoji} ${mission.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = CatBrownText,
                textAlign = TextAlign.Center
            )

            Text(
                text = "\"${mission.catComment}\"",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = CatPeachPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = CatYellow,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Aprovado por $catName 🐾",
                    fontSize = 11.sp,
                    color = CatBrownMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyRecapCard(
    monthName: String,
    catName: String,
    skinType: String = "WHITE",
    accessoryType: String = "NONE",
    onGenerateSample: () -> Unit,
    onAddProofClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CartoonCatFace(
                size = 90.dp,
                skinType = skinType,
                accessoryType = accessoryType
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sem fotos em $monthName ainda!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CatBrownText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "O $catName está te esperando para cumprir missões com fotos e criar a retrospectiva animada com trilha sonora! 🐾📸",
                fontSize = 13.sp,
                color = CatBrownMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGenerateSample,
                colors = ButtonDefaults.buttonColors(containerColor = CatMint),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("generate_sample_recap_button")
            ) {
                Text("✨ Gerar Demonstração do Mês", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onAddProofClick,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cumprir Missão Diária com Foto 📸")
            }
        }
    }
}

@Composable
private fun RecapStatsCard(
    totalPhotos: Int,
    streakDays: Int,
    catName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Estatísticas do Miau Amor 💖",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = CatBrownText
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(label = "Fotos no Álbum", value = "$totalPhotos 📸", color = CatPeachPrimary)
                StatItem(label = "Sequência", value = "$streakDays dias 🔥", color = CatPink)
                StatItem(label = "Amor Felino", value = "100% 😻", color = CatMint)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Black,
            fontSize = 17.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = CatBrownMuted
        )
    }
}

@Composable
private fun RecapPhotoZoomDialog(
    mission: CatMission,
    catName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 ${DateUtils.formatDayMonth(mission.date)}",
                        fontWeight = FontWeight.Bold,
                        color = CatPeachPrimary,
                        fontSize = 14.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                val photoFile = remember(mission.photoPath) {
                    mission.photoPath?.let { File(it) }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFF9F5))
                ) {
                    if (photoFile != null && photoFile.exists()) {
                        Image(
                            painter = rememberAsyncImagePainter(photoFile),
                            contentDescription = mission.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CartoonCatFace(size = 120.dp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "${mission.getCategoryEnum().iconEmoji} ${mission.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CatBrownText
                )

                Text(
                    text = "\"${mission.catComment}\"",
                    fontSize = 13.sp,
                    color = CatPeachPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Aprovado com 5 estrelas pelo gatinho $catName! ⭐⭐⭐⭐⭐",
                    fontSize = 11.sp,
                    color = CatBrownMuted
                )
            }
        }
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    result = it.getString(index)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}
