package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MissionCategory(
    val displayName: String,
    val iconEmoji: String,
    val defaultTitle: String,
    val defaultDescription: String,
    val suggestionPrompt: String,
    val basePoints: Int,
    val fishReward: Int
) {
    WATER(
        displayName = "Água Fresca",
        iconEmoji = "💧",
        defaultTitle = "Trocar Água Fresquinha",
        defaultDescription = "Lavar o potinho e encher com água limpa e fresca.",
        suggestionPrompt = "Tire foto do gatinho bebendo água ou do potinho brilhando!",
        basePoints = 40,
        fishReward = 20
    ),
    FOOD(
        displayName = "Alimentação & Sachê",
        iconEmoji = "🐟",
        defaultTitle = "Refeição do Bichano",
        defaultDescription = "Servir a ração balanceada ou um sachê delicioso.",
        suggestionPrompt = "Tire foto do pratinho ou do gatinho saboreando o rango!",
        basePoints = 40,
        fishReward = 20
    ),
    HYGIENE(
        displayName = "Caixa de Areia",
        iconEmoji = "🧼",
        defaultTitle = "Limpar a Caixinha de Areia",
        defaultDescription = "Retirar torrões e manter o banheiro do felino impecável.",
        suggestionPrompt = "Tire foto da caixinha 100% limpinha e arejada!",
        basePoints = 50,
        fishReward = 25
    ),
    PLAY(
        displayName = "Brincadeira Ativa",
        iconEmoji = "🧶",
        defaultTitle = "Sessão de Brincadeira (15 min)",
        defaultDescription = "Gastar energia com varinha, bolinha de papel ou laser.",
        suggestionPrompt = "Fotografe o gatinho em ação caçando o brinquedo!",
        basePoints = 45,
        fishReward = 25
    ),
    GROOMING(
        displayName = "Escovação & Higiene",
        iconEmoji = "✨",
        defaultTitle = "Escovar os Pelos Macios",
        defaultDescription = "Remover pelos mortos e evitar bolas de pelo no estômago.",
        suggestionPrompt = "Tire foto da escovinha com os pelos ou do pelo sedoso!",
        basePoints = 45,
        fishReward = 25
    ),
    LOVE(
        displayName = "Momento Carinho & Ronrom",
        iconEmoji = "💖",
        defaultTitle = "Checagem de Ronrom & Mimos",
        defaultDescription = "Fazer cafuné no queixo, orelhas e checar se está tudo bem.",
        suggestionPrompt = "Selfie fofa com seu gatinho relaxado e ronronando!",
        basePoints = 50,
        fishReward = 30
    ),
    HEALTH(
        displayName = "Saúde & Checkup",
        iconEmoji = "🩺",
        defaultTitle = "Checagem Geral & Dentes",
        defaultDescription = "Checar olhos, orelhinhas limpas e hidratação.",
        suggestionPrompt = "Tire foto do gatinho com pose de modelo saudável!",
        basePoints = 60,
        fishReward = 35
    )
}

@Entity(tableName = "cat_missions")
data class CatMission(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format: YYYY-MM-DD (e.g. 2026-09-28)
    val title: String,
    val description: String,
    val category: String, // Category name
    val isCompleted: Boolean = false,
    val photoPath: String? = null,
    val completedAt: Long? = null,
    val catComment: String = "Miau! Aprovado pelo bichano! 🐾",
    val catMood: String = "😸",
    val isCustom: Boolean = false,
    val pointsEarned: Int = 0,
    val fishEarned: Int = 0
) {
    fun getCategoryEnum(): MissionCategory {
        return try {
            MissionCategory.valueOf(category)
        } catch (e: Exception) {
            MissionCategory.LOVE
        }
    }
}
