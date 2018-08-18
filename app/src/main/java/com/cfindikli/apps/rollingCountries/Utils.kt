package com.cfindikli.apps.rollingCountries

import android.net.Uri
import android.os.AsyncTask
import android.os.NetworkOnMainThreadException
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class Utils {


    inner class FetchJson : AsyncTask<String, Void, Array<out Any>?>() {


        override fun doInBackground(vararg params: String): Array<out Any>? {
            val restTemplate = RestTemplate()
            val response = restTemplate.getForObject(params[0], Map::class.java).entries.stream().toArray()
            val firstCountry = response!![randomCountry()].toString().split("=")
            val secondCountry = response[randomCountry()].toString().split("=")
            return arrayOf((firstCountry.first().toString().toLowerCase()), firstCountry.last(), secondCountry.first().toString().toLowerCase(), secondCountry.last())
        }

    }

    companion object {


        fun checkURL(urlString: String): Boolean {
            try {
                val url = URL(urlString)
                val urlConnection = url
                        .openConnection() as HttpURLConnection
                val responseCode = urlConnection.responseCode
                urlConnection.disconnect()
                return responseCode == 200
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return false
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } catch (e: NetworkOnMainThreadException) {
                e.printStackTrace()
                return false
            }

        }

        fun randomCountry(): Int {
            return Random().nextInt(250) + 1
        }


        fun getFlag(shortCode: String?): android.net.Uri {

            val uri = "http://flagpedia.net/data/flags/normal/$shortCode.png"

            return Uri.parse(uri)
        }


    }


}






