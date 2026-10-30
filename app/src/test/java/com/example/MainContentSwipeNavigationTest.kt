package com.example

import android.app.Application
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.core.app.ApplicationProvider
import com.example.notifications.NotificationPreferences
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CatCareViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Garante que as seções do app podem ser alternadas arrastando o conteúdo para os lados,
 * além dos botões do menu inferior.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MainContentSwipeNavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        // Evita abrir o diálogo de permissão de notificações durante o teste.
        NotificationPreferences.markNotificationPermissionRequested(application())

        composeRule.setContent {
            MyApplicationTheme {
                MainContent(viewModel = CatCareViewModel(application()))
            }
        }
    }

    @Test
    fun `arrastar para a esquerda abre a proxima secao`() {
        composeRule.onNodeWithTag("nav_missions").assertIsSelected()

        composeRule.onNodeWithTag("section_pager").performTouchInput { swipeLeft() }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag("prev_month_button").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("nav_calendar").assertIsSelected()
    }

    @Test
    fun `arrastar para a direita volta para a secao anterior`() {
        composeRule.onNodeWithTag("section_pager").performTouchInput { swipeLeft() }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag("prev_month_button").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("nav_calendar").assertIsSelected()

        composeRule.onNodeWithTag("section_pager").performTouchInput { swipeRight() }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag("open_reminders_button").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("nav_missions").assertIsSelected()
    }

    @Test
    fun `menu inferior troca de secao a partir de Lembretes`() {
        composeRule.onNodeWithTag("open_reminders_button").performScrollTo().performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Lembretes & Mensagens").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Lembretes & Mensagens").assertExists()

        composeRule.onNodeWithTag("nav_rewards").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Lojinha de Recompensas").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Lojinha de Recompensas").assertExists()
        composeRule.onNodeWithTag("nav_rewards").assertIsSelected()
    }

    private fun application(): Application = ApplicationProvider.getApplicationContext()
}
