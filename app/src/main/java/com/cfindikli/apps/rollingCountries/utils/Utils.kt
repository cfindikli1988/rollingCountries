package com.cfindikli.apps.rollingCountries.utils

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.view.animation.Animation
import android.widget.ImageView
import com.cfindikli.apps.rollingCountries.R
import com.cfindikli.apps.rollingCountries.model.CountryModel
import com.squareup.seismic.ShakeDetector
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.security.SecureRandom
import java.util.*


class Utils {
	
	
	companion object {
		
		val song = listOf(R.raw.dicerolleffect, R.raw.queenwearethechampions, R.raw.whawha)
		var isMute = false
		var mp: MediaPlayer? = null
		var shakeDetector: ShakeDetector? = null
		var sensorManager: SensorManager? = null
		var anim1: Animation? = null
		var anim2: Animation? = null
		var firstCountryObj = CountryModel()
		var secondCountryObj = CountryModel()
		private var reselect = CountryModel()
		var countryModelList: List<CountryModel>? = null
		const val BASE_URL = "https://restcountries.eu/rest/v2/"
		
		
		private fun randomCountry(): IntArray {
			return SecureRandom().ints(2, 0, 250).distinct().toArray()
		}
		
        fun getFlag(shortCode: String?): Uri {
			val uri = "http://flagpedia.net/data/flags/normal/${shortCode!!.toLowerCase(Locale.ENGLISH)}.png"
			return Uri.parse(uri)
		}
		
		fun randomDiceValue(): IntArray {
			return SecureRandom().ints(2, 1, 7).toArray()
		}
		
		fun reselect(countryModel: CountryModel) {
			do {
				
				reselect = countryModelList!![this.randomCountry()[SecureRandom().nextInt(2)]]
				
			} while (reselect.name == secondCountryObj.name || reselect.name == firstCountryObj.name)
			countryModel.name = reselect.name.toString()
			countryModel.alpha2Code = reselect.alpha2Code!!.toLowerCase(Locale.ENGLISH)
			countryModel.imageUrl = getFlag(countryModel.alpha2Code)
		}
		
		fun throwConfetti(confettiView: KonfettiView) {
			
			confettiView.build()
					.addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLUE)
					.setDirection(0.0, 359.0)
					.setSpeed(1f, 5f)
					.setFadeOutEnabled(true)
					.setTimeToLive(2000L)
					.addShapes(Shape.RECT, Shape.CIRCLE)
					.addSizes(Size(12))
					.setPosition(-50f, confettiView.width + 50f, -50f, -50f)
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









