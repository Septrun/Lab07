package com.example.lab07a

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab07a.data.local.AppDatabase
import com.example.lab07a.data.local.CharacterEntity
import com.example.lab07a.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val nameInput: String = "",
    val isLoadingSession: Boolean = true,
    val isSyncing: Boolean = false,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkExistingSession()
    }

    fun onNameChanged(newName: String) {
        _uiState.update { it.copy(nameInput = newName) }
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            userPreferences.userNameFlow.collect { savedName ->
                if (!savedName.isNullOrBlank()) {
                    _uiState.update { it.copy(isLoadingSession = false, isLoggedIn = true) }
                } else {
                    _uiState.update { it.copy(isLoadingSession = false, isLoggedIn = false) }
                }
            }
        }
    }

    fun loginUser(onSuccess: () -> Unit) {
        val currentName = _uiState.value.nameInput.trim()
        if (currentName.isNotEmpty()) {
            viewModelScope.launch {
                userPreferences.saveUserName(currentName)

                // Obtiene la base de datos de Room
                val database = AppDatabase.getDatabase(getApplication())
                val mockDb = CharacterDb()
                val characterList = mockDb.getAllCharacters()

                val entities = characterList.map { character ->
                    CharacterEntity(
                        id = character.id,
                        name = character.name,
                        status = character.status,
                        species = character.species,
                        gender = character.gender,
                        image = character.image,
                        isFavorite = false
                    )
                }

                database.characterDao().insertCharacters(entities)
                onSuccess()
            }
        }
    }
}