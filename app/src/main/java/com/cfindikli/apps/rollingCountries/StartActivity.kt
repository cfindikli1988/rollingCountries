package com.cfindikli.apps.rollingCountries

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
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
        Utils().FetchJson().execute(Utils.url).get()!!
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

        Picasso.get().load(firstCountryObj.imageUrl)
                .resize(400, 267).error(R.drawable.rollingdices)
                .into(firstCountryFlag)

        Picasso.get().load(secondCountryObj.imageUrl)
                .resize(400, 267)
                .error(R.drawable.rollingdices).into(secondCountryFlag)
    }


}
