package com.cfindikli.apps.rollingCountries

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.cfindikli.apps.rollingCountries.Utils.Companion.fetchValues
import com.cfindikli.apps.rollingCountries.Utils.Companion.firstCountryObj
import com.cfindikli.apps.rollingCountries.Utils.Companion.secondCountryObj
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_start.*

@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {


    private var pStatus = 0

    private val handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        firstCountryFlag.visibility = View.INVISIBLE
        secondCountryFlag.visibility = View.INVISIBLE
        firstCountryText.visibility = View.INVISIBLE
        secondCountryText.visibility = View.INVISIBLE

        fetchValues = Utils().FetchJson().execute(Utils.url).get()!!

        firstCountryObj.shortCode = fetchValues[0].toLowerCase()
        secondCountryObj.shortCode = fetchValues[2].toLowerCase()
        firstCountryObj.imageUrl = Utils.getFlag(firstCountryObj.shortCode)
        secondCountryObj.imageUrl = Utils.getFlag(secondCountryObj.shortCode)

        firstCountryObj.countryName = fetchValues[1]
        secondCountryObj.countryName = fetchValues[3]


        setUIComponents()


        Thread(Runnable {

            while (pStatus < 100) {

                pStatus += 2

                handler.post {
                    google_progress.progress = pStatus

                }
                try {

                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
            switchToMainActivity()
        }).start()


    }


    private fun switchToMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }


    private fun setUIComponents() {

        firstCountryText.text = firstCountryObj.countryName
        Picasso.with(this@StartActivity).load(firstCountryObj.imageUrl)
                .resize(400, 267).error(R.drawable.rollingdices)
                .into(firstCountryFlag)

        secondCountryText.text = secondCountryObj.countryName

        Picasso.with(this@StartActivity).load(secondCountryObj.imageUrl)
                .resize(400, 267)
                .error(R.drawable.rollingdices).into(secondCountryFlag)
    }


}
