package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_config")
data class ReminderConfig(
    @PrimaryKey
    val id: Int = 1,
    val morningEnabled: Boolean = true,
    val morningHour: Int = 8,
    val morningMinute: Int = 30,
    val afternoonEnabled: Boolean = true,
    val afternoonHour: Int = 14,
    val afternoonMinute: Int = 0,
    val eveningEnabled: Boolean = true,
    val eveningHour: Int = 20,
    val eveningMinute: Int = 0,
    val soundEnabled: Boolean = true,
    val friendlyTone: String = "CARINHOSO", // CARINHOSO, BRINCALHAO, RECLAMAO_FOFO
    val customMessage: String = "Não esqueça de cuidar do seu gatinho hoje!"
)
