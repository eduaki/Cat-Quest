package com.example.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.ReminderConfig
import com.example.notifications.CatNotificationHelper
import com.example.ui.components.CartoonCatFace
import com.example.ui.components.CartoonPawPrint
import com.example.ui.components.CatAffectionNotificationManagerSection
import com.example.ui.components.WeekMissionsBarChart
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
import java.util.Locale

@Composable
fun RemindersSettingsScreen(
    viewModel: CatCareViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val config by viewModel.reminderConfig.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val weekData by viewModel.weekMissionsProgress.collectAsStateWithLifecycle()
    val affectionMessages by viewModel.affectionMessages.collectAsStateWithLifecycle()

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Lembretes fofos ativados com sucesso! 🐾", Toast.LENGTH_SHORT).show()
        }
    }

    var petName by remember(profile.name) { mutableStateOf(profile.name) }
    var petBreed by remember(profile.breed) { mutableStateOf(profile.breed) }
    var petSnack by remember(profile.favoriteSnack) { mutableStateOf(profile.favoriteSnack) }
    var isEditingProfile by remember { mutableStateOf(false) }

    val activeSchedules = remember(config) {
        CatNotificationHelper.getFormattedActiveSchedules(config)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CatVanilla),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CartoonCatFace(
                        size = 64.dp,
                        skinType = profile.equippedSkin,
                        accessoryType = profile.equippedAccessory
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Lembretes & Mensagens 🔔",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CatBrownText
                        )
                        Text(
                            text = "Notificações carinhosas e avisos de missões do seu gato!",
                            style = MaterialTheme.typography.bodySmall,
                            color = CatPeachPrimary
                        )
                    }
                }
            }
        }

        // Permission Request Banner (Android 13+)
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE0)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CatPeachPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🐾", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Permissão de Notificação Necessária",
                                fontWeight = FontWeight.Bold,
                                color = CatBrownText
                            )
                        }
                        Text(
                            text = "Para que o ${profile.name} possa te lembrar das missões diárias com miados carinhosos, autorize o envio de notificações.",
                            fontSize = 12.sp,
                            color = CatBrownMuted,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Autorizar Lembretes Fofos 🐾", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active Scheduled Alarms Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Alarm,
                                contentDescription = null,
                                tint = CatPeachPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Status dos Lembretes Agendados",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = CatBrownText
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (activeSchedules.isNotEmpty()) CatMint.copy(alpha = 0.2f) else Color(0xFFFFECE0)
                        ) {
                            Text(
                                text = if (activeSchedules.isNotEmpty()) "⏰ ${activeSchedules.size} Ativos" else "Inativo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeSchedules.isNotEmpty()) CatMint else CatPeachPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (activeSchedules.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            activeSchedules.forEach { (period, time) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFFF9F5),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CatPeachLight),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = period, fontSize = 11.sp, color = CatBrownMuted)
                                        Text(
                                            text = time,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = CatPeachPrimary
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Nenhum horário ativado. Ative um dos períodos abaixo para receber avisos do seu gatinho!",
                            fontSize = 12.sp,
                            color = CatBrownMuted
                        )
                    }
                }
            }
        }

        // Local Notification Manager: Personalized Affection & Encouragement Section
        item {
            CatAffectionNotificationManagerSection(
                messages = affectionMessages,
                catName = profile.name,
                onAddCustomMessage = { title, text, category ->
                    viewModel.addCustomAffectionMessage(title, text, category)
                },
                onToggleMessage = { msg ->
                    viewModel.toggleAffectionMessage(msg)
                },
                onDeleteMessage = { msg ->
                    viewModel.deleteAffectionMessage(msg)
                },
                onDispatchNow = { msg ->
                    viewModel.dispatchAffectionNotificationNow(context, msg)
                }
            )
        }

        // Friendly Tone Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tom das Mensagens do Bichano 😸",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CatBrownText
                    )
                    Text(
                        text = "Personalize o jeitinho que o gato fala com você:",
                        fontSize = 12.sp,
                        color = CatBrownMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val tones = listOf(
                            Triple("CARINHOSO", "Carinhoso 💖", "Cheio de amor e ronrons"),
                            Triple("BRINCALHAO", "Brincalhão 🧶", "Animado e saltitante"),
                            Triple("RECLAMAO_FOFO", "Exigente 😹", "Cobrando o sachê já")
                        )

                        tones.forEach { (key, label, _) ->
                            FilterChip(
                                selected = config.friendlyTone == key,
                                onClick = {
                                    viewModel.updateReminderConfig(config.copy(friendlyTone = key))
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CatPeachPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Sample preview of message
                    val sampleMessage = remember(config.friendlyTone, profile.name) {
                        CatNotificationHelper.getRandomFriendlyMessage(
                            profile.name,
                            "MORNING",
                            config.friendlyTone
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF9F5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFECE0))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Exemplo de Notificação:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CatPeachPrimary
                            )
                            Text(
                                text = "${sampleMessage.first}\n${sampleMessage.second}",
                                fontSize = 12.sp,
                                color = CatBrownText
                            )
                        }
                    }
                }
            }
        }

        // Sound & Vibration Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = CatPeachPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Som & Vibração dos Lembretes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = CatBrownText
                            )
                            Text(
                                text = if (config.soundEnabled) "Ativado (com vibração fofa)" else "Silencioso",
                                fontSize = 12.sp,
                                color = CatBrownMuted
                            )
                        }
                    }

                    Switch(
                        checked = config.soundEnabled,
                        onCheckedChange = { isEnabled ->
                            viewModel.updateReminderConfig(config.copy(soundEnabled = isEnabled))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CatPeachPrimary
                        )
                    )
                }
            }
        }

        // Daily Reminder Slots
        item {
            Text(
                text = "Horários dos Lembretes Diários ⏰",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = CatBrownText,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // Morning Slot
        item {
            ReminderSlotCard(
                title = "Manhã: Água Limpa & Comidinha",
                iconEmoji = "💧🐟",
                enabled = config.morningEnabled,
                hour = config.morningHour,
                minute = config.morningMinute,
                onToggle = { isEnabled ->
                    viewModel.updateReminderConfig(config.copy(morningEnabled = isEnabled))
                },
                onTimeChange = { h, m ->
                    viewModel.updateReminderConfig(config.copy(morningHour = h, morningMinute = m))
                }
            )
        }

        // Afternoon Slot
        item {
            ReminderSlotCard(
                title = "Tarde: Brincadeira & Pelos",
                iconEmoji = "🧶✨",
                enabled = config.afternoonEnabled,
                hour = config.afternoonHour,
                minute = config.afternoonMinute,
                onToggle = { isEnabled ->
                    viewModel.updateReminderConfig(config.copy(afternoonEnabled = isEnabled))
                },
                onTimeChange = { h, m ->
                    viewModel.updateReminderConfig(config.copy(afternoonHour = h, afternoonMinute = m))
                }
            )
        }

        // Evening Slot
        item {
            ReminderSlotCard(
                title = "Noite: Caixinha de Areia & Mimos",
                iconEmoji = "🧼🌙",
                enabled = config.eveningEnabled,
                hour = config.eveningHour,
                minute = config.eveningMinute,
                onToggle = { isEnabled ->
                    viewModel.updateReminderConfig(config.copy(eveningEnabled = isEnabled))
                },
                onTimeChange = { h, m ->
                    viewModel.updateReminderConfig(config.copy(eveningHour = h, eveningMinute = m))
                }
            )
        }

        // Weekly Missions Progress Bar Chart (D3 / Recharts inside Compose)
        item {
            WeekMissionsBarChart(
                weekData = weekData,
                catName = profile.name,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Cat Profile Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Perfil do Seu Gatinho 🐾",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CatBrownText
                        )
                        OutlinedButton(
                            onClick = {
                                if (isEditingProfile) {
                                    viewModel.updateProfile(petName, petBreed, petSnack)
                                }
                                isEditingProfile = !isEditingProfile
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isEditingProfile) "Salvar" else "Editar")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isEditingProfile) {
                        OutlinedTextField(
                            value = petName,
                            onValueChange = { petName = it },
                            label = { Text("Nome do Gatinho") },
                            modifier = Modifier.fillMaxWidth().testTag("cat_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = petBreed,
                            onValueChange = { petBreed = it },
                            label = { Text("Raça / Tipo de Pelagem") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = petSnack,
                            onValueChange = { petSnack = it },
                            label = { Text("Petisco Favorito") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = "Nome: ${profile.name}",
                            fontWeight = FontWeight.SemiBold,
                            color = CatPeachPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Pelagem: ${profile.breed}",
                            color = CatBrownMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Petisco Preferido: ${profile.favoriteSnack}",
                            color = CatBrownMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderSlotCard(
    title: String,
    iconEmoji: String,
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onToggle: (Boolean) -> Unit,
    onTimeChange: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(text = iconEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CatBrownText
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (enabled) CatPeachLight.copy(alpha = 0.3f) else Color(0xFFEEEEEE),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable(enabled = enabled) {
                                TimePickerDialog(
                                    context,
                                    { _, selectedHour, selectedMinute ->
                                        onTimeChange(selectedHour, selectedMinute)
                                    },
                                    hour,
                                    minute,
                                    true
                                ).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (enabled) CatPeachPrimary else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$timeFormatted (Toque para mudar)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (enabled) CatPeachPrimary else Color.Gray
                            )
                        }
                    }
                }
            }

            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CatPeachPrimary
                )
            )
        }
    }
}
