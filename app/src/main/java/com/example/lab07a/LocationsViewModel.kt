package com.example.lab07a

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationsUiState(
    val isLoading: Boolean = true,
    val data: List<Location>? = null,
    val hasError: Boolean = false
)

class LocationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()
    private val locationDb = LocationDb()

    init { loadLocations() }

    fun loadLocations() {
        viewModelScope.launch {
            // Se definen los 4s para la carga del listado de las ubicaciones
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            delay(4000L)
            try {
                val locations = locationDb.getAllLocations()
                _uiState.update { it.copy(isLoading = false, data = locations) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun triggerError() {
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}