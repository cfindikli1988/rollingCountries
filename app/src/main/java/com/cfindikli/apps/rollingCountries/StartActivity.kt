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
import com.squareup.picasso.Picasso
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.util.*


@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {
    private val random = Random()

    private var pStatus = 0
    var uri1: Uri? = null
    var uri2: Uri? = null
    val url = "http://country.io/names.json"
    lateinit var firstCountry: TextView
    lateinit var secondCountry: TextView
    lateinit var firstCountryFlag: ImageView
    lateinit var secondCountryFlag: ImageView
    lateinit var mProgress: ProgressBar
    private val handler = Handler()
    @SuppressLint("SetTextI18n")


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


        mProgress = findViewById(R.id.google_progress)
        firstCountry = this.findViewById(R.id.textView5)
        secondCountry = this.findViewById(R.id.textView)
        firstCountryFlag = this.findViewById(R.id.imageView4)
        secondCountryFlag = this.findViewById(R.id.imageView3)
        firstCountryFlag.visibility = View.INVISIBLE
        secondCountryFlag.visibility = View.INVISIBLE
        firstCountry.visibility = View.INVISIBLE
        secondCountry.visibility = View.INVISIBLE

        fetchJson().execute(url)




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

    inner class fetchJson : AsyncTask<String, Void, Array<out Any>?>() {
        override fun doInBackground(vararg params: String): Array<out Any>? {
            val restTemplate = RestTemplate()

            restTemplate.messageConverters.add(
                    StringHttpMessageConverter())

            return restTemplate.getForObject(params[0], Map::class.java).entries.stream().toArray()
        }


        override fun onPostExecute(result: Array<out Any>?) {


            val firstCountries = result!![randomCountry()].toString().split("=")
            val secondCountries = result[randomCountry()].toString().split("=")



            uri1 = getFlag(firstCountries[0].toLowerCase())
            firstCountryName = firstCountries[1]
            uri2 = getFlag(secondCountries[0].toLowerCase())
            secondCountryName = secondCountries[1]



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


}
