package com.DipuoMokwena.pantrychef.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.DipuoMokwena.pantrychef.remote.MealDto

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onMealClick: (String) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Search Recipes", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Recipe name or ingredient") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.search(query) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        val errorMessage = state.error
        if (errorMessage != null) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.meals) { meal ->
                MealRow(
                    meal = meal,
                    onClick = {
                        val id = meal.idMeal
                        if (!id.isNullOrBlank()) {
                            onMealClick(id)
                        }
                    }
                )
            }
        }

        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}

@Composable
fun MealRow(
    meal: MealDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        AsyncImage(
            model = meal.strMealThumb,
            contentDescription = meal.strMeal,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = meal.strMeal ?: "Unknown")
            Text(text = meal.strCategory ?: "")
        }
    }
}