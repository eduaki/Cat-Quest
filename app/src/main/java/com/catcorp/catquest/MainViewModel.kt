package com.catcorp.catquest

import android.app.Application
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.catcorp.catquest.data.ChallengeGenerator
import com.catcorp.catquest.data.DatabaseHelper
import com.catcorp.catquest.data.TaskEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val _currentTask = MutableStateFlow<TaskEntry?>(null)
    val currentTask: StateFlow<TaskEntry?> = _currentTask.asStateFlow()

    private val _monthlyTasks = MutableStateFlow<List<TaskEntry>>(emptyList())
    val monthlyTasks: StateFlow<List<TaskEntry>> = _monthlyTasks.asStateFlow()

    init {
        loadTodayTask()
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentMonthString(): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return sdf.format(Date())
    }

    fun loadTodayTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = getTodayDateString()
            var task = dbHelper.getTaskByDate(dateStr)
            if (task == null) {
                val challenge = ChallengeGenerator.getChallengeForDate(dateStr)
                task = TaskEntry(date = dateStr, challengeTitle = challenge)
                dbHelper.insertOrUpdateTask(task)
            }
            _currentTask.value = task
            loadMonthlyTasks()
        }
    }

    fun loadMonthlyTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentMonth = getCurrentMonthString()
            val tasks = dbHelper.getTasksForMonth(currentMonth)
            _monthlyTasks.value = tasks
        }
    }

    fun completeTodayTask(photoUri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = _currentTask.value
            if (current != null) {
                val updatedTask = TaskEntry(
                    id = current.id,
                    date = current.date,
                    challengeTitle = current.challengeTitle,
                    photoUri = photoUri,
                    isCompleted = true
                )
                dbHelper.insertOrUpdateTask(updatedTask)
                _currentTask.value = updatedTask
                loadMonthlyTasks()
            }
        }
    }
}
