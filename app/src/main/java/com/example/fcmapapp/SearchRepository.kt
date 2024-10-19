package com.example.fcmapapp

import android.app.Application
import android.content.res.Resources
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SearchRepository {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(MapAppInterceptor())
        .build()

    private  val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openapi.naver.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    private val service = retrofit.create(SearchService::class.java)

    fun getGoodRestaurants(query: String): Call<SearchResult> {
        return service.getGoodRestaurants(query = "$query 맛집", display = 5)
    }


    class MapAppInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val clientId = MyApplication.applicationContext.getString(R.string.naver_search_client_id)
            val clientSecret = MyApplication.applicationContext.getString(R.string.naver_search_client_secret)

            val newRequest = chain.request().newBuilder()
                .addHeader("X-Naver-Client-Id", clientId)
                .addHeader("X-Naver-Client-Secret", clientSecret)
                .build()

            return chain.proceed(newRequest)
        }

    }
}