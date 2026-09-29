package com.example.utils

import com.example.data.models.CatMission
import com.example.data.models.MissionCategory

data class ScoreCalculationResult(
    val basePoints: Int,
    val streakMultiplier: Float,
    val streakBonusPoints: Int,
    val totalPoints: Int,
    val fishCoinsAwarded: Int,
    val isLevelUp: Boolean,
    val oldLevel: Int,
    val newLevel: Int,
    val levelTitle: String
)

object ScoreCalculator {

    private val levelThresholds = listOf(
        Pair(0, "Tutor Iniciante 🐾"),
        Pair(200, "Amigo dos Bigodes 🐱"),
        Pair(500, "Cuidador Dedicado ✨"),
        Pair(1000, "Encantador de Gatos 💖"),
        Pair(1800, "Mestre do Ronrom 😻"),
        Pair(3000, "Lorde Protetor Felino 👑")
    )

    fun calculateLevel(totalScore: Int): Int {
        for (i in levelThresholds.indices.reversed()) {
            if (totalScore >= levelThresholds[i].first) {
                return i + 1
            }
        }
        return 1
    }

    fun getLevelTitle(level: Int): String {
        val index = (level - 1).coerceIn(0, levelThresholds.size - 1)
        return levelThresholds[index].second
    }

    fun getLevelProgress(totalScore: Int): Float {
        val currentLevel = calculateLevel(totalScore)
        if (currentLevel >= levelThresholds.size) return 1f

        val currentMin = levelThresholds[currentLevel - 1].first
        val nextMin = levelThresholds[currentLevel].first
        val range = nextMin - currentMin
        if (range <= 0) return 1f

        return ((totalScore - currentMin).toFloat() / range).coerceIn(0f, 1f)
    }

    fun getNextLevelInfo(totalScore: Int): Pair<Int, Int> {
        val currentLevel = calculateLevel(totalScore)
        if (currentLevel >= levelThresholds.size) {
            val maxScore = levelThresholds.last().first
            return Pair(totalScore, maxScore)
        }
        val nextThreshold = levelThresholds[currentLevel].first
        return Pair(totalScore, nextThreshold)
    }

    /**
     * Calculates score and rewards for completing a mission with photo proof.
     */
    fun calculateMissionReward(
        mission: CatMission,
        streakDays: Int,
        currentTotalScore: Int
    ): ScoreCalculationResult {
        val category = mission.getCategoryEnum()
        val basePoints = category.basePoints
        val baseFish = category.fishReward

        // Streak Multiplier bonus
        val multiplier = when {
            streakDays >= 14 -> 2.0f // Double score for 2+ weeks streak!
            streakDays >= 7 -> 1.5f // +50% for 1+ week
            streakDays >= 3 -> 1.2f // +20% for 3+ days
            else -> 1.0f
        }

        val streakBonus = ((basePoints * multiplier) - basePoints).toInt()
        val totalPoints = basePoints + streakBonus

        // Fish coins also get a streak bonus if >= 3 days
        val bonusFish = if (streakDays >= 3) (streakDays * 2).coerceAtMost(20) else 0
        val totalFish = baseFish + bonusFish

        val oldLevel = calculateLevel(currentTotalScore)
        val newScore = currentTotalScore + totalPoints
        val newLevel = calculateLevel(newScore)
        val isLevelUp = newLevel > oldLevel

        return ScoreCalculationResult(
            basePoints = basePoints,
            streakMultiplier = multiplier,
            streakBonusPoints = streakBonus,
            totalPoints = totalPoints,
            fishCoinsAwarded = totalFish,
            isLevelUp = isLevelUp,
            oldLevel = oldLevel,
            newLevel = newLevel,
            levelTitle = getLevelTitle(newLevel)
        )
    }
}
