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
import com.squareup.seismic.ShakeDetector
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import org.springframework.web.client.RestTemplate
import java.security.SecureRandom
import java.util.*


class Utils {


    @SuppressLint("StaticFieldLeak")
    inner class FetchJson : AsyncTask<String, Void, List<Country>>() {


        override fun doInBackground(vararg params: String): List<Country> {
            val restTemplate = RestTemplate()
            response = restTemplate.getForObject(params[0], Map::class.java).entries.stream().toArray().toList()
            val firstCountry = response!![randomCountry().first()].toString().split("=")
            val secondCountry = response!![randomCountry().last()].toString().split("=")
            firstCountryObj.shortCode = firstCountry.first().toLowerCase()
            firstCountryObj.countryName = firstCountry.last()
            secondCountryObj.shortCode = secondCountry.first().toLowerCase()
            secondCountryObj.countryName = secondCountry.last()
            firstCountryObj.imageUrl = Utils.getFlag(firstCountryObj.shortCode)
            secondCountryObj.imageUrl = Utils.getFlag(secondCountryObj.shortCode)
            return listOf(firstCountryObj, secondCountryObj)


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
        var response: List<Any>? = null

        const val url = "http://country.io/names.json"

        fun randomCountry(): List<Int> {
            return SecureRandom().ints(2, 0, 250).distinct().toArray().toList()
        }



        fun getFlag(shortCode: String?): android.net.Uri {

            val uri = "http://flagpedia.net/data/flags/normal/$shortCode.png"

            return Uri.parse(uri)
        }

        fun randomDiceValue(): List<Int> {
            return SecureRandom().ints(2, 1, 7).toArray().toList()
        }

        fun reselect(country: Country): Array<String>? {
            country.reselected = Objects.requireNonNull(Objects.requireNonNull<List<Any>>(Utils.response)[Utils.randomCountry()[Random().nextInt(2)]]).toString().split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return secondCountryObj.reselected
        }

        fun throwKonfetti(konfettiView: KonfettiView) {

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






