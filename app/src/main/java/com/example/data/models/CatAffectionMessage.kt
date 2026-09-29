package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_affection_messages")
data class CatAffectionMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val text: String,
    val category: String = "INCENTIVO", // CARINHO, INCENTIVO, AUTOESTIMA, SAUDE_MENTAL
    val isCustom: Boolean = false,
    val isEnabled: Boolean = true
) {
    fun formatMessage(catName: String): Pair<String, String> {
        val name = catName.ifBlank { "Mingau" }
        val formattedTitle = title.replace("{gato}", name)
        val formattedText = text.replace("{gato}", name)
        return Pair(formattedTitle, formattedText)
    }
}
