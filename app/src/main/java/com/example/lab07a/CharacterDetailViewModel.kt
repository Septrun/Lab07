package com.example.lab07a

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.lab07a.data.local.AppDatabase
import com.example.lab07a.data.mappers.toModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterDetailUiState(
    val character: Character? = null,
    val isLoading: Boolean = true
)

class CharacterDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val route = savedStateHandle.toRoute<CharacterDetailRoute>()
    private val characterDao = AppDatabase.getDatabase(application).characterDao()

    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    init {
        loadCharacterDetail()
    }

    fun loadCharacterDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val entity = characterDao.getCharacterById(route.characterId)
            _uiState.update {
                it.copy(
                    character = entity?.toModel(),
                    isLoading = false
                )
            }
        }
    }

    fun toggleFavorite() {
        val current = _uiState.value.character ?: return
        viewModelScope.launch {
            val newStatus = !current.isFavorite
            characterDao.updateFavorite(current.id, newStatus)
            _uiState.update { it.copy(character = current.copy(isFavorite = newStatus)) }
        }
    }
}