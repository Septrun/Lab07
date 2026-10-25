package com.example.lab07a

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab07a.data.local.AppDatabase
import com.example.lab07a.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)
    private val database = AppDatabase.getDatabase(application)

    val userName: StateFlow<String> = userPreferences.userNameFlow
        .map { name -> name ?: "Diego Ayala" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Diego Ayala"
        )

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearSession()

            database.characterDao().deleteAllCharacters()
            database.locationDao().deleteAllLocations()

            onLogoutComplete()
        }
    }
}