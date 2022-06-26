package com.cfindikli.apps.rollingCountries.service

import com.cfindikli.apps.rollingCountries.model.CountryModel
import retrofit2.Response
import retrofit2.http.GET

interface CountryAPI {

    @GET("all?fields=name,alpha2Code")
    suspend fun getData(): Response<List<CountryModel>>
}