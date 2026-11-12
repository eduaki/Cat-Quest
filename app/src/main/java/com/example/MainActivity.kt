package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.notifications.NotificationPreferences
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
    RequestNotificationPermissionOnce()

    var currentScreen by remember { mutableStateOf(AppScreen.MISSIONS) }
    var hideBottomBar by remember { mutableStateOf(false) }

    val bottomTabs = remember {
        listOf(
            AppScreen.MISSIONS,
            AppScreen.CALENDAR,
            AppScreen.CAROUSEL,
            AppScreen.MURAL,
            AppScreen.REWARDS
        )
    }

    val pagerState = rememberPagerState(pageCount = { bottomTabs.size })

    // Mantém o item do menu sincronizado com a página alcançada ao arrastar.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val screen = bottomTabs.getOrNull(page) ?: return@collect
            if (currentScreen != screen) currentScreen = screen
        }
    }

    // Troca de seção pelo menu ou por atalhos internos, posicionando o pager na página certa.
    fun navigateTo(screen: AppScreen) {
        currentScreen = screen

        val page = bottomTabs.indexOf(screen)
        // "Lembretes" não é uma página do pager.
        if (page >= 0) pagerState.requestScrollToPage(page)
    }

    // Retorna para a tela de missões ao apertar voltar
    if (currentScreen != AppScreen.MISSIONS) {
        BackHandler {
            navigateTo(AppScreen.MISSIONS)
        }
    }

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
                            onClick = { navigateTo(screen) },
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
        // Com a câmera aberta a barra inferior some e o conteúdo vai de borda a borda,
        // para o preview ocupar a tela toda (antes sobrava uma faixa de fundo embaixo).
        val contentModifier = if (hideBottomBar) Modifier else Modifier.padding(innerPadding)

        if (currentScreen == AppScreen.SETTINGS) {
            // "Lembretes" não é uma aba do menu inferior, então fica fora do pager.
            RemindersSettingsScreen(
                viewModel = viewModel,
                modifier = contentModifier
            )
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = contentModifier
                    .fillMaxSize()
                    .testTag("section_pager"),
                // A câmera ocupa a tela toda, então o arrasto fica desativado com ela aberta.
                userScrollEnabled = !hideBottomBar
            ) { page ->
                val pageModifier = Modifier.fillMaxSize()

                when (bottomTabs[page]) {
                    AppScreen.MISSIONS -> DailyMissionsScreen(
                        viewModel = viewModel,
                        onOpenShop = { navigateTo(AppScreen.REWARDS) },
                        onOpenReminders = { navigateTo(AppScreen.SETTINGS) },
                        onCameraStateChanged = { hideBottomBar = it },
                        modifier = pageModifier
                    )
                    AppScreen.CALENDAR -> CalendarScreen(
                        viewModel = viewModel,
                        onNavigateToCarousel = { navigateTo(AppScreen.CAROUSEL) },
                        modifier = pageModifier
                    )
                    AppScreen.CAROUSEL -> MonthRecapCarouselScreen(
                        viewModel = viewModel,
                        onNavigateToMural = { navigateTo(AppScreen.MURAL) },
                        onNavigateToMissions = { navigateTo(AppScreen.MISSIONS) },
                        modifier = pageModifier
                    )
                    AppScreen.MURAL -> PhotoMuralScreen(
                        viewModel = viewModel,
                        onNavigateToMissions = { navigateTo(AppScreen.MISSIONS) },
                        modifier = pageModifier
                    )
                    AppScreen.REWARDS -> RewardsShopScreen(
                        viewModel = viewModel,
                        modifier = pageModifier
                    )
                    // SETTINGS é renderizado acima, fora do pager.
                    AppScreen.SETTINGS -> Unit
                }
            }
        }
    }
}

/**
 * Asks for [Manifest.permission.POST_NOTIFICATIONS] on the first launch (Android 13+),
 * so the scheduled background reminders are actually delivered. The user can still
 * re-enable them later from the "Lembretes" screen.
 */
@Composable
private fun RequestNotificationPermissionOnce() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        NotificationPreferences.markNotificationPermissionRequested(context)
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted && !NotificationPreferences.wasNotificationPermissionRequested(context)) {
            NotificationPreferences.markNotificationPermissionRequested(context)
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
