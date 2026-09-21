package com.DipuoMokwena.pantrychef.remote

data class MealResponse(
    val meals: List<MealDto>?
)

data class MealDto(
    val idMeal: String?,
    val strMeal: String?,
    val strMealThumb: String?,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strYoutube: String?
)