package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.utils.ScoreCalculator
import com.example.data.models.RewardCatalog
import com.example.data.models.RewardType
import com.example.data.models.ShopRewardItem
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

@Composable
fun RewardsShopScreen(
    viewModel: CatCareViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    val categories = listOf("Acessórios 👑", "Pelagens 🐱", "Temas 🌸")

    val currentItems = when (selectedTab) {
        0 -> RewardCatalog.accessories
        1 -> RewardCatalog.skins
        else -> RewardCatalog.themes
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CatVanilla),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cat Live Preview & Balance Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFFF3E0), Color(0xFFFFECE0))
                            )
                        )
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Lojinha de Recompensas do Bichano 🐟",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = CatBrownText
                    )
                    Text(
                        text = "Complete missões diárias com fotos para ganhar peixinhos!",
                        fontSize = 12.sp,
                        color = CatPeachPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live avatar with equipped skin & accessory
                    CartoonCatFace(
                        size = 110.dp,
                        skinType = profile.equippedSkin,
                        accessoryType = profile.equippedAccessory
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Level & Score Tag
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCCBC))
                    ) {
                        Text(
                            text = "⭐ ${profile.totalScore} pts • ${ScoreCalculator.getLevelTitle(profile.level)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CatBrownText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Currency Balance Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Golden Fish Points
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(CatYellow.copy(alpha = 0.35f))
                                .border(1.dp, CatYellow, RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🐟", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${profile.fishCoins} Peixinhos",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = CatBrownText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Love Paws
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(CatPinkLight.copy(alpha = 0.6f))
                                .border(1.dp, CatPink, RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🐾", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${profile.pawsCount} Patinhas",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = CatBrownText
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = CatPeachPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CatPeachPrimary
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, Color(0xFFFFECE0), RoundedCornerShape(18.dp))
            ) {
                categories.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        // List of items
        items(currentItems, key = { it.id }) { item ->
            val isUnlocked = profile.hasUnlocked(item.id)
            val isEquipped = when (item.type) {
                RewardType.ACCESSORY -> profile.equippedAccessory == item.id
                RewardType.SKIN -> profile.equippedSkin == item.id
                RewardType.THEME -> profile.equippedTheme == item.id
            }
            val canAfford = profile.fishCoins >= item.price

            RewardItemCard(
                item = item,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                canAfford = canAfford,
                onBuyOrEquip = {
                    viewModel.buyOrEquipReward(item)
                }
            )
        }
    }
}

@Composable
private fun RewardItemCard(
    item: ShopRewardItem,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onBuyOrEquip: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reward_item_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) Color(0xFFF9FFF9) else Color.White
        ),
        border = if (isEquipped) {
            androidx.compose.foundation.BorderStroke(1.5.dp, CatMint)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFECE0))
        },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (isEquipped) CatMint.copy(alpha = 0.2f)
                        else CatPeachLight.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.iconEmoji, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CatBrownText
                )
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = CatBrownMuted
                )

                if (!isUnlocked && item.price > 0) {
                    Text(
                        text = "Preço: ${item.price} 🐟",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CatPeachPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            when {
                isEquipped -> {
                    Surface(
                        color = CatMint.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Em Uso ✨",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CatMint,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
                isUnlocked -> {
                    Button(
                        onClick = onBuyOrEquip,
                        colors = ButtonDefaults.buttonColors(containerColor = CatPeachPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Equipar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    Button(
                        onClick = onBuyOrEquip,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(containerColor = CatPink),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (canAfford) "Desbloquear" else "Faltam 🐟",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
