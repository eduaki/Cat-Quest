package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.models.CatAffectionMessage
import java.util.Calendar
import kotlin.random.Random

object LocalCatNotificationManager {

    const val CHANNEL_ID = "cat_affection_encouragement_channel"
    const val CHANNEL_ID_SILENT = "cat_affection_encouragement_silent_channel"
    const val CHANNEL_NAME = "Carinho & Incentivo Felino"
    const val CHANNEL_DESC = "Mensagens personalizadas de carinho, apoio emocional e incentivo do seu gatinho ao longo do dia!"

    const val ACTION_CAT_AFFECTION = "com.example.notifications.action.CAT_AFFECTION"

    const val REQ_AFFECTION_ALARM = 201
    const val REQ_AFFECTION_ACTION_CUDDLE = 202
    const val REQ_AFFECTION_ACTION_MISSIONS = 203

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val soundChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 150, 100, 150, 80, 200)
                setShowBadge(true)
            }

            val silentChannel = NotificationChannel(
                CHANNEL_ID_SILENT,
                "$CHANNEL_NAME (silencioso)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
                setSound(null, null)
                enableVibration(false)
                setShowBadge(true)
            }

            manager.createNotificationChannels(listOf(soundChannel, silentChannel))
        }
    }

    fun channelIdFor(soundEnabled: Boolean): String =
        if (soundEnabled) CHANNEL_ID else CHANNEL_ID_SILENT

    /**
     * Dispatches a personalized affectionate and encouraging notification.
     */
    fun sendAffectionNotification(
        context: Context,
        title: String,
        message: String,
        catName: String = "Mingau",
        soundEnabled: Boolean = true,
        notificationId: Int = Random.nextInt(2000, 9999)
    ) {
        createNotificationChannel(context)

        val name = catName.ifBlank { "Mingau" }
        val formattedTitle = title.replace("{gato}", name)
        val formattedText = message.replace("{gato}", name)

        // Main Tap Action: Open MainActivity
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_AFFECTION_CLICKED", true)
        }

        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action Intent: Send cuddle / cafuné back
        val cuddleIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_ACTION_CUDDLE_BACK", true)
        }

        val cuddlePendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 10,
            cuddleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = if (soundEnabled) RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) else null

        val builder = NotificationCompat.Builder(context, channelIdFor(soundEnabled))
            .setSmallIcon(R.drawable.ic_notification_cat)
            .setContentTitle(formattedTitle)
            .setContentText(formattedText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(formattedText)
                    .setSummaryText("Recado de Amor do $name")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                R.drawable.ic_notification_cat,
                "Mandar Cafuné",
                cuddlePendingIntent
            )
            .addAction(
                R.drawable.ic_notification_cat,
                "Ver Missões",
                openAppPendingIntent
            )

        // Channel settings win on Android O+, so only override sound on older versions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (soundUri != null) {
                builder.setSound(soundUri)
            } else {
                builder.setSilent(true)
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Enables the automatic affection reminders and schedules the next one.
     *
     * The chosen interval is persisted so the background receiver can restore the
     * alarm after a reboot or an app update.
     */
    fun schedulePeriodicAffectionAlarm(
        context: Context,
        intervalHours: Int,
        catName: String = "Mingau"
    ) {
        if (intervalHours <= 0) {
            disablePeriodicAffectionAlarm(context)
            return
        }
        NotificationPreferences.setAffectionIntervalHours(context, intervalHours)
        scheduleNextAffectionAlarm(context, intervalHours, catName)
    }

    /** Re-arms the affection alarm using the interval stored on the device, if any. */
    fun restorePeriodicAffectionAlarm(context: Context, catName: String = "Mingau") {
        val intervalHours = NotificationPreferences.getAffectionIntervalHours(context)
        if (intervalHours <= 0) {
            cancelAffectionAlarm(context)
            return
        }
        scheduleNextAffectionAlarm(context, intervalHours, catName)
    }

    /** Interval in hours, or 0 when the automatic affection reminders are disabled. */
    fun getAffectionIntervalHours(context: Context): Int =
        NotificationPreferences.getAffectionIntervalHours(context)

    fun disablePeriodicAffectionAlarm(context: Context) {
        NotificationPreferences.setAffectionIntervalHours(context, 0)
        cancelAffectionAlarm(context)
    }

    private fun scheduleNextAffectionAlarm(context: Context, intervalHours: Int, catName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, CatNotificationReceiver::class.java).apply {
            action = ACTION_CAT_AFFECTION
            putExtra(CatNotificationHelper.EXTRA_CAT_NAME, catName)
            putExtra(
                CatNotificationHelper.EXTRA_SOUND_ENABLED,
                NotificationPreferences.isSoundEnabled(context)
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQ_AFFECTION_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (intervalHours * 3600 * 1000L)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelAffectionAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, CatNotificationReceiver::class.java).apply {
            action = ACTION_CAT_AFFECTION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQ_AFFECTION_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
