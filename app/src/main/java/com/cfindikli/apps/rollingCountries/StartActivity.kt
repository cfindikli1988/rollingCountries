package com.cfindikli.apps.rollingCountries

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_start.*

@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {


    private var pStatus = 0
    private var uri1: Uri? = null
    private var uri2: Uri? = null

    private lateinit var fetchValues: List<String>
    private val handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        firstCountryFlag.visibility = View.INVISIBLE
        secondCountryFlag.visibility = View.INVISIBLE
        firstCountryText.visibility = View.INVISIBLE
        secondCountryText.visibility = View.INVISIBLE

        fetchValues = Utils().FetchJson().execute(Utils.url).get()!!

        uri1 = Utils.getFlag(fetchValues[0])
        uri2 = Utils.getFlag(fetchValues[2])

        setUIComponents(uri1!!, uri2!!, fetchValues[1], fetchValues[3])


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
        i.putExtra("uri1", uri1.toString())
        i.putExtra("uri2", uri2.toString())
        i.putExtra("shortCode1", fetchValues.first())
        i.putExtra("shortCode2", fetchValues[2])
        i.putExtra("firstCountryName", fetchValues[1])
        i.putExtra("secondCountryName", fetchValues.last())
        startActivity(i)
    }


    private fun setUIComponents(uri1: Uri, uri2: Uri, firstCountryName: String, secondCountryName: String) {

        firstCountryText.text = firstCountryName
        Picasso.with(this@StartActivity).load(uri1)
                .resize(400, 267).error(R.drawable.rollingdices)
                .into(firstCountryFlag)

        secondCountryText.text = secondCountryName

        Picasso.with(this@StartActivity).load(uri2)
                .resize(400, 267)
                .error(R.drawable.rollingdices).into(secondCountryFlag)
    }


}
