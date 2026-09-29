package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.CatAffectionMessage
import com.example.notifications.LocalCatNotificationManager
import com.example.ui.theme.CatBrownMuted
import com.example.ui.theme.CatBrownText
import com.example.ui.theme.CatMint
import com.example.ui.theme.CatPeachLight
import com.example.ui.theme.CatPeachPrimary
import com.example.ui.theme.CatPink
import com.example.ui.theme.CatPinkLight
import com.example.ui.theme.CatYellow

@Composable
fun CatAffectionNotificationManagerSection(
    messages: List<CatAffectionMessage>,
    catName: String,
    onAddCustomMessage: (String, String, String) -> Unit,
    onToggleMessage: (CatAffectionMessage) -> Unit,
    onDeleteMessage: (CatAffectionMessage) -> Unit,
    onDispatchNow: (CatAffectionMessage?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedIntervalHours by remember { mutableIntStateOf(4) }
    var isListExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cat_affection_notification_manager_section"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = CatPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Carinho & Incentivo Diário 💌",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CatBrownText
                        )
                        Text(
                            text = "Notificações de apoio e afeto do $catName ao longo do dia",
                            fontSize = 12.sp,
                            color = CatBrownMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Send Random Now & Create Custom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onDispatchNow(null)
                        Toast.makeText(context, "Notificação de carinho enviada! 😻", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).testTag("dispatch_affection_now_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CatPink),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Disparar Agora 💖", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f).testTag("add_custom_affection_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = CatPeachPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Criar Recado ✍️", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CatPeachPrimary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Frequency / Interval Selector
            Text(
                text = "Intervalo dos Recados Automáticos:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CatBrownText
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val intervals = listOf(
                    Pair(2, "A cada 2h"),
                    Pair(4, "A cada 4h ⭐"),
                    Pair(6, "A cada 6h"),
                    Pair(8, "2x ao dia")
                )

                intervals.forEach { (hours, label) ->
                    val isSelected = selectedIntervalHours == hours
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedIntervalHours = hours
                            LocalCatNotificationManager.schedulePeriodicAffectionAlarm(
                                context = context,
                                intervalHours = hours,
                                catName = catName
                            )
                            Toast.makeText(context, "Frequência ajustada para $label! 🐾", Toast.LENGTH_SHORT).show()
                        },
                        label = { Text(label, fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CatPeachPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bank of Messages Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isListExpanded = !isListExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Banco de Mensagens (${messages.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CatBrownText
                )

                Text(
                    text = if (isListExpanded) "Ocultar ▲" else "Ver Todas ▼",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CatPeachPrimary
                )
            }

            AnimatedVisibility(visible = isListExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    messages.forEach { msg ->
                        AffectionMessageRowItem(
                            message = msg,
                            catName = catName,
                            onToggle = { onToggleMessage(msg) },
                            onDelete = { onDeleteMessage(msg) },
                            onTestNow = {
                                onDispatchNow(msg)
                                Toast.makeText(context, "Disparando: ${msg.title}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog for adding a custom affection message
    if (showAddDialog) {
        CreateCustomAffectionDialog(
            catName = catName,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, text, category ->
                onAddCustomMessage(title, text, category)
                showAddDialog = false
                Toast.makeText(context, "Novo recado salvo com sucesso! 💖", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun AffectionMessageRowItem(
    message: CatAffectionMessage,
    catName: String,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTestNow: () -> Unit
) {
    val (formattedTitle, formattedText) = remember(message, catName) {
        message.formatMessage(catName)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (message.isEnabled) Color(0xFFFFFBF8) else Color(0xFFF7F7F7),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (message.isEnabled) CatPeachLight else Color(0xFFE5E5E5)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (message.category) {
                            "CARINHO" -> CatPinkLight
                            "AUTOESTIMA" -> CatYellow.copy(alpha = 0.4f)
                            "SAUDE_MENTAL" -> CatMint.copy(alpha = 0.3f)
                            else -> CatPeachLight.copy(alpha = 0.5f)
                        }
                    ) {
                        Text(
                            text = when (message.category) {
                                "CARINHO" -> "💖 Carinho"
                                "AUTOESTIMA" -> "🌟 Autoestima"
                                "SAUDE_MENTAL" -> "🍃 Calma"
                                else -> "🐾 Incentivo"
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CatBrownText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (message.isCustom) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8DEF8)
                        ) {
                            Text(
                                text = "✍️ Seu Recado",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A148C),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Test send button
                    IconButton(
                        onClick = onTestNow,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = "Testar",
                            tint = CatPeachPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete custom button
                    if (message.isCustom) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Excluir",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Switch(
                        checked = message.isEnabled,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CatPeachPrimary
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formattedTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (message.isEnabled) CatBrownText else Color.Gray
            )

            Text(
                text = formattedText,
                fontSize = 12.sp,
                color = if (message.isEnabled) CatBrownMuted else Color.LightGray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun CreateCustomAffectionDialog(
    catName: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("💖 Um Recado do {gato}!") }
    var text by remember { mutableStateOf("O {gato} passou aqui pra lembrar que você é incrível e muito amado! 🐾✨") }
    var category by remember { mutableStateOf("INCENTIVO") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Novo Recado Personalizado 💌",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CatBrownText
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Text(
                    text = "Dica: Digite {gato} no texto para ser substituído automaticamente pelo nome do seu bichano ($catName).",
                    fontSize = 11.sp,
                    color = CatPeachPrimary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título da Notificação") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Mensagem de Carinho ou Incentivo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )

                Text(
                    text = "Categoria do Recado:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CatBrownText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val cats = listOf(
                        Pair("INCENTIVO", "🐾 Incentivo"),
                        Pair("CARINHO", "💖 Carinho"),
                        Pair("AUTOESTIMA", "🌟 Autoestima"),
                        Pair("SAUDE_MENTAL", "🍃 Calma")
                    )

                    cats.forEach { (catKey, label) ->
                        FilterChip(
                            selected = category == catKey,
                            onClick = { category = catKey },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CatPeachPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Live Preview Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF9F5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CatPeachLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Como o tutor verá no celular:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CatPeachPrimary
                        )
                        Text(
                            text = title.replace("{gato}", catName),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CatBrownText
                        )
                        Text(
                            text = text.replace("{gato}", catName),
                            fontSize = 11.sp,
                            color = CatBrownMuted
                        )
                    }
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && text.isNotBlank()) {
                            onConfirm(title.trim(), text.trim(), category)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Salvar Recado de Amor 🐾", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
