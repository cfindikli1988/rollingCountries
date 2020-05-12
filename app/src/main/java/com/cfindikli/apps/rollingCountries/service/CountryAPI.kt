package com.cfindikli.apps.rollingCountries.service

import com.cfindikli.apps.rollingCountries.model.CountryModel
import io.reactivex.Observable
import retrofit2.http.GET

interface CountryAPI {

    @GET("all?fields=name;alpha2Code")
    fun getData(): Observable<List<CountryModel>>
}