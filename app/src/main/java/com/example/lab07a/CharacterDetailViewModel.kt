package com.example.lab07a

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterDetailUiState(
    val isLoading: Boolean = true,
    val data: Character? = null,
    val hasError: Boolean = false
)

class CharacterDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    private val route = savedStateHandle.toRoute<CharacterDetailRoute>()
    private val characterId = route.characterId
    private val characterDb = CharacterDb()

    init { loadCharacterDetail() }

    fun loadCharacterDetail() {
        viewModelScope.launch {
            // Se definen los 2s para la carga del perfil del personaje
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(2000L)
            try {
                val character = characterDb.getCharacterById(characterId)
                _uiState.update { it.copy(isLoading = false, data = character) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun triggerError() {
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}