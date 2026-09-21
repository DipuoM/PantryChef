package com.DipuoMokwena.pantrychef.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("search.php")
    suspend fun searchByName(@Query("s") query: String): MealResponse

    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealResponse

    @GET("filter.php")
    suspend fun filterByIngredient(@Query("i") ingredient: String): MealResponse
}