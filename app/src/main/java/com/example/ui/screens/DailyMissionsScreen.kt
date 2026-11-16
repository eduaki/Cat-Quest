package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.data.models.CatMission
import com.example.data.models.MissionCategory
import com.example.ui.camera.CatCameraScreen
import com.example.ui.components.CartoonCatFace
import com.example.ui.components.CartoonPawPrint
import com.example.ui.components.MissionCategoryIcon
import com.example.ui.components.getCategoryIcon
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
import com.example.utils.PhotoManager
import com.example.utils.ScoreCalculator
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMissionsScreen(
    viewModel: CatCareViewModel,
    onOpenShop: () -> Unit = {},
    onOpenReminders: () -> Unit = {},
    onCameraStateChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val missions by viewModel.selectedDateMissions.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()
    val celebrationMission by viewModel.recentlyCompletedMission.collectAsStateWithLifecycle()
    val lastScoreResult by viewModel.lastScoreResult.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMissionForPhoto by remember { mutableStateOf<CatMission?>(null) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }
    var previewPhotoPath by remember { mutableStateOf<String?>(null) }
    var activeCameraMission by remember { mutableStateOf<CatMission?>(null) }

    LaunchedEffect(activeCameraMission) {
        onCameraStateChanged(activeCameraMission != null)
    }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null && selectedMissionForPhoto != null) {
            val savedPath = PhotoManager.saveImageFromUri(context, tempCameraUri!!)
            if (savedPath != null) {
                viewModel.completeMissionWithPhoto(selectedMissionForPhoto!!, savedPath)
            }
            showPhotoOptionsDialog = false
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = PhotoManager.saveImageFromUri(context, uri)
            if (savedPath != null && selectedMissionForPhoto != null) {
                viewModel.completeMissionWithPhoto(selectedMissionForPhoto!!, savedPath)
            }
            showPhotoOptionsDialog = false
        }
    }

    val completedCount = missions.count { it.isCompleted }
    val progress = if (missions.isNotEmpty()) completedCount.toFloat() / missions.size else 0f

    // Mantém a posição de rolagem da lista ao voltar da câmera.
    val missionsListState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize().background(CatVanilla)) {
        // A câmera é o ÚNICO filho de layout da tela enquanto está aberta: nada pode
        // ficar composto atrás dela. Dentro de um pager (layout "lazy"), composables
        // irmãos são medidos/posicionados lado a lado — e a câmera, declarada como
        // irmã da lista, era posicionada fora da tela, por isso não abria.
        if (activeCameraMission != null) {
            CatCameraScreen(
                mission = activeCameraMission!!,
                catName = profile.name,
                onPhotoCaptured = { savedPath ->
                    viewModel.completeMissionWithPhoto(activeCameraMission!!, savedPath)
                    activeCameraMission = null
                },
                onClose = { activeCameraMission = null }
            )
        } else {
            LazyColumn(
                state = missionsListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                item {
                    HeaderBanner(
                        catName = profile.name,
                        dateStr = selectedDate,
                        streak = streakDays,
                        completed = completedCount,
                        total = missions.size,
                        progress = progress,
                        fishCoins = profile.fishCoins,
                        totalScore = profile.totalScore,
                        level = profile.level,
                        equippedSkin = profile.equippedSkin,
                        equippedAccessory = profile.equippedAccessory,
                        onOpenShop = onOpenShop,
                        onOpenReminders = onOpenReminders
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Missões Diárias de Cuidado",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CatBrownText
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                CartoonPawPrint(size = 18.dp)
                            }
                            Text(
                                text = "Tire uma foto para comprovar cada missão!",
                                style = MaterialTheme.typography.bodySmall,
                                color = CatBrownMuted
                            )
                        }
                    }
                }

                items(missions, key = { it.id }) { mission ->
                    MissionCardItem(
                        mission = mission,
                        onTakePhotoClick = {
                            selectedMissionForPhoto = mission
                            showPhotoOptionsDialog = true
                        },
                        onViewPhotoClick = { path ->
                            previewPhotoPath = path
                        },
                        onUndoClick = {
                            viewModel.undoMission(mission.id)
                        },
                        onDeleteClick = {
                            viewModel.deleteMission(mission)
                        }
                    )
                }

                if (missions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CartoonCatFace(size = 90.dp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Nenhuma missão para esta data!",
                                    fontWeight = FontWeight.Bold,
                                    color = CatBrownText
                                )
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp, end = 20.dp)
                    .testTag("add_custom_mission_fab"),
                containerColor = CatPeachPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Missão")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nova Missão", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    if (showPhotoOptionsDialog && selectedMissionForPhoto != null) {
        val mission = selectedMissionForPhoto!!
        AlertDialog(
            onDismissRequest = { showPhotoOptionsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CatPeachPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Comprovar Missão",
                        fontWeight = FontWeight.Bold,
                        color = CatBrownText
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = mission.title,
                        fontWeight = FontWeight.SemiBold,
                        color = CatPeachPrimary
                    )
                    Text(
                        text = mission.getCategoryEnum().suggestionPrompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = CatBrownMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    Button(
                        onClick = {
                            activeCameraMission = mission
                            showPhotoOptionsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().testTag("open_camera_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abrir Câmera do Gatinho")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("pick_gallery_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escolher da Galeria")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            viewModel.completeMissionWithInstantProof(context, mission)
                            showPhotoOptionsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().testTag("instant_proof_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CatMint),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gerar Selo Mágico Fofo")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoOptionsDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (celebrationMission != null) {
        val completed = celebrationMission!!
        Dialog(onDismissRequest = { viewModel.dismissCelebration() }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CartoonCatFace(size = 90.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CartoonPawPrint(size = 20.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MISSÃO APROVADA!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = CatPeachPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CartoonPawPrint(size = 20.dp)
                    }
                    Text(
                        text = completed.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = CatBrownText
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (completed.photoPath != null) {
                        Image(
                            painter = rememberAsyncImagePainter(File(completed.photoPath)),
                            contentDescription = "Comprovação da Missão",
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(3.dp, CatPink, RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = completed.catComment,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = CatPeachPrimary,
                        modifier = Modifier
                            .background(CatPinkLight.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFFFF9F5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CatPeachLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = CatYellow, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Cálculo da Recompensa",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CatPeachPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Star, contentDescription = null, tint = CatYellow, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Base da Missão:",
                                    fontSize = 12.sp,
                                    color = CatBrownMuted
                                )
                                Text(
                                    text = "+${lastScoreResult?.basePoints ?: completed.getCategoryEnum().basePoints} pts",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CatBrownText
                                )
                            }

                            if ((lastScoreResult?.streakBonusPoints ?: 0) > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Bônus Sequência (${lastScoreResult?.streakMultiplier ?: 1.0f}x):",
                                        fontSize = 12.sp,
                                        color = CatPink
                                    )
                                    Text(
                                        text = "+${lastScoreResult?.streakBonusPoints} pts",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CatPink
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total de Pontos Adicionados:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CatBrownText
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+${lastScoreResult?.totalPoints ?: (if (completed.pointsEarned > 0) completed.pointsEarned else completed.getCategoryEnum().basePoints)} pts",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CatPeachPrimary
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(Icons.Default.Star, contentDescription = null, tint = CatYellow, modifier = Modifier.size(12.dp))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Peixinhos Adquiridos:",
                                    fontSize = 12.sp,
                                    color = CatBrownMuted
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+${lastScoreResult?.fishCoinsAwarded ?: (if (completed.fishEarned > 0) completed.fishEarned else completed.getCategoryEnum().fishReward)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CatBrownText
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CatYellow, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }

                    if (lastScoreResult?.isLevelUp == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CatYellow.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CatYellow)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CatBrownText, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SUBIU DE NÍVEL! Agora você é ${lastScoreResult?.levelTitle}!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CatBrownText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        repeat(5) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = CatYellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.dismissCelebration() },
                        colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("celebration_continue_button")
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar Cuidando!", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (previewPhotoPath != null) {
        Dialog(onDismissRequest = { previewPhotoPath = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(File(previewPhotoPath!!)),
                        contentDescription = "Foto da Missão",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { previewPhotoPath = null },
                        colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Fechar")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomMissionDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, desc, cat ->
                viewModel.addCustomMission(title, desc, cat)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun HeaderBanner(
    catName: String,
    dateStr: String,
    streak: Int,
    completed: Int,
    total: Int,
    progress: Float,
    fishCoins: Int = 100,
    totalScore: Int = 0,
    level: Int = 1,
    equippedSkin: String = "WHITE",
    equippedAccessory: String = "NONE",
    onOpenShop: () -> Unit = {},
    onOpenReminders: () -> Unit = {}
) {
    val levelTitle = ScoreCalculator.getLevelTitle(level)
    val levelProgress = ScoreCalculator.getLevelProgress(totalScore)
    val (_, nextLevelGoal) = ScoreCalculator.getNextLevelInfo(totalScore)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFECE0),
                            Color(0xFFFFF9F5)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onOpenReminders() }
                            .testTag("open_profile_header")
                    ) {
                        CartoonCatFace(
                            size = 72.dp,
                            skinType = equippedSkin,
                            accessoryType = equippedAccessory
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Gatinho $catName",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CatBrownText
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Pets, contentDescription = null, tint = CatBrownText, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = DateUtils.formatDayMonth(dateStr),
                                style = MaterialTheme.typography.bodyMedium,
                                color = CatPeachPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = CatYellow, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$totalScore pts • Nível $level",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CatBrownMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CatPinkLight.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CatPink)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = CatPink, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$streak d",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CatBrownText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CatPeachLight.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CatPeachPrimary),
                            modifier = Modifier.clickable { onOpenReminders() }.testTag("open_reminders_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = CatBrownText, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Avisos",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CatBrownText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CatYellow.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CatYellow),
                            modifier = Modifier.clickable { onOpenShop() }.testTag("open_shop_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CatBrownText, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$fishCoins",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CatBrownText
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCCBC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Classificação: $levelTitle",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CatBrownText
                        )
                        Text(
                            text = "$totalScore / $nextLevelGoal pts",
                            fontSize = 10.sp,
                            color = CatBrownMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progresso do Dia: $completed de $total missões",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = CatBrownMuted
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = CatPeachPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = CatPeachPrimary,
                    trackColor = Color(0xFFFFE0B2),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun MissionCardItem(
    mission: CatMission,
    onTakePhotoClick: () -> Unit,
    onViewPhotoClick: (String) -> Unit,
    onUndoClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("mission_card_${mission.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (mission.isCompleted) Color(0xFFF9FFF9) else Color.White
        ),
        border = if (mission.isCompleted) {
            androidx.compose.foundation.BorderStroke(1.5.dp, CatMint.copy(alpha = 0.6f))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFECE0))
        },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (mission.isCompleted) CatMint.copy(alpha = 0.15f)
                            else CatPeachLight.copy(alpha = 0.35f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    MissionCategoryIcon(
                        category = mission.getCategoryEnum(),
                        tint = if (mission.isCompleted) CatMint else CatPeachPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = mission.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CatBrownText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (mission.isCompleted) CatMint.copy(alpha = 0.15f) else CatYellow.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (mission.isCompleted) {
                                        "+${if (mission.pointsEarned > 0) mission.pointsEarned else mission.getCategoryEnum().basePoints} pts"
                                    } else {
                                        "+${mission.getCategoryEnum().basePoints} pts • ${mission.getCategoryEnum().fishReward} "
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mission.isCompleted) CatMint else CatBrownText
                                )
                                if (!mission.isCompleted) {
                                    Icon(
                                        Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(10.dp),
                                        tint = CatBrownText
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = mission.description,
                        fontSize = 12.sp,
                        color = CatBrownMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (mission.isCustom) {
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (mission.isCompleted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CatMint.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (mission.photoPath != null) {
                        Image(
                            painter = rememberAsyncImagePainter(File(mission.photoPath)),
                            contentDescription = "Foto de comprovação",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onViewPhotoClick(mission.photoPath) },
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CatMint,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Comprovado com Foto!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CatMint
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                CartoonPawPrint(size = 12.dp, color = CatMint)
                            }
                        }
                        Text(
                            text = mission.catComment,
                            fontSize = 11.sp,
                            color = CatBrownMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    TextButton(onClick = onUndoClick) {
                        Text("Refazer", fontSize = 11.sp, color = CatPeachPrimary)
                    }
                }
            } else {
                Button(
                    onClick = onTakePhotoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prove_photo_button_${mission.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tirar Foto de Prova",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCustomMissionDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, description: String, category: MissionCategory) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MissionCategory.LOVE) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Nova Missão do Gatinho",
                    fontWeight = FontWeight.Bold,
                    color = CatBrownText
                )
                Spacer(modifier = Modifier.width(8.dp))
                CartoonPawPrint(size = 20.dp)
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nome da Missão") },
                    placeholder = { Text("Ex: Dar sachê especial, Passeio") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_mission_title_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição do Cuidado") },
                    placeholder = { Text("Ex: Tirar foto do gatinho curtindo") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_mission_desc_input"),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        leadingIcon = {
                            MissionCategoryIcon(category = selectedCategory, modifier = Modifier.size(20.dp), tint = CatPeachPrimary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        MissionCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(getCategoryIcon(cat), contentDescription = null, modifier = Modifier.size(18.dp), tint = CatPeachPrimary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(cat.displayName)
                                    }
                                },
                                onClick = {
                                    selectedCategory = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            title,
                            description.ifBlank { "Tire uma foto para comprovar este carinho!" },
                            selectedCategory
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
