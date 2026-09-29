package com.example

import android.app.Application
import com.example.data.db.AppDatabase
import com.example.data.repository.CatRepository
import com.example.notifications.CatNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CatCareApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: CatRepository
        private set

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        CatNotificationHelper.createNotificationChannel(this)
        com.example.notifications.LocalCatNotificationManager.createNotificationChannel(this)

        database = AppDatabase.getDatabase(this)
        repository = CatRepository(
            missionDao = database.catMissionDao(),
            profileDao = database.catProfileDao(),
            reminderDao = database.reminderConfigDao(),
            affectionDao = database.catAffectionMessageDao()
        )

        applicationScope.launch {
            try {
                repository.initDefaultsIfFirstRun()
                val config = database.reminderConfigDao().getConfigSync()
                val profile = database.catProfileDao().getProfileSync()
                if (config != null) {
                    CatNotificationHelper.scheduleAlarmsFromConfig(
                        this@CatCareApplication,
                        config,
                        profile?.name ?: "Mingau"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
