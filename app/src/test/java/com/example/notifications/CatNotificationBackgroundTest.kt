package com.example.notifications

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.ReminderConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], application = Application::class)
class CatNotificationBackgroundTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Test
    fun `schedules one alarm per enabled slot`() {
        val config = ReminderConfig(
            morningEnabled = true,
            afternoonEnabled = false,
            eveningEnabled = true
        )

        CatNotificationHelper.scheduleAlarmsFromConfig(context, config, "Mingau")

        assertEquals(2, shadowOf(alarmManager).scheduledAlarms.size)
    }

    @Test
    fun `daily reminder posts a notification and re-arms the same slot`() {
        CatNotificationReceiver().onReceive(context, dailyReminderIntent())

        assertEquals(1, shadowOf(notificationManager).size())

        val alarms = shadowOf(alarmManager).scheduledAlarms
        assertEquals(1, alarms.size)
        assertEquals(
            CatNotificationHelper.REQ_MORNING,
            shadowOf(alarms.first().operation).requestCode
        )
        assertTrue(alarms.first().triggerAtTime > System.currentTimeMillis())
    }

    @Test
    fun `daily reminder respects the silent setting`() {
        CatNotificationReceiver().onReceive(
            context,
            dailyReminderIntent().putExtra(CatNotificationHelper.EXTRA_SOUND_ENABLED, false)
        )

        val notification = shadowOf(notificationManager).allNotifications.first()
        assertEquals(CatNotificationHelper.CHANNEL_ID_SILENT, notification.channelId)
    }

    @Test
    fun `ignores broadcasts that are not app reminders`() {
        CatNotificationReceiver().onReceive(context, Intent("com.example.SOMETHING_ELSE"))

        assertEquals(0, shadowOf(notificationManager).size())
        assertEquals(0, shadowOf(alarmManager).scheduledAlarms.size)
    }

    @Test
    fun `every tone and period produces a message mentioning the cat`() {
        listOf("CARINHOSO", "BRINCALHAO", "RECLAMAO_FOFO").forEach { tone ->
            listOf("MORNING", "AFTERNOON", "EVENING").forEach { period ->
                val (title, message) = CatNotificationHelper.getRandomFriendlyMessage("Mingau", period, tone)
                assertTrue(title.isNotBlank())
                assertTrue(message.contains("Mingau"))
            }
        }
    }

    @Test
    fun `next trigger time is always in the future and within a day`() {
        val now = System.currentTimeMillis()
        val trigger = CatNotificationHelper.nextTriggerTime(8, 30)

        assertTrue(trigger > now)
        assertTrue(trigger <= now + AlarmManager.INTERVAL_DAY + 60_000)
    }

    private fun dailyReminderIntent(): Intent =
        Intent(context, CatNotificationReceiver::class.java).apply {
            action = CatNotificationHelper.ACTION_CAT_REMINDER
            putExtra(CatNotificationHelper.EXTRA_CAT_NAME, "Mingau")
            putExtra(CatNotificationHelper.EXTRA_PERIOD, "MORNING")
            putExtra(CatNotificationHelper.EXTRA_TONE, "CARINHOSO")
            putExtra(CatNotificationHelper.EXTRA_SOUND_ENABLED, true)
            putExtra(CatNotificationHelper.EXTRA_HOUR, 8)
            putExtra(CatNotificationHelper.EXTRA_MINUTE, 30)
            putExtra(CatNotificationHelper.EXTRA_REQUEST_CODE, CatNotificationHelper.REQ_MORNING)
        }
}
