package com.example.lab07a

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab07a.data.local.AppDatabase
import com.example.lab07a.data.local.CharacterEntity
import com.example.lab07a.data.mappers.toModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val showOnlyFavorites: Boolean = false
)

class CharactersViewModel(application: Application) : AndroidViewModel(application) {
    private val characterDao = AppDatabase.getDatabase(application).characterDao()

    private val _showOnlyFavorites = MutableStateFlow(false)

    val uiState: StateFlow<CharactersUiState> = combine(
        _showOnlyFavorites.flatMapLatest { onlyFavs ->
            if (onlyFavs) characterDao.getFavoriteCharacters() else characterDao.getAllCharacters()
        },
        _showOnlyFavorites
    ) { entities, onlyFavs ->
        CharactersUiState(
            characters = entities.map { it.toModel() },
            showOnlyFavorites = onlyFavs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CharactersUiState()
    )

    init {
        loadCharactersIfNeeded()
    }

    private fun loadCharactersIfNeeded() {
        viewModelScope.launch {
            try {
                if (characterDao.getCount() == 0) {
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

                    characterDao.insertCharacters(entities)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavoriteFilter() {
        _showOnlyFavorites.update { !it }
    }

    fun toggleFavorite(characterId: Int, currentStatus: Boolean) {
        viewModelScope.launch {
            characterDao.updateFavorite(characterId, !currentStatus)
        }
    }
}