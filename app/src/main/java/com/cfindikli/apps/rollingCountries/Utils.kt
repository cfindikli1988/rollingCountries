package com.cfindikli.apps.rollingCountries

import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import org.springframework.web.client.RestTemplate
import java.util.*


class Utils {


    @SuppressLint("StaticFieldLeak")
    inner class FetchJson : AsyncTask<String, Void, List<String>>() {


        override fun doInBackground(vararg params: String): List<String> {
            val restTemplate = RestTemplate()
            response = restTemplate.getForObject(params[0], Map::class.java).entries.stream().toArray().toList()
            val firstCountry = response!![randomCountry().first()].toString().split("=")
            val secondCountry = response!![randomCountry().last()].toString().split("=")
            return listOf((firstCountry.first().toString().toLowerCase()), firstCountry.last().toString(), secondCountry.first().toString().toLowerCase(), secondCountry.last().toString())
        }

    }

    companion object {


        var response: List<Any>? = null

        const val url = "http://country.io/names.json"

        fun randomCountry(): List<Int> {
            return Random().ints(2,0,250).toArray().toList()
        }


        fun getFlag(shortCode: String?): android.net.Uri {

            val uri = "http://flagpedia.net/data/flags/normal/$shortCode.png"

            return Uri.parse(uri)
        }

        fun randomDiceValue(): List<Int> {
            return Random().ints(2,1,7).toArray().toList()
        }


    }


}






