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
import com.example.data.models.ReminderConfig
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

object CatNotificationHelper {

    const val CHANNEL_ID = "cat_daily_reminders_channel"
    const val CHANNEL_NAME = "Lembretes Fofos & Carinho do Gatinho 🐾"
    const val CHANNEL_DESC = "Lembretes amigáveis para cuidar do seu gato, mensagens de afeto e avisos de missões diárias!"

    const val REQ_MORNING = 101
    const val REQ_AFTERNOON = 102
    const val REQ_EVENING = 103
    const val REQ_SPONTANEOUS_LOVE = 104
    const val REQ_TEST = 999

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Generates a sweet affectionate reminder message depending on tone and time period.
     */
    fun getRandomFriendlyMessage(catName: String, period: String, tone: String): Pair<String, String> {
        val name = catName.ifBlank { "Mingau" }
        return when (tone) {
            "BRINCALHAO" -> when (period) {
                "MORNING" -> Pair(
                    "🐾 Miau! Dia lindo pra caçar!",
                    "Acorda, humano! O $name já fez o alongamento matinal e quer comidinha e água fresca! 🐟✨"
                )
                "AFTERNOON" -> Pair(
                    "🧶 Hora do Play Felino!",
                    "O $name está de olho na varinha e cheio de energia! Que tal 15 minutos de brincadeira? 😻"
                )
                else -> Pair(
                    "⭐ Hora do Soninho & Carinho",
                    "Depois de um dia cheio de sapequices, o $name quer massagem na barriguinha e checagem da caixinha! 🧼"
                )
            }
            "RECLAMAO_FOFO" -> when (period) {
                "MORNING" -> Pair(
                    "📢 Humano, atenção!",
                    "Já são horas! O pote de água do $name precisa ser renovado agora antes que ele empurre algo da mesa! 😹"
                )
                "AFTERNOON" -> Pair(
                    "👀 O conselho dos gatos convocou:",
                    "Cadê a escovação dos pelos sedosos do $name? Venha tirar a foto de prova logo! 📸"
                )
                else -> Pair(
                    "😼 Última chamada pro sachê!",
                    "Não durma sem registrar as missões de hoje com o $name! Seu ronrom depende disso! 🐾"
                )
            }
            else -> when (period) { // CARINHOSO
                "MORNING" -> Pair(
                    "💖 Bom dia com ronrons!",
                    "O $name acordou ronronando e te esperando com água limpinha e sachê gostoso! Bom dia, tutor amoroso! 🐾"
                )
                "AFTERNOON" -> Pair(
                    "🌸 Pausa para o seu peludinho!",
                    "Uma piscadinha lenta do $name pra você. Que tal escovar os pelos e tirar uma fotinho fofa? ✨"
                )
                else -> Pair(
                    "🌙 Boa noite com muito chamego!",
                    "O $name já está se aninhando. Hora da checagem das missões do dia e muito cafuné! 💤"
                )
            }
        }
    }

    /**
     * Generates a spontaneous, pure-love affectionate message from the cat.
     */
    fun getRandomAffectionateMessage(catName: String): Pair<String, String> {
        val name = catName.ifBlank { "Mingau" }
        val messages = listOf(
            Pair(
                "💖 Mensagem Secreta de Amor",
                "Miau! O $name acabou de dar uma piscadinha lenta para você. Sabia que em 'gatês' isso significa 'eu te amo muito'? 🥰"
            ),
            Pair(
                "😻 Ronrom a todo volume!",
                "Detectamos uma alta concentração de carinho! O $name está guardando muito ronrom pra quando você chegar! 🐾"
            ),
            Pair(
                "🌸 Gratidão de Bigodinho",
                "Obrigado por ser o humano mais incrível do mundo pro $name. Você cuida tão bem de mim! 😽✨"
            ),
            Pair(
                "🧶 Pensando em você...",
                "O $name está esticando as patinhas no tapete e pensando: 'cadê meu humano favorito pra brincar com a bolinha?' 🐾"
            ),
            Pair(
                "✨ Pedido Especial de Cafuné",
                "Passando aqui só pra lembrar que você merece um dia maravilhoso, com direito a cafuné no queixo do $name! 💖"
            )
        )
        return messages[Random.nextInt(messages.size)]
    }

    fun sendFriendlyNotification(
        context: Context,
        title: String,
        message: String,
        soundEnabled: Boolean = true,
        notificationId: Int = Random.nextInt(1000, 9999)
    ) {
        createNotificationChannel(context)

        // Main Tap Action: Open MainActivity
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NOTIFICATION_CLICKED", true)
        }

        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = if (soundEnabled) RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) else null

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
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
     * Schedules all reminder alarms according to the user's saved configuration.
     */
    fun scheduleAlarmsFromConfig(context: Context, config: ReminderConfig, catName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Morning Slot
        if (config.morningEnabled) {
            scheduleDailyAlarm(
                context = context,
                alarmManager = alarmManager,
                requestCode = REQ_MORNING,
                hour = config.morningHour,
                minute = config.morningMinute,
                period = "MORNING",
                catName = catName,
                tone = config.friendlyTone
            )
        } else {
            cancelAlarm(context, alarmManager, REQ_MORNING)
        }

        // Afternoon Slot
        if (config.afternoonEnabled) {
            scheduleDailyAlarm(
                context = context,
                alarmManager = alarmManager,
                requestCode = REQ_AFTERNOON,
                hour = config.afternoonHour,
                minute = config.afternoonMinute,
                period = "AFTERNOON",
                catName = catName,
                tone = config.friendlyTone
            )
        } else {
            cancelAlarm(context, alarmManager, REQ_AFTERNOON)
        }

        // Evening Slot
        if (config.eveningEnabled) {
            scheduleDailyAlarm(
                context = context,
                alarmManager = alarmManager,
                requestCode = REQ_EVENING,
                hour = config.eveningHour,
                minute = config.eveningMinute,
                period = "EVENING",
                catName = catName,
                tone = config.friendlyTone
            )
        } else {
            cancelAlarm(context, alarmManager, REQ_EVENING)
        }
    }

    private fun scheduleDailyAlarm(
        context: Context,
        alarmManager: AlarmManager,
        requestCode: Int,
        hour: Int,
        minute: Int,
        period: String,
        catName: String,
        tone: String
    ) {
        val intent = Intent(context, CatNotificationReceiver::class.java).apply {
            putExtra("EXTRA_PERIOD", period)
            putExtra("EXTRA_CAT_NAME", catName)
            putExtra("EXTRA_TONE", tone)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If the time already passed today, set for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            // Use setAndAllowWhileIdle so reminders fire even when device is in Doze Mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // In case of permission restriction on Android 12+, fallback to inexact set
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelAlarm(context: Context, alarmManager: AlarmManager, requestCode: Int) {
        val intent = Intent(context, CatNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Helper to format active schedule times for the UI
     */
    fun getFormattedActiveSchedules(config: ReminderConfig): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        if (config.morningEnabled) {
            list.add(Pair("Manhã 🌅", String.format(Locale.getDefault(), "%02d:%02d", config.morningHour, config.morningMinute)))
        }
        if (config.afternoonEnabled) {
            list.add(Pair("Tarde ☀️", String.format(Locale.getDefault(), "%02d:%02d", config.afternoonHour, config.afternoonMinute)))
        }
        if (config.eveningEnabled) {
            list.add(Pair("Noite 🌙", String.format(Locale.getDefault(), "%02d:%02d", config.eveningHour, config.eveningMinute)))
        }
        return list
    }
}
