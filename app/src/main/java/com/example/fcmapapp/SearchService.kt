package com.example.fcmapapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("v1/search/local.json")
    fun getGoodRestaurants(
        @Query("query") query: String,
        @Query("display") display: Int,
    ): Call<SearchResult>
}