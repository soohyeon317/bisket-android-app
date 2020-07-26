package com.bisket.naveropenapi

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NaverOpenApiClient {
    private var instance : Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    fun getInstnace() : Retrofit {
        if(instance == null){
            instance = Retrofit.Builder()
                .baseUrl("https://naveropenapi.apigw.ntruss.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance!!
    }
}