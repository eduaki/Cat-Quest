package com.example.notifications

import android.content.Context

/**
 * Small persistence layer for settings that must survive process death and
 * reboots, so the reminders can be restored by the background receiver.
 */
object NotificationPreferences {
    private const val PREFS_NAME = "cat_notification_prefs"
    private const val KEY_AFFECTION_INTERVAL_HOURS = "affection_interval_hours"
    private const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
    private const val KEY_SOUND_ENABLED = "sound_enabled"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** 0 means the automatic affection reminders are disabled. */
    fun getAffectionIntervalHours(context: Context): Int =
        prefs(context).getInt(KEY_AFFECTION_INTERVAL_HOURS, 0)

    fun setAffectionIntervalHours(context: Context, hours: Int) {
        prefs(context).edit().putInt(KEY_AFFECTION_INTERVAL_HOURS, hours).apply()
    }

    fun wasNotificationPermissionRequested(context: Context): Boolean =
        prefs(context).getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false)

    fun markNotificationPermissionRequested(context: Context) {
        prefs(context).edit().putBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, true).apply()
    }

    /** Mirrors [com.example.data.models.ReminderConfig.soundEnabled] for background code. */
    fun isSoundEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SOUND_ENABLED, true)

    fun setSoundEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }
}
