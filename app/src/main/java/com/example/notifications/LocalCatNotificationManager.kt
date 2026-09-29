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
    const val CHANNEL_NAME = "Carinho & Incentivo Felino 🐾💖"
    const val CHANNEL_DESC = "Mensagens personalizadas de carinho, apoio emocional e incentivo do seu gatinho ao longo do dia!"

    const val REQ_AFFECTION_ALARM = 201
    const val REQ_AFFECTION_ACTION_CUDDLE = 202
    const val REQ_AFFECTION_ACTION_MISSIONS = 203

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 150, 100, 150, 80, 200)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

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

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(formattedTitle)
            .setContentText(formattedText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(formattedText)
                    .setSummaryText("Recado de Amor do $name 🐾")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Mandar Cafuné 💖",
                cuddlePendingIntent
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Ver Missões 🐾",
                openAppPendingIntent
            )

        if (soundUri != null) {
            builder.setSound(soundUri)
        } else {
            builder.setSilent(true)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Schedules a local periodic affection alarm (e.g. every X hours throughout the daytime).
     */
    fun schedulePeriodicAffectionAlarm(
        context: Context,
        intervalHours: Int = 4,
        catName: String = "Mingau"
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, CatNotificationReceiver::class.java).apply {
            putExtra("EXTRA_IS_LOVE_MESSAGE", true)
            putExtra("EXTRA_CAT_NAME", catName)
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
        val intent = Intent(context, CatNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQ_AFFECTION_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
