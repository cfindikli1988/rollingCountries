package com.cfindikli.apps.rollingCountries

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_start.*
import org.springframework.web.client.RestTemplate
import java.util.*

@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {
    private val random = Random()

    private var pStatus = 0
    var uri1: Uri? = null
    var uri2: Uri? = null
    private val url = "http://country.io/names.json"
    private val handler = Handler()


    fun randomCountry(): Int {
        return random.nextInt(250) + 1
    }


    private fun getFlag(shortCode: String?): android.net.Uri {

        val uri = "http://flagpedia.net/data/flags/normal/$shortCode.png"

        return Uri.parse(uri)
    }


    private var firstCountryName: String = ""
    private var secondCountryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)


        firstCountryFlag.visibility = View.INVISIBLE
        secondCountryFlag.visibility = View.INVISIBLE
        firstCountryText.visibility = View.INVISIBLE
        secondCountryText.visibility = View.INVISIBLE

        FetchJson().execute(url)

        Thread(Runnable {

            while (pStatus < 100) {

                pStatus += 3

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

    @SuppressLint("StaticFieldLeak")
    inner class FetchJson : AsyncTask<String, Void, Array<out Any>?>() {
        override fun doInBackground(vararg params: String): Array<out Any>? {
            val restTemplate = RestTemplate()
            val response = restTemplate.getForObject(params[0], Map::class.java).entries.stream().toArray()
            val firstCountry = response!![randomCountry()].toString().split("=")
            val secondCountry = response[randomCountry()].toString().split("=")
            return arrayOf(firstCountry.first(), firstCountry.last(), secondCountry.first(), secondCountry.last())
        }


        override fun onPostExecute(result: Array<out Any>?) {


            uri1 = getFlag(result!!.first().toString().toLowerCase())
            firstCountryName = result[1].toString()
            uri2 = getFlag(result[2].toString().toLowerCase())
            secondCountryName = result.last().toString()
            setUIComponents(uri1!!, uri2!!, firstCountryName, secondCountryName)

        }
    }


    private fun switchToMainActivity() {

        val i = Intent(this, MainActivity::class.java)
        i.putExtra("uri1", uri1.toString())
        i.putExtra("uri2", uri2.toString())
        i.putExtra("firstCountryName", firstCountryName)
        i.putExtra("secondCountryName", secondCountryName)
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
