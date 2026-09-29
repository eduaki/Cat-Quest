package com.example.data.repository

import com.example.data.db.CatAffectionMessageDao
import com.example.data.db.CatMissionDao
import com.example.data.db.CatProfileDao
import com.example.data.db.ReminderConfigDao
import com.example.data.models.CatAffectionMessage
import com.example.data.models.CatMission
import com.example.data.models.CatProfile
import com.example.data.models.MissionCategory
import com.example.data.models.ReminderConfig
import com.example.data.models.RewardType
import com.example.data.models.ShopRewardItem
import com.example.utils.ScoreCalculationResult
import com.example.utils.ScoreCalculator
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class CatRepository(
    private val missionDao: CatMissionDao,
    private val profileDao: CatProfileDao,
    private val reminderDao: ReminderConfigDao,
    private val affectionDao: CatAffectionMessageDao
) {
    val profile: Flow<CatProfile?> = profileDao.getProfile()
    val reminderConfig: Flow<ReminderConfig?> = reminderDao.getConfig()
    val datesWithCompletedMissions: Flow<List<String>> = missionDao.getDatesWithCompletedMissions()
    val allPhotos: Flow<List<CatMission>> = missionDao.getAllPhotos()
    val allAffectionMessages: Flow<List<CatAffectionMessage>> = affectionDao.getAllMessages()

    fun getMissionsForDate(date: String): Flow<List<CatMission>> {
        return missionDao.getMissionsForDate(date)
    }

    fun getMissionsForMonth(monthPattern: String): Flow<List<CatMission>> {
        return missionDao.getMissionsForMonth(monthPattern)
    }

    fun getPhotosForMonth(monthPattern: String): Flow<List<CatMission>> {
        return missionDao.getPhotosForMonth(monthPattern)
    }

    fun getMissionsBetween(startDate: String, endDate: String): Flow<List<CatMission>> {
        return missionDao.getMissionsBetween(startDate, endDate)
    }

    suspend fun ensureDefaultMissionsForDate(date: String) {
        val count = missionDao.countMissionsForDate(date)
        if (count == 0) {
            val defaults = listOf(
                CatMission(
                    date = date,
                    title = MissionCategory.WATER.defaultTitle,
                    description = MissionCategory.WATER.defaultDescription,
                    category = MissionCategory.WATER.name
                ),
                CatMission(
                    date = date,
                    title = MissionCategory.FOOD.defaultTitle,
                    description = MissionCategory.FOOD.defaultDescription,
                    category = MissionCategory.FOOD.name
                ),
                CatMission(
                    date = date,
                    title = MissionCategory.HYGIENE.defaultTitle,
                    description = MissionCategory.HYGIENE.defaultDescription,
                    category = MissionCategory.HYGIENE.name
                ),
                CatMission(
                    date = date,
                    title = MissionCategory.PLAY.defaultTitle,
                    description = MissionCategory.PLAY.defaultDescription,
                    category = MissionCategory.PLAY.name
                ),
                CatMission(
                    date = date,
                    title = MissionCategory.GROOMING.defaultTitle,
                    description = MissionCategory.GROOMING.defaultDescription,
                    category = MissionCategory.GROOMING.name
                ),
                CatMission(
                    date = date,
                    title = MissionCategory.LOVE.defaultTitle,
                    description = MissionCategory.LOVE.defaultDescription,
                    category = MissionCategory.LOVE.name
                )
            )
            missionDao.insertMissions(defaults)
        }
    }

    suspend fun completeMissionWithPhoto(
        missionId: Long,
        photoPath: String,
        customComment: String? = null,
        streakDays: Int = 0
    ): ScoreCalculationResult? {
        val mission = missionDao.getMissionById(missionId) ?: return null
        val currentProfile = profileDao.getProfileSync() ?: CatProfile()

        val scoreResult = ScoreCalculator.calculateMissionReward(
            mission = mission,
            streakDays = streakDays,
            currentTotalScore = currentProfile.totalScore
        )

        val comments = listOf(
            "Miau! Humano exemplar, ronronando alto! 🐾",
            "Aprovadíssimo pelo conselho felino! 😸",
            "Foto linda! Ganhou +${scoreResult.totalPoints} pontos e +${scoreResult.fishCoinsAwarded} peixinhos! 🐟✨",
            "Bigodinhos felizes e barriguinha cheia! 🥰",
            "Miau miau! O melhor tutor do mundo! 💖",
            "Ronrom ativado na potência máxima! 😽"
        )
        val moods = listOf("😻", "😸", "😽", "🥰", "😺")
        val comment = customComment?.takeIf { it.isNotBlank() } ?: comments[Random.nextInt(comments.size)]
        val mood = moods[Random.nextInt(moods.size)]

        missionDao.completeMission(
            id = missionId,
            isCompleted = true,
            photoPath = photoPath,
            completedAt = System.currentTimeMillis(),
            comment = comment,
            mood = mood,
            points = scoreResult.totalPoints,
            fish = scoreResult.fishCoinsAwarded
        )

        // Award points and fish to CatProfile
        val updatedProfile = currentProfile.copy(
            totalScore = currentProfile.totalScore + scoreResult.totalPoints,
            level = scoreResult.newLevel,
            fishCoins = currentProfile.fishCoins + scoreResult.fishCoinsAwarded,
            pawsCount = currentProfile.pawsCount + 1
        )
        profileDao.insertOrUpdate(updatedProfile)

        return scoreResult
    }

    suspend fun undoCompleteMission(missionId: Long) {
        val mission = missionDao.getMissionById(missionId)
        val currentProfile = profileDao.getProfileSync() ?: CatProfile()

        if (mission != null && mission.isCompleted) {
            val revertScore = (currentProfile.totalScore - mission.pointsEarned).coerceAtLeast(0)
            val revertCoins = (currentProfile.fishCoins - mission.fishEarned).coerceAtLeast(0)
            val revertPaws = (currentProfile.pawsCount - 1).coerceAtLeast(0)
            val revertLevel = ScoreCalculator.calculateLevel(revertScore)

            profileDao.insertOrUpdate(
                currentProfile.copy(
                    totalScore = revertScore,
                    fishCoins = revertCoins,
                    pawsCount = revertPaws,
                    level = revertLevel
                )
            )
        }

        missionDao.completeMission(
            id = missionId,
            isCompleted = false,
            photoPath = null,
            completedAt = null,
            comment = "Aguardando cumprimento...",
            mood = "😺",
            points = 0,
            fish = 0
        )
    }

    suspend fun addCustomMission(date: String, title: String, description: String, category: MissionCategory) {
        val mission = CatMission(
            date = date,
            title = title,
            description = description,
            category = category.name,
            isCustom = true
        )
        missionDao.insertMission(mission)
    }

    suspend fun deleteMission(mission: CatMission) {
        missionDao.deleteMission(mission)
    }

    suspend fun updateProfile(profile: CatProfile) {
        profileDao.insertOrUpdate(profile)
    }

    suspend fun buyAndEquipReward(item: ShopRewardItem): Boolean {
        val current = profileDao.getProfileSync() ?: CatProfile()
        val isAlreadyUnlocked = current.hasUnlocked(item.id)

        if (!isAlreadyUnlocked) {
            if (current.fishCoins < item.price) {
                return false // Not enough coins
            }
        }

        val remainingCoins = if (isAlreadyUnlocked) current.fishCoins else current.fishCoins - item.price
        val updatedUnlocked = if (isAlreadyUnlocked) current.unlockedItems else current.withUnlocked(item.id)

        val updatedProfile = when (item.type) {
            RewardType.ACCESSORY -> current.copy(
                fishCoins = remainingCoins,
                unlockedItems = updatedUnlocked,
                equippedAccessory = item.id
            )
            RewardType.SKIN -> current.copy(
                fishCoins = remainingCoins,
                unlockedItems = updatedUnlocked,
                equippedSkin = item.id
            )
            RewardType.THEME -> current.copy(
                fishCoins = remainingCoins,
                unlockedItems = updatedUnlocked,
                equippedTheme = item.id
            )
        }
        profileDao.insertOrUpdate(updatedProfile)
        return true
    }

    suspend fun equipReward(item: ShopRewardItem) {
        val current = profileDao.getProfileSync() ?: CatProfile()
        val updatedProfile = when (item.type) {
            RewardType.ACCESSORY -> current.copy(equippedAccessory = item.id)
            RewardType.SKIN -> current.copy(equippedSkin = item.id)
            RewardType.THEME -> current.copy(equippedTheme = item.id)
        }
        profileDao.insertOrUpdate(updatedProfile)
    }

    suspend fun updateReminderConfig(config: ReminderConfig) {
        reminderDao.insertOrUpdate(config)
    }

    suspend fun insertSampleCompletedMission(
        date: String,
        category: MissionCategory,
        photoPath: String,
        catComment: String
    ) {
        val mission = CatMission(
            date = date,
            title = category.defaultTitle,
            description = category.defaultDescription,
            category = category.name,
            isCompleted = true,
            photoPath = photoPath,
            completedAt = System.currentTimeMillis(),
            catComment = catComment,
            pointsEarned = 100,
            fishEarned = 15
        )
        missionDao.insertMission(mission)
    }

    suspend fun addAffectionMessage(title: String, text: String, category: String = "INCENTIVO"): Long {
        val msg = CatAffectionMessage(
            title = title,
            text = text,
            category = category,
            isCustom = true,
            isEnabled = true
        )
        return affectionDao.insertMessage(msg)
    }

    suspend fun updateAffectionMessage(message: CatAffectionMessage) {
        affectionDao.updateMessage(message)
    }

    suspend fun deleteAffectionMessage(message: CatAffectionMessage) {
        affectionDao.deleteMessage(message)
    }

    suspend fun getEnabledAffectionMessages(): List<CatAffectionMessage> {
        return affectionDao.getEnabledMessagesSync()
    }

    suspend fun initDefaultsIfFirstRun() {
        val existingProfile = profileDao.getProfileSync()
        if (existingProfile == null) {
            profileDao.insertOrUpdate(CatProfile())
        }
        val existingConfig = reminderDao.getConfigSync()
        if (existingConfig == null) {
            reminderDao.insertOrUpdate(ReminderConfig())
        }

        if (affectionDao.countMessages() == 0) {
            val defaults = listOf(
                CatAffectionMessage(
                    title = "🐾 Força, meu humano favorito!",
                    text = "O {gato} está torcendo por você em cada desafio de hoje! Você é capaz de tudo, não se esqueça!",
                    category = "INCENTIVO",
                    isCustom = false
                ),
                CatAffectionMessage(
                    title = "💖 Pausa para Respirar",
                    text = "Respira fundo... Solte os ombros. O {gato} está deitadinho no seu colo mentalmente agora, ronronando bem quentinho.",
                    category = "SAUDE_MENTAL",
                    isCustom = false
                ),
                CatAffectionMessage(
                    title = "✨ Piscadinha Lenta de Amor",
                    text = "Sabia que quando um gato pisca devagar para você, é a declaração de amor mais pura que existe? Pois o {gato} acabou de piscar pra você! 🥰",
                    category = "CARINHO",
                    isCustom = false
                ),
                CatAffectionMessage(
                    title = "🌟 Orgulho Felino",
                    text = "Lembre-se: aos olhos do {gato}, você é a melhor pessoa do universo inteiro. Nunca duvide do seu brilho!",
                    category = "AUTOESTIMA",
                    isCustom = false
                ),
                CatAffectionMessage(
                    title = "🧶 Vá no seu próprio ritmo!",
                    text = "Até os gatinhos tiram 16 horas de sono por dia! Não se cobre tanto, você está fazendo o seu melhor!",
                    category = "SAUDE_MENTAL",
                    isCustom = false
                ),
                CatAffectionMessage(
                    title = "🐟 Hidratação & Cuidado",
                    text = "Você já bebeu água hoje? O {gato} exige que você se hidrate para continuar sendo esse humano maravilhoso!",
                    category = "INCENTIVO",
                    isCustom = false
                ),
                CatAffectionMessage(
                    title = "🥰 Chamego de Bigodinho",
                    text = "Passando só pra deixar um ronrom bem gostoso no seu coração. Você ilumina cada cantinho da casa!",
                    category = "CARINHO",
                    isCustom = false
                ),
                CatAffectionMessage(
                    title = "🌈 Um Dia Radiante",
                    text = "O {gato} acordou com os bigodes apontados para a vitória! Hoje vai ser um dia incrível, confie!",
                    category = "INCENTIVO",
                    isCustom = false
                )
            )
            affectionDao.insertMessages(defaults)
        }
    }
}
