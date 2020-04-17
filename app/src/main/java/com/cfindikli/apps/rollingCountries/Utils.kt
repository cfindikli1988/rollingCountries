package com.cfindikli.apps.rollingCountries

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.view.animation.Animation
import android.widget.ImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.seismic.ShakeDetector
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.lang.reflect.Type
import java.net.URL
import java.security.SecureRandom
import java.util.*


class Utils {


    @SuppressLint("StaticFieldLeak")
    inner class FetchJson : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg params: String): Nothing? {
            val response = URL(url).readText()
            val listType: Type = object : TypeToken<List<Country?>?>() {}.type
            countryList = Gson().fromJson(response, listType)
            firstCountryObj = countryList!![randomCountry().first()]
            secondCountryObj = countryList!![randomCountry().last()]
            firstCountryObj.imageUrl = getFlag(firstCountryObj.alpha2Code)
            secondCountryObj.imageUrl = getFlag(secondCountryObj.alpha2Code)
            return null

        }
    }
    companion object {

        val song = arrayOf(R.raw.dicerolleffect, R.raw.queenwearethechampions, R.raw.whawha)
        var isMute = false
        var mp: MediaPlayer? = null
        var shakeDetector: ShakeDetector? = null
        var sensorManager: SensorManager? = null
        var anim1: Animation? = null
        var anim2: Animation? = null
        var firstCountryObj = Country()
        var secondCountryObj = Country()
        private var reselect = Country()
        var countryList: List<Country>? = null
        const val url = "https://restcountries.eu/rest/v2/all?fields=name;alpha2Code"

        fun randomCountry(): IntArray {
            return SecureRandom().ints(2, 0, 250).distinct().toArray()
        }

        fun getFlag(shortCode: String?): Uri {
            val uri = "http://flagpedia.net/data/flags/normal/${shortCode!!.toLowerCase(Locale.ENGLISH)}.png"
            return Uri.parse(uri)
        }

        fun randomDiceValue(): IntArray {
            return SecureRandom().ints(2, 1, 7).toArray()
        }

        fun reselect(country: Country): Void? {
            do {

                reselect = countryList!![randomCountry()[SecureRandom().nextInt(2)]]

            } while (reselect.name == secondCountryObj.name || reselect.name == firstCountryObj.name)
            country.name = reselect.name.toString()
            country.alpha2Code = reselect.alpha2Code!!.toLowerCase(Locale.ENGLISH)
            country.imageUrl = getFlag(country.alpha2Code)
            return null

        }

        fun throwConfetti(konfettiView: KonfettiView) {

            konfettiView.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLUE)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.RECT, Shape.CIRCLE)
                    .addSizes(Size(12))
                    .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
                    .streamFor(500, 5000L)
        }


        fun setBW(iv: ImageView, isUnfiltered: Float?) {
            val matrix = ColorMatrix()
            matrix.setSaturation(isUnfiltered!!)
            val filter = ColorMatrixColorFilter(matrix)
            iv.colorFilter = filter
        }

    }


}






