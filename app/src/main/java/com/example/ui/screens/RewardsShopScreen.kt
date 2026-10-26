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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.utils.ScoreCalculator
import com.example.data.models.CatProfile
import com.example.data.models.RewardCatalog
import com.example.data.models.RewardType
import com.example.data.models.ShopRewardItem
import com.example.ui.components.CartoonCatFace
import com.example.ui.components.CartoonAccessoryIcon
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
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CatCareViewModel

@Composable
fun RewardsShopScreen(
    viewModel: CatCareViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    
    RewardsShopContent(
        profile = profile,
        onBuyOrEquip = { viewModel.buyOrEquipReward(it) },
        modifier = modifier
    )
}

@Composable
fun RewardsShopContent(
    profile: CatProfile,
    onBuyOrEquip: (ShopRewardItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val categories = listOf("Acessórios", "Pelagens", "Temas")

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = CatBrownText, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lojinha de Recompensas",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = CatBrownText
                        )
                    }
                    Text(
                        text = "Complete missões diárias para ganhar peixinhos!",
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
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = CatYellow, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.totalScore} pts • ${ScoreCalculator.getLevelTitle(profile.level)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CatBrownText
                            )
                        }
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
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CatBrownText, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${profile.fishCoins}",
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
                                CartoonPawPrint(size = 18.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${profile.pawsCount}",
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
                    onBuyOrEquip(item)
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
                if (item.type == RewardType.SKIN) {
                    SkinColorIcon(skinType = item.id)
                } else if (item.type == RewardType.ACCESSORY) {
                    CartoonAccessoryIcon(accessoryType = item.id)
                } else {
                    Icon(
                        imageVector = getRewardIcon(item.iconName),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = if (isEquipped) CatMint else CatPeachPrimary
                    )
                }
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
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            text = "Preço: ${item.price}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CatPeachPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CatPeachPrimary, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            when {
                isEquipped -> {
                    Surface(
                        color = CatMint.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CatMint, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Em Uso",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CatMint
                            )
                        }
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
                        if (!canAfford) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (canAfford) "Desbloquear" else "Faltam moedas",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkinColorIcon(skinType: String) {
    val baseColor = when (skinType) {
        "GINGER" -> Color(0xFFFFB74D)
        "BLACK", "TUXEDO" -> Color(0xFF37474F)
        "SIAMESE" -> Color(0xFFFFF8E7)
        "CALICO" -> Color(0xFFFFFDF8)
        "TABBY" -> Color(0xFFBDBDBD)
        else -> Color.White
    }
    val markingColor = when (skinType) {
        "SIAMESE", "SPOTTED_BROWN" -> Color(0xFF5D4037)
        "CALICO" -> Color(0xFFFF9800)
        "SPOTTED_BLACK" -> Color(0xFF212121)
        "TABBY", "SPOTTED_GRAY" -> Color(0xFF757575)
        "SPOTTED_ORANGE" -> Color(0xFFFF9800)
        else -> null
    }

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(baseColor)
            .border(1.dp, CatBrownMuted.copy(alpha = 0.45f), CircleShape)
    ) {
        if (markingColor != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(3.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(markingColor)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(3.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (skinType == "CALICO") Color(0xFF37474F) else markingColor
                    )
            )
        } else if (skinType == "TUXEDO") {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 3.dp)
                    .size(width = 10.dp, height = 12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
            )
        }
    }
}

private fun getRewardIcon(iconName: String): ImageVector {
    return when (iconName) {
        "pets" -> Icons.Default.Pets
        "celebration" -> Icons.Default.Celebration
        "emoji_events" -> Icons.Default.EmojiEvents
        "theater_comedy" -> Icons.Default.TheaterComedy
        "checkroom" -> Icons.Default.Checkroom
        "auto_awesome" -> Icons.Default.AutoAwesome
        "local_florist" -> Icons.Default.LocalFlorist
        "wb_sunny" -> Icons.Default.WbSunny
        "brush" -> Icons.Default.Brush
        "palette" -> Icons.Default.Palette
        "eco" -> Icons.Default.Eco
        "nights_stay" -> Icons.Default.NightsStay
        "circle" -> Icons.Default.Circle
        else -> Icons.Default.ShoppingBag
    }
}

@Preview(showBackground = true)
@Composable
fun RewardsShopScreenPreview() {
    MyApplicationTheme {
        RewardsShopContent(
            profile = CatProfile(
                fishCoins = 500,
                pawsCount = 12,
                totalScore = 1250,
                level = 3,
                equippedSkin = "SIAMESE"
            ),
            onBuyOrEquip = {}
        )
    }
}
