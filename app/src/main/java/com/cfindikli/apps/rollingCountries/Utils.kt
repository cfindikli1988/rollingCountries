package com.cfindikli.apps.rollingCountries

import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import org.springframework.web.client.RestTemplate
import java.util.*


class Utils {


    @SuppressLint("StaticFieldLeak")
    inner class FetchJson : AsyncTask<String, Void, Array<out Any>?>() {


        override fun doInBackground(vararg params: String): Array<out Any>? {
            val restTemplate = RestTemplate()
            response = restTemplate.getForObject(params[0], Map::class.java).entries.stream().toArray()
            val firstCountry = response!![randomCountry()].toString().split("=")
            val secondCountry = response!![randomCountry()].toString().split("=")
            return arrayOf((firstCountry.first().toString().toLowerCase()), firstCountry.last(), secondCountry.first().toString().toLowerCase(), secondCountry.last())
        }

    }

    companion object {


        var response: Array<out Any>? = null

        const val url = "http://country.io/names.json"

        fun randomCountry(): Int {
            return Random().nextInt(250) + 1
        }


        fun getFlag(shortCode: String?): android.net.Uri {

            val uri = "http://flagpedia.net/data/flags/normal/$shortCode.png"

            return Uri.parse(uri)
        }

        fun randomDiceValue(): Int {
            return Random().nextInt(6) + 1
        }


    }


}






