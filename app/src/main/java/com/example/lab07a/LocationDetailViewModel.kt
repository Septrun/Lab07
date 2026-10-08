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

data class LocationDetailUiState(
    val isLoading: Boolean = true,
    val data: Location? = null,
    val hasError: Boolean = false
)

class LocationDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(LocationDetailUiState())
    val uiState: StateFlow<LocationDetailUiState> = _uiState.asStateFlow()

    private val route = savedStateHandle.toRoute<LocationDetailRoute>()
    private val locationId = route.locationId
    private val locationDb = LocationDb()

    init { loadLocationDetail() }

    fun loadLocationDetail() {
        viewModelScope.launch {
            // Se definen los 2s para la carga del perfil de la ubicación
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(2000L)
            try {
                val location = locationDb.getLocationById(locationId)
                _uiState.update { it.copy(isLoading = false, data = location) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun triggerError() {
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}