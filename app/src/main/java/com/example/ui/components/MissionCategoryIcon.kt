package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.models.MissionCategory

@Composable
fun MissionCategoryIcon(
    category: MissionCategory,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val icon = when (category) {
        MissionCategory.WATER -> Icons.Default.WaterDrop
        MissionCategory.FOOD -> Icons.Default.Restaurant
        MissionCategory.HYGIENE -> Icons.Default.CleaningServices
        MissionCategory.PLAY -> Icons.Default.SportsEsports
        MissionCategory.GROOMING -> Icons.Default.Brush
        MissionCategory.LOVE -> Icons.Default.Favorite
        MissionCategory.HEALTH -> Icons.Default.MedicalServices
    }

    Icon(
        imageVector = icon,
        contentDescription = category.displayName,
        modifier = modifier,
        tint = tint
    )
}

fun getCategoryIcon(category: MissionCategory): ImageVector {
    return when (category) {
        MissionCategory.WATER -> Icons.Default.WaterDrop
        MissionCategory.FOOD -> Icons.Default.Restaurant
        MissionCategory.HYGIENE -> Icons.Default.CleaningServices
        MissionCategory.PLAY -> Icons.Default.SportsEsports
        MissionCategory.GROOMING -> Icons.Default.Brush
        MissionCategory.LOVE -> Icons.Default.Favorite
        MissionCategory.HEALTH -> Icons.Default.MedicalServices
    }
}
