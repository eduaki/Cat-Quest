package com.catcorp.catquest.data

data class TaskEntry(
    val id: Long = 0,
    val date: String, // Formato YYYY-MM-DD
    val challengeTitle: String,
    val photoUri: String? = null,
    val isCompleted: Boolean = false
)
