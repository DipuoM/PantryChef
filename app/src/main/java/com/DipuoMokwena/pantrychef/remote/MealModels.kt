package com.DipuoMokwena.pantrychef.remote

data class MealResponse(
    val meals: List<MealDto>?
)

data class MealDto(
    val idMeal: String? = null,
    val strMeal: String? = null,
    val strMealThumb: String? = null,
    val strCategory: String? = null,
    val strArea: String? = null,
    val strInstructions: String? = null
)