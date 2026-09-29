package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DailyMissionsScreen
import com.example.ui.screens.MonthRecapCarouselScreen
import com.example.ui.screens.PhotoMuralScreen
import com.example.ui.screens.RemindersSettingsScreen
import com.example.ui.screens.RewardsShopScreen
import com.example.ui.theme.CatBrownMuted
import com.example.ui.theme.CatPeachLight
import com.example.ui.theme.CatPeachPrimary
import com.example.ui.theme.CatVanilla
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CatCareViewModel

enum class AppScreen(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    MISSIONS("Missões", Icons.Default.Pets, "nav_missions"),
    CALENDAR("Calendário", Icons.Default.CalendarMonth, "nav_calendar"),
    CAROUSEL("Música", Icons.Default.Audiotrack, "nav_carousel"),
    MURAL("Mural", Icons.Default.PhotoLibrary, "nav_mural"),
    REWARDS("Lojinha", Icons.Default.CardGiftcard, "nav_rewards"),
    SETTINGS("Lembretes", Icons.Default.Notifications, "nav_settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: CatCareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: CatCareViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.MISSIONS) }
    var hideBottomBar by remember { mutableStateOf(false) }

    // Retorna para a tela de missões ao apertar voltar
    if (currentScreen != AppScreen.MISSIONS) {
        BackHandler {
            currentScreen = AppScreen.MISSIONS
        }
    }

    val bottomTabs = listOf(
        AppScreen.MISSIONS,
        AppScreen.CALENDAR,
        AppScreen.CAROUSEL,
        AppScreen.MURAL,
        AppScreen.REWARDS
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CatVanilla,
        bottomBar = {
            AnimatedVisibility(
                visible = !hideBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp
                ) {
                    bottomTabs.forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CatPeachPrimary,
                                selectedTextColor = CatPeachPrimary,
                                indicatorColor = CatPeachLight.copy(alpha = 0.35f),
                                unselectedIconColor = CatBrownMuted,
                                unselectedTextColor = CatBrownMuted
                            ),
                            modifier = Modifier.testTag(screen.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Quando a barra está escondida, o padding inferior deve ser zero para a câmera ser full screen
        val screenModifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            AppScreen.MISSIONS -> {
                DailyMissionsScreen(
                    viewModel = viewModel,
                    onOpenShop = { currentScreen = AppScreen.REWARDS },
                    onOpenReminders = { currentScreen = AppScreen.SETTINGS },
                    onCameraStateChanged = { hideBottomBar = it },
                    modifier = screenModifier
                )
            }
            AppScreen.CALENDAR -> {
                CalendarScreen(
                    viewModel = viewModel,
                    onNavigateToCarousel = { currentScreen = AppScreen.CAROUSEL },
                    modifier = screenModifier
                )
            }
            AppScreen.CAROUSEL -> {
                MonthRecapCarouselScreen(
                    viewModel = viewModel,
                    onNavigateToMural = { currentScreen = AppScreen.MURAL },
                    onNavigateToMissions = { currentScreen = AppScreen.MISSIONS },
                    modifier = screenModifier
                )
            }
            AppScreen.MURAL -> {
                PhotoMuralScreen(
                    viewModel = viewModel,
                    onNavigateToMissions = { currentScreen = AppScreen.MISSIONS },
                    modifier = screenModifier
                )
            }
            AppScreen.REWARDS -> {
                RewardsShopShopScreen(
                    viewModel = viewModel,
                    modifier = screenModifier
                )
            }
            AppScreen.SETTINGS -> {
                RemindersSettingsScreen(
                    viewModel = viewModel,
                    modifier = screenModifier
                )
            }
        }
    }
}

// Pequeno hack caso o nome da classe tenha mudado no seu projeto
@Composable
fun RewardsShopShopScreen(viewModel: CatCareViewModel, modifier: Modifier) {
    RewardsShopScreen(viewModel, modifier)
}
