package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.CatCareApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class CatNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        val app = context.applicationContext as? CatCareApplication

        // 1. Reschedule alarms on device boot
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (app == null) return
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    val config = app.database.reminderConfigDao().getConfigSync()
                    val profile = app.database.catProfileDao().getProfileSync()
                    val catName = profile?.name ?: "Mingau"
                    if (config != null) {
                        CatNotificationHelper.scheduleAlarmsFromConfig(
                            context = context,
                            config = config,
                            catName = catName
                        )
                        LocalCatNotificationManager.schedulePeriodicAffectionAlarm(
                            context = context,
                            intervalHours = 4,
                            catName = catName
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return
        }

        // 2. Alarm fired -> Extract parameters
        val catName = intent.getStringExtra("EXTRA_CAT_NAME") ?: "Mingau"
        val period = intent.getStringExtra("EXTRA_PERIOD") ?: "MORNING"
        val tone = intent.getStringExtra("EXTRA_TONE") ?: "CARINHOSO"
        val isLoveMessage = intent.getBooleanExtra("EXTRA_IS_LOVE_MESSAGE", false)

        if (isLoveMessage) {
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    val messages = app?.database?.catAffectionMessageDao()?.getEnabledMessagesSync() ?: emptyList()
                    val (title, text) = if (messages.isNotEmpty()) {
                        val chosen = messages[Random.nextInt(messages.size)]
                        chosen.formatMessage(catName)
                    } else {
                        CatNotificationHelper.getRandomAffectionateMessage(catName)
                    }

                    LocalCatNotificationManager.sendAffectionNotification(
                        context = context,
                        title = title,
                        message = text,
                        catName = catName
                    )

                    // Re-schedule next periodic affection message
                    LocalCatNotificationManager.schedulePeriodicAffectionAlarm(
                        context = context,
                        intervalHours = 4,
                        catName = catName
                    )
                } catch (e: Exception) {
                    val (title, text) = CatNotificationHelper.getRandomAffectionateMessage(catName)
                    LocalCatNotificationManager.sendAffectionNotification(
                        context = context,
                        title = title,
                        message = text,
                        catName = catName
                    )
                }
            }
            return
        }

        // Standard Scheduled Daily Reminder
        val (title, message) = CatNotificationHelper.getRandomFriendlyMessage(catName, period, tone)

        CatNotificationHelper.sendFriendlyNotification(
            context = context,
            title = title,
            message = message
        )

        // Re-arm standard alarm for next day
        if (app != null) {
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    val config = app.database.reminderConfigDao().getConfigSync()
                    if (config != null) {
                        CatNotificationHelper.scheduleAlarmsFromConfig(
                            context = context,
                            config = config,
                            catName = catName
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
