package com.cfindikli.apps.rollingCountries

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cfindikli.apps.rollingCountries.Model.CountryModel
import com.cfindikli.apps.rollingCountries.Service.CountryAPI
import com.cfindikli.apps.rollingCountries.Utils.Companion.countryModelList
import com.cfindikli.apps.rollingCountries.Utils.Companion.firstCountryObj
import com.cfindikli.apps.rollingCountries.Utils.Companion.secondCountryObj
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_start.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {


    private var pStatus = 0
    private val handler = Handler()
    private var compositeDisposable: CompositeDisposable? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        compositeDisposable = CompositeDisposable()
        loadData()
        setUIComponents()
    }


    private fun switchToMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }




    private fun setUIComponents() {
        firstCountryFlag.visibility = View.INVISIBLE
        secondCountryFlag.visibility = View.INVISIBLE
        startRollingDiceAnimation()
        Picasso.get().load(firstCountryObj.imageUrl)
                .resize(400, 267).error(R.drawable.rollingdices)
                .into(firstCountryFlag)

        Picasso.get().load(secondCountryObj.imageUrl)
                .resize(400, 267)
                .error(R.drawable.rollingdices).into(secondCountryFlag)
    }


    private fun loadData() {


        val retrofit = Retrofit.Builder()
                .baseUrl(Utils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(CountryAPI::class.java)


        compositeDisposable?.add(retrofit.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponse))



    }

    private fun handleResponse(countryList: List<CountryModel>?) {
        countryModelList = countryList?.let { ArrayList(it) }
        firstCountryObj = countryModelList!![Utils.randomCountry().first()]
        secondCountryObj = countryModelList!![Utils.randomCountry().last()]
        firstCountryObj.imageUrl = Utils.getFlag(firstCountryObj.alpha2Code)
        secondCountryObj.imageUrl = Utils.getFlag(secondCountryObj.alpha2Code)

    }


    private fun startRollingDiceAnimation() {

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

}
