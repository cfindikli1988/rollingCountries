package com.cfindikli.apps.rollingCountries

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.picasso.Picasso
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {
    private val RANDOM = Random()

    private var pStatus = 0
    var uri1: Uri? = null
    var uri2: Uri? = null
    private val handler = Handler()
    @SuppressLint("SetTextI18n")


    private fun randomCountry(): Int {
        return RANDOM.nextInt(250) + 1
    }

    private fun getKeyFromValue(hm: Map<*, *>, value: Any): String? {
        for (o in hm.keys) {
            if (hm[o] == value) {
                return o.toString().toLowerCase()
            }
        }
        return null
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


        val mProgress = findViewById<ProgressBar>(R.id.google_progress)
        val firstCountry = this.findViewById<TextView>(R.id.textView5)
        val secondCountry = this.findViewById<TextView>(R.id.textView)
        val firstCountryFlag = this.findViewById<ImageView>(R.id.imageView4)
        val secondCountryFlag = this.findViewById<ImageView>(R.id.imageView3)
        firstCountryFlag.visibility = View.INVISIBLE
        secondCountryFlag.visibility = View.INVISIBLE
        firstCountry.visibility = View.INVISIBLE
        secondCountry.visibility = View.INVISIBLE


        val simpleGetTask = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<String, Void, String>() {
            override fun doInBackground(vararg params: String): String {
                val restTemplate = RestTemplate()
                //add the String message converter, since the result of
                // the call will be a String
                restTemplate.messageConverters.add(
                        StringHttpMessageConverter())
                // Make the HTTP GET request on the url (params[0]),
                // marshaling the response to a String
                return restTemplate.getForObject(params[0], String::class.java)
            }

            override fun onPostExecute(result: String) {
                // executed by the UI thread once the background
                // thread is done getting the result
                val mapper = ObjectMapper()

                var map: Map<String, Any> = HashMap()

                // convert JSON string to Map
                try {
                    map = mapper.readValue(result, object : TypeReference<Map<String, String>>() {

                    })
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val countries = ArrayList(map.values)
                val randomSelectionFirstCountry = randomCountry()
                val randomSelectionSecondCountry = randomCountry()
                try {
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                uri1 = getFlag(getKeyFromValue(map, countries[randomSelectionFirstCountry - 1]))
                firstCountryName = countries[randomSelectionFirstCountry - 1].toString()
                uri2 = getFlag(getKeyFromValue(map, countries[randomSelectionSecondCountry - 1]))
                secondCountryName = countries[randomSelectionSecondCountry - 1].toString()



                firstCountry.text = firstCountryName

                Picasso.with(this@StartActivity).load(uri1)
                        .resize(400, 267).error(R.drawable.rollingdices)
                        .into(firstCountryFlag)



                secondCountry.text = secondCountryName

                Picasso.with(this@StartActivity).load(uri2)
                        .resize(400, 267)
                        .error(R.drawable.rollingdices).into(secondCountryFlag)


            }
        }

        val url = "http://country.io/names.json"
        simpleGetTask.execute(url)


        Thread(Runnable {

            while (pStatus < 100) {

                pStatus += 1

                handler.post {
                    mProgress.progress = pStatus

                }
                try {

                    Thread.sleep(100) //thread will take approx 3 seconds to finish
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

            val i = Intent(this, MainActivity::class.java)

            i.putExtra("uri1", uri1.toString())
            i.putExtra("uri2", uri2.toString())
            i.putExtra("firstCountryName", firstCountryName)
            i.putExtra("secondCountryName", secondCountryName)
            startActivity(i)

        }).start()


    }


}
