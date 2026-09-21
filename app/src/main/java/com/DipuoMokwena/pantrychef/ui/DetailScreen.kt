package com.DipuoMokwena.pantrychef.ui.detail

import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.DipuoMokwena.pantrychef.data.remote.MealDto
import com.DipuoMokwena.pantrychef.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DetailState(
    val isLoading: Boolean = false,
    val meal: MealDto? = null,
    val error: String? = null,
    val cookingMode: Boolean = false
)

class DetailViewModel : ViewModel() {
    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state

    fun load(id: String) {
        viewModelScope.launch {
            _state.value = DetailState(isLoading = true)
            try {
                val result = RetrofitClient.api.getMealById(id)
                _state.value = DetailState(meal = result.meals?.firstOrNull())
            } catch (e: Exception) {
                _state.value = DetailState(error = e.localizedMessage ?: "Could not load recipe")
            }
        }
    }

    fun toggleCookingMode() {
        val current = _state.value
        _state.value = current.copy(cookingMode = !current.cookingMode)
    }
}

@Composable
fun DetailScreen(
    mealId: String,
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val view = LocalView.current

    LaunchedEffect(mealId) {
        viewModel.load(mealId)
    }

    DisposableEffect(state.cookingMode) {
        val window = (view.context as? android.app.Activity)?.window
        if (state.cookingMode) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextButton(onClick = onBack) { Text("Back") }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        if (state.error != null) {
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }

        state.meal?.let { meal ->
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(meal.strMeal ?: "", style = MaterialTheme.typography.headlineSmall)
            Text("${meal.strCategory ?: ""} • ${meal.strArea ?: ""}")
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { viewModel.toggleCookingMode() }) {
                Text(if (state.cookingMode) "Exit cooking mode" else "Start cooking")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Instructions", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(meal.strInstructions ?: "No instructions")
        }
    }
}