package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.CatCareApplication
import com.example.audio.CatChiptuneSynthesizer
import com.example.data.models.CatAffectionMessage
import com.example.data.models.CatMission
import com.example.data.models.CatProfile
import com.example.data.models.MissionCategory
import com.example.data.models.ReminderConfig
import com.example.data.repository.CatRepository
import com.example.notifications.CatNotificationHelper
import com.example.utils.DateUtils
import com.example.utils.PhotoManager
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class CatCareViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CatRepository = (application as CatCareApplication).repository
    val synthesizer = CatChiptuneSynthesizer()

    val todayDate: String = DateUtils.getTodayDateString()

    private val _selectedDate = MutableStateFlow(todayDate)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val currentCal = Calendar.getInstance()
    private val _selectedYearMonth = MutableStateFlow(
        Pair(currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH) + 1)
    )
    val selectedYearMonth: StateFlow<Pair<Int, Int>> = _selectedYearMonth.asStateFlow()

    val profile: StateFlow<CatProfile> = repository.profile
        .map { it ?: CatProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatProfile())

    val reminderConfig: StateFlow<ReminderConfig> = repository.reminderConfig
        .map { it ?: ReminderConfig() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReminderConfig())

    val affectionMessages: StateFlow<List<CatAffectionMessage>> = repository.allAffectionMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val datesWithCompletedMissions: StateFlow<List<String>> = repository.datesWithCompletedMissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val streakDays: StateFlow<Int> = datesWithCompletedMissions
        .map { DateUtils.calculateStreak(it.toSet()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val selectedDateMissions: StateFlow<List<CatMission>> = _selectedDate
        .flatMapLatest { date ->
            repository.ensureDefaultMissionsForDate(date)
            repository.getMissionsForDate(date)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMissions: StateFlow<List<CatMission>> = repository.getMissionsForDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthPhotos: StateFlow<List<CatMission>> = _selectedYearMonth
        .flatMapLatest { (year, month) ->
            val pattern = DateUtils.getMonthPattern(year, month)
            repository.getPhotosForMonth(pattern)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPhotos: StateFlow<List<CatMission>> = repository.allPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentWeekDays = DateUtils.getCurrentWeekDays()
    val weekMissionsProgress: StateFlow<List<DayMissionsBarData>> = repository
        .getMissionsBetween(currentWeekDays.first().dateStr, currentWeekDays.last().dateStr)
        .map { missionsList ->
            val missionsByDate = missionsList.groupBy { it.date }
            currentWeekDays.map { dayInfo ->
                val dateMissions = missionsByDate[dayInfo.dateStr] ?: emptyList()
                val completed = dateMissions.count { it.isCompleted }
                val total = dateMissions.size.coerceAtLeast(if (dayInfo.isToday) 3 else 0)
                DayMissionsBarData(
                    dateStr = dayInfo.dateStr,
                    dayLabel = dayInfo.dayLabel,
                    dayNumber = dayInfo.dayNumber,
                    completedCount = completed,
                    totalCount = total,
                    isToday = dayInfo.isToday
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog & Interaction states
    private val _recentlyCompletedMission = MutableStateFlow<CatMission?>(null)
    val recentlyCompletedMission: StateFlow<CatMission?> = _recentlyCompletedMission.asStateFlow()

    private val _lastScoreResult = MutableStateFlow<com.example.utils.ScoreCalculationResult?>(null)
    val lastScoreResult: StateFlow<com.example.utils.ScoreCalculationResult?> = _lastScoreResult.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.ensureDefaultMissionsForDate(todayDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
        viewModelScope.launch {
            try {
                repository.ensureDefaultMissionsForDate(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectMonth(year: Int, month: Int) {
        _selectedYearMonth.value = Pair(year, month)
    }

    fun nextMonth() {
        val (year, month) = _selectedYearMonth.value
        if (month == 12) {
            _selectedYearMonth.value = Pair(year + 1, 1)
        } else {
            _selectedYearMonth.value = Pair(year, month + 1)
        }
    }

    fun previousMonth() {
        val (year, month) = _selectedYearMonth.value
        if (month == 1) {
            _selectedYearMonth.value = Pair(year - 1, 12)
        } else {
            _selectedYearMonth.value = Pair(year, month - 1)
        }
    }

    fun completeMissionWithPhoto(mission: CatMission, photoPath: String) {
        viewModelScope.launch {
            val score = repository.completeMissionWithPhoto(
                missionId = mission.id,
                photoPath = photoPath,
                streakDays = streakDays.value
            )
            _lastScoreResult.value = score
            _recentlyCompletedMission.value = mission.copy(
                isCompleted = true,
                photoPath = photoPath,
                pointsEarned = score?.totalPoints ?: 0,
                fishEarned = score?.fishCoinsAwarded ?: 25
            )
        }
    }

    fun completeMissionWithInstantProof(context: Context, mission: CatMission) {
        viewModelScope.launch {
            val catName = profile.value.name
            val generatedPath = PhotoManager.createSampleCutePhoto(
                context = context,
                categoryName = mission.category,
                missionTitle = mission.title,
                catName = catName
            )
            val score = repository.completeMissionWithPhoto(
                missionId = mission.id,
                photoPath = generatedPath,
                streakDays = streakDays.value
            )
            _lastScoreResult.value = score
            _recentlyCompletedMission.value = mission.copy(
                isCompleted = true,
                photoPath = generatedPath,
                pointsEarned = score?.totalPoints ?: 0,
                fishEarned = score?.fishCoinsAwarded ?: 25
            )
        }
    }

    fun dismissCelebration() {
        _recentlyCompletedMission.value = null
        _lastScoreResult.value = null
    }

    fun undoMission(missionId: Long) {
        viewModelScope.launch {
            repository.undoCompleteMission(missionId)
        }
    }

    fun addCustomMission(title: String, description: String, category: MissionCategory) {
        viewModelScope.launch {
            repository.addCustomMission(
                date = _selectedDate.value,
                title = title,
                description = description,
                category = category
            )
        }
    }

    fun deleteMission(mission: CatMission) {
        viewModelScope.launch {
            repository.deleteMission(mission)
        }
    }

    fun buyOrEquipReward(item: com.example.data.models.ShopRewardItem) {
        viewModelScope.launch {
            repository.buyAndEquipReward(item)
        }
    }

    fun updateProfile(name: String, breed: String, favoriteSnack: String) {
        viewModelScope.launch {
            val updated = profile.value.copy(
                name = name,
                breed = breed,
                favoriteSnack = favoriteSnack
            )
            repository.updateProfile(updated)
            // Reschedule alarms with new cat name
            CatNotificationHelper.scheduleAlarmsFromConfig(
                getApplication(),
                reminderConfig.value,
                name
            )
        }
    }

    fun updateReminderConfig(config: ReminderConfig) {
        viewModelScope.launch {
            repository.updateReminderConfig(config)
            CatNotificationHelper.scheduleAlarmsFromConfig(
                getApplication(),
                config,
                profile.value.name
            )
        }
    }

    fun testNotificationNow(context: Context) {
        val catName = profile.value.name
        val tone = reminderConfig.value.friendlyTone
        val (title, message) = CatNotificationHelper.getRandomFriendlyMessage(
            catName = catName,
            period = "AFTERNOON",
            tone = tone
        )
        CatNotificationHelper.sendFriendlyNotification(
            context = context,
            title = title,
            message = message,
            soundEnabled = reminderConfig.value.soundEnabled
        )
    }

    fun sendSpontaneousLoveNotification(context: Context) {
        dispatchAffectionNotificationNow(context)
    }

    fun addCustomAffectionMessage(title: String, text: String, category: String = "INCENTIVO") {
        viewModelScope.launch {
            repository.addAffectionMessage(title, text, category)
        }
    }

    fun toggleAffectionMessage(message: com.example.data.models.CatAffectionMessage) {
        viewModelScope.launch {
            repository.updateAffectionMessage(message.copy(isEnabled = !message.isEnabled))
        }
    }

    fun deleteAffectionMessage(message: com.example.data.models.CatAffectionMessage) {
        viewModelScope.launch {
            repository.deleteAffectionMessage(message)
        }
    }

    fun dispatchAffectionNotificationNow(
        context: Context,
        specificMessage: com.example.data.models.CatAffectionMessage? = null
    ) {
        val catName = profile.value.name
        viewModelScope.launch {
            val (title, text) = if (specificMessage != null) {
                specificMessage.formatMessage(catName)
            } else {
                val enabled = repository.getEnabledAffectionMessages()
                if (enabled.isNotEmpty()) {
                    val chosen = enabled[kotlin.random.Random.nextInt(enabled.size)]
                    chosen.formatMessage(catName)
                } else {
                    CatNotificationHelper.getRandomAffectionateMessage(catName)
                }
            }

            com.example.notifications.LocalCatNotificationManager.sendAffectionNotification(
                context = context,
                title = title,
                message = text,
                catName = catName,
                soundEnabled = reminderConfig.value.soundEnabled
            )
        }
    }

    fun generateSampleMonthMemories(context: Context) {
        viewModelScope.launch {
            val (year, month) = _selectedYearMonth.value
            val catName = profile.value.name
            val sampleCategories = listOf(
                Pair(MissionCategory.WATER, "Água fresquinha do pote de cerâmica! Adorei 💧"),
                Pair(MissionCategory.FOOD, "Sachê delicioso de salmão servido no capricho! 🐟"),
                Pair(MissionCategory.GROOMING, "Pelos escovados e brilhando como seda! ✨"),
                Pair(MissionCategory.PLAY, "Brincadeira intensa com a varinha de penas! 🧶"),
                Pair(MissionCategory.LOVE, "Muito chamego, massagem na barriguinha e ronrom! 💖")
            )

            for ((index, item) in sampleCategories.withIndex()) {
                val (cat, comment) = item
                val day = 3 + index * 5
                val dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
                val photoPath = PhotoManager.createSampleCutePhoto(
                    context = context,
                    categoryName = cat.name,
                    missionTitle = cat.defaultTitle,
                    catName = catName
                )
                repository.insertSampleCompletedMission(
                    date = dateStr,
                    category = cat,
                    photoPath = photoPath,
                    catComment = comment
                )
            }
        }
    }

    fun shareCurrentMural(context: Context) {
        val (year, month) = _selectedYearMonth.value
        val monthDisplay = DateUtils.formatMonthYear(year, month)
        val photos = monthPhotos.value
        val samplePath = photos.firstOrNull()?.photoPath

        PhotoManager.shareMonthlyMural(
            context = context,
            monthName = monthDisplay,
            totalMissions = photos.size,
            totalPhotos = photos.size,
            streakDays = streakDays.value,
            catName = profile.value.name,
            samplePhotoPath = samplePath
        )
    }

    override fun onCleared() {
        super.onCleared()
        synthesizer.release()
    }
}

data class DayMissionsBarData(
    val dateStr: String,
    val dayLabel: String,
    val dayNumber: Int,
    val completedCount: Int,
    val totalCount: Int,
    val isToday: Boolean
) {
    val completionRatio: Float
        get() = if (totalCount > 0) (completedCount.toFloat() / totalCount).coerceIn(0f, 1f) else 0f
}
