package com.example.ui.screens

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.example.ui.components.CartoonCatFace
import com.example.ui.components.CartoonPawPrint
import com.example.ui.theme.CatBrownMuted
import com.example.ui.theme.CatBrownText
import com.example.ui.theme.CatPeachLight
import com.example.ui.theme.CatPeachPrimary
import com.example.ui.theme.CatPink
import com.example.ui.theme.CatPinkLight
import com.example.ui.theme.CatVanilla
import com.example.ui.viewmodel.CatCareViewModel
import com.example.utils.DateUtils
import java.io.File

@Composable
fun PhotoMuralScreen(
    viewModel: CatCareViewModel,
    onNavigateToMissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val monthPhotos by viewModel.monthPhotos.collectAsStateWithLifecycle()
    val allPhotos by viewModel.allPhotos.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsStateWithLifecycle()

    val (year, month) = selectedYearMonth
    val monthName = DateUtils.formatMonthYear(year, month)

    var showOnlyCurrentMonth by remember { mutableStateOf(true) }
    var selectedPhotoDetail by remember { mutableStateOf<CatMission?>(null) }

    val displayedPhotos = if (showOnlyCurrentMonth) monthPhotos else allPhotos

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CatVanilla)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header span across both columns
            item(span = { GridItemSpan(2) }) {
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CartoonPawPrint(size = 22.dp, color = CatPeachPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Mural de Lembranças 🖼️",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = CatBrownText
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                CartoonPawPrint(size = 22.dp, color = CatPeachPrimary)
                            }

                            Text(
                                text = "Álbum de carinho e cuidados com o ${profile.name} 🐱",
                                style = MaterialTheme.typography.bodySmall,
                                color = CatPeachPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Filter Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                FilterChip(
                                    selected = showOnlyCurrentMonth,
                                    onClick = { showOnlyCurrentMonth = true },
                                    label = { Text(monthName, fontSize = 12.sp) },
                                    modifier = Modifier.padding(end = 8.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CatPeachPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = !showOnlyCurrentMonth,
                                    onClick = { showOnlyCurrentMonth = false },
                                    label = { Text("Todas as Fotos (${allPhotos.size})", fontSize = 12.sp) },
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

            // Empty state if no photos
            if (displayedPhotos.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CartoonCatFace(
                                size = 80.dp,
                                skinType = profile.equippedSkin,
                                accessoryType = profile.equippedAccessory
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "O mural ainda está vazio!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CatBrownText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tire fotos das missões diárias para preencher o mural de amor do ${profile.name}! 📸✨",
                                fontSize = 13.sp,
                                color = CatBrownMuted,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onNavigateToMissions,
                                colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Ir para as Missões Diárias 🐾")
                            }
                        }
                    }
                }
            }

            // Grid items: Polaroid cards
            items(displayedPhotos, key = { it.id }) { mission ->
                MuralPolaroidCard(
                    mission = mission,
                    skinType = profile.equippedSkin,
                    accessoryType = profile.equippedAccessory,
                    onClick = { selectedPhotoDetail = mission }
                )
            }
        }

        // Floating Action Button to Share Mural
        if (displayedPhotos.isNotEmpty()) {
            FloatingActionButton(
                onClick = { viewModel.shareCurrentMural(context) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp, end = 20.dp)
                    .testTag("share_mural_fab"),
                containerColor = CatPink,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Compartilhar")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compartilhar Mural", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    // Detail Photo Modal
    if (selectedPhotoDetail != null) {
        val mission = selectedPhotoDetail!!
        Dialog(onDismissRequest = { selectedPhotoDetail = null }) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${mission.getCategoryEnum().iconEmoji} ${mission.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CatBrownText,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { selectedPhotoDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (mission.photoPath != null && File(mission.photoPath).exists()) {
                        Image(
                            painter = rememberAsyncImagePainter(File(mission.photoPath)),
                            contentDescription = "Foto da Missão",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(18.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Data: " + DateUtils.formatDayMonth(mission.date),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CatPeachPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = CatPinkLight.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "\"${mission.catComment}\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CatBrownText,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.shareCurrentMural(context)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartilhar esta Lembrança", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MuralPolaroidCard(
    mission: CatMission,
    skinType: String = "WHITE",
    accessoryType: String = "NONE",
    onClick: () -> Unit
) {
    // Subtle whimsical rotation
    val angle = remember(mission.id) {
        val angles = listOf(-2.5f, 2f, -1.5f, 1.8f, -0.8f)
        angles[(mission.id % angles.size).toInt()]
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .rotate(angle)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("mural_card_${mission.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFECE0))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Little washi tape sticker at the top
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (mission.id % 2L == 0L) CatPinkLight.copy(alpha = 0.85f)
                        else CatPeachLight.copy(alpha = 0.85f)
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Photo image square
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                if (mission.photoPath != null && File(mission.photoPath).exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(File(mission.photoPath)),
                        contentDescription = mission.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    CartoonCatFace(
                        size = 60.dp,
                        skinType = skinType,
                        accessoryType = accessoryType
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Caption
            Text(
                text = "${mission.getCategoryEnum().iconEmoji} ${mission.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = CatBrownText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = DateUtils.formatDayMonth(mission.date),
                fontSize = 10.sp,
                color = CatPeachPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
