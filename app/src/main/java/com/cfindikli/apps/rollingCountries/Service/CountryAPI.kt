package com.cfindikli.apps.rollingCountries.Service

import com.cfindikli.apps.rollingCountries.Model.CountryModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import java.util.*

interface CountryAPI {

    @GET("all?fields=name;alpha2Code")
    fun getData(): Observable<List<CountryModel>>
}