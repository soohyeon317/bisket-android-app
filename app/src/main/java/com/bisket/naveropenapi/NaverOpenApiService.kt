package com.bisket.naveropenapi

import com.bisket.dto.ReverseGeoCode.ReverseGeoCodeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverOpenApiService {

    @GET("/map-reversegeocode/v2/gc")
    fun getAreaAddressNameList(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String? = null,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String? = null,
        @Query("coords") coordinates: String? = null,
        @Query("orders") orders: String = "legalcode,admcode,addr,roadaddr",
        @Query("output") output: String = "json"
    ): Call<ReverseGeoCodeResponse>

}