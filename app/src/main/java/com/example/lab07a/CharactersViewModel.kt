package com.example.lab07a

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharactersUiState(
    val isLoading: Boolean = true,
    val data: List<Character>? = null,
    val hasError: Boolean = false
)

class CharactersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()
    private val characterDb = CharacterDb()

    init { loadCharacters() }

    fun loadCharacters() {
        viewModelScope.launch {
            // Se definen los 4s para la carga del listado de los personajes
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(4000L)
            try {
                val characters = characterDb.getAllCharacters()
                _uiState.update { it.copy(isLoading = false, data = characters) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun triggerError() {
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}