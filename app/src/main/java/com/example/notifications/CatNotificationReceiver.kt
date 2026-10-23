package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.CatCareApplication
import com.example.data.models.ReminderConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class CatNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        // The device rebooted, the app was updated or the clock/time zone changed:
        // pending alarms are gone, so rebuild the whole schedule from the database.
        if (isRescheduleAction(intent.action)) {
            rescheduleAllReminders(context)
            return
        }

        when (intent.action) {
            CatNotificationHelper.ACTION_CAT_REMINDER -> handleDailyReminder(context, intent)
            LocalCatNotificationManager.ACTION_CAT_AFFECTION -> handleAffectionReminder(context, intent)
        }
    }

    private fun isRescheduleAction(action: String?): Boolean = when (action) {
        Intent.ACTION_BOOT_COMPLETED,
        Intent.ACTION_MY_PACKAGE_REPLACED,
        Intent.ACTION_TIME_CHANGED,
        Intent.ACTION_TIMEZONE_CHANGED,
        ACTION_QUICKBOOT_POWERON -> true
        else -> false
    }

    /**
     * Posts the reminder and immediately re-arms the same slot for the next day.
     *
     * The data needed to re-arm travels inside the [Intent], so this path never touches
     * the database and keeps working when the process is recreated just to deliver the
     * broadcast (app swiped away, device idle, etc.).
     */
    private fun handleDailyReminder(context: Context, intent: Intent) {
        val catName = intent.getStringExtra(CatNotificationHelper.EXTRA_CAT_NAME) ?: DEFAULT_CAT_NAME
        val period = intent.getStringExtra(CatNotificationHelper.EXTRA_PERIOD) ?: "MORNING"
        val tone = intent.getStringExtra(CatNotificationHelper.EXTRA_TONE) ?: "CARINHOSO"
        val soundEnabled = intent.getBooleanExtra(CatNotificationHelper.EXTRA_SOUND_ENABLED, true)

        val (title, message) = CatNotificationHelper.getRandomFriendlyMessage(catName, period, tone)
        CatNotificationHelper.sendFriendlyNotification(
            context = context,
            title = title,
            message = message,
            soundEnabled = soundEnabled
        )

        val hour = intent.getIntExtra(CatNotificationHelper.EXTRA_HOUR, -1)
        val minute = intent.getIntExtra(CatNotificationHelper.EXTRA_MINUTE, -1)
        if (hour in 0..23 && minute in 0..59) {
            CatNotificationHelper.scheduleDailyAlarm(
                context = context,
                requestCode = intent.getIntExtra(
                    CatNotificationHelper.EXTRA_REQUEST_CODE,
                    CatNotificationHelper.requestCodeForPeriod(period)
                ),
                hour = hour,
                minute = minute,
                period = period,
                catName = catName,
                tone = tone,
                soundEnabled = soundEnabled
            )
        } else {
            // Alarms created by older app versions did not carry the schedule details.
            rescheduleAllReminders(context)
        }
    }

    private fun handleAffectionReminder(context: Context, intent: Intent) {
        val catName = intent.getStringExtra(CatNotificationHelper.EXTRA_CAT_NAME) ?: DEFAULT_CAT_NAME
        val soundEnabled = intent.getBooleanExtra(CatNotificationHelper.EXTRA_SOUND_ENABLED, true)

        val (title, message) = loadAffectionMessage(context, catName)
        LocalCatNotificationManager.sendAffectionNotification(
            context = context,
            title = title,
            message = message,
            catName = catName,
            soundEnabled = soundEnabled
        )

        LocalCatNotificationManager.restorePeriodicAffectionAlarm(context, catName)
    }

    private fun loadAffectionMessage(context: Context, catName: String): Pair<String, String> {
        val messages = runCatching {
            val app = context.applicationContext as? CatCareApplication
                ?: return@runCatching emptyList()
            runBlocking(Dispatchers.IO) {
                app.database.catAffectionMessageDao().getEnabledMessagesSync()
            }
        }.getOrDefault(emptyList())

        return if (messages.isNotEmpty()) {
            messages[Random.nextInt(messages.size)].formatMessage(catName)
        } else {
            CatNotificationHelper.getRandomAffectionateMessage(catName)
        }
    }

    private fun rescheduleAllReminders(context: Context) {
        val app = context.applicationContext as? CatCareApplication ?: return

        var config: ReminderConfig? = null
        var profileName: String? = null
        runCatching {
            runBlocking(Dispatchers.IO) {
                config = app.database.reminderConfigDao().getConfigSync()
                profileName = app.database.catProfileDao().getProfileSync()?.name
            }
        }.onFailure { it.printStackTrace() }

        val catName = profileName ?: DEFAULT_CAT_NAME
        CatNotificationHelper.scheduleAlarmsFromConfig(context, config ?: ReminderConfig(), catName)
        LocalCatNotificationManager.restorePeriodicAffectionAlarm(context, catName)
    }

    private companion object {
        const val DEFAULT_CAT_NAME = "Mingau"
        const val ACTION_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
    }
}
