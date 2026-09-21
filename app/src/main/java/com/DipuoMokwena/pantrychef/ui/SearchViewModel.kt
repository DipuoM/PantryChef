package com.DipuoMokwena.pantrychef.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.DipuoMokwena.pantrychef.remote.RetrofitClient
import com.DipuoMokwena.pantrychef.remote.MealDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SearchState(
    val isLoading: Boolean = false,
    val meals: List<MealDto> = emptyList(),
    val error: String? = null
)

class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state

    fun search(query: String) {
        if (query.isBlank()) {
            _state.value = SearchState(error = "Please enter a recipe or ingredient")
            return
        }

        viewModelScope.launch {
            _state.value = SearchState(isLoading = true)
            try {
                val byName = RetrofitClient.api().searchByName(query.trim())
                val meals = byName.meals
                if (!meals.isNullOrEmpty()) {
                    _state.value = SearchState(meals = meals)
                } else {
                    val byIngredient = RetrofitClient.api().filterByIngredient(query.trim())
                    _state.value = SearchState(
                        meals = byIngredient.meals ?: emptyList(),
                        error = if (byIngredient.meals.isNullOrEmpty()) "No recipes found" else null
                    )
                }
            } catch (e: Exception) {
                _state.value = SearchState(error = e.localizedMessage ?: "Could not load recipes")
            }
        }
    }
}