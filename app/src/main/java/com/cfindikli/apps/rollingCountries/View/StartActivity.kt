package com.cfindikli.apps.rollingCountries.View

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cfindikli.apps.rollingCountries.Adapter.RecyclerViewAdapter
import com.cfindikli.apps.rollingCountries.MainActivity
import com.cfindikli.apps.rollingCountries.Model.CountryModel
import com.cfindikli.apps.rollingCountries.R
import com.cfindikli.apps.rollingCountries.Service.CountryAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_start.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity(), RecyclerViewAdapter.Listener {


    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var compositeDisposable: CompositeDisposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        compositeDisposable = CompositeDisposable()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager


        loadData()
    }


    private fun switchToMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }


    private fun loadData() {


        val retrofit = Retrofit.Builder()
                .baseUrl(com.cfindikli.apps.rollingCountries.Utils.Companion.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(CountryAPI::class.java)


        compositeDisposable?.add(retrofit.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponse))


    }


    private fun handleResponse(countryList: List<CountryModel>) {
        com.cfindikli.apps.rollingCountries.Utils.Companion.countryModelList = ArrayList(countryList)

        com.cfindikli.apps.rollingCountries.Utils.Companion.countryModelList.let {
            recyclerViewAdapter = RecyclerViewAdapter(it as ArrayList<CountryModel>, this@StartActivity)
            recyclerView.adapter = recyclerViewAdapter
        }
    }


    override fun onItemClick(countryModel: CountryModel) {
        Toast.makeText(this, "Clicked : ${countryModel.name}", Toast.LENGTH_LONG).show()
        com.cfindikli.apps.rollingCountries.Utils.Companion.firstCountryObj = countryModel
        com.cfindikli.apps.rollingCountries.Utils.Companion.secondCountryObj = com.cfindikli.apps.rollingCountries.Utils.Companion.countryModelList!![com.cfindikli.apps.rollingCountries.Utils.Companion.randomCountry().last()]
        com.cfindikli.apps.rollingCountries.Utils.Companion.firstCountryObj.imageUrl = com.cfindikli.apps.rollingCountries.Utils.Companion.getFlag(com.cfindikli.apps.rollingCountries.Utils.Companion.firstCountryObj.alpha2Code)
        com.cfindikli.apps.rollingCountries.Utils.Companion.secondCountryObj.imageUrl = com.cfindikli.apps.rollingCountries.Utils.Companion.getFlag(com.cfindikli.apps.rollingCountries.Utils.Companion.secondCountryObj.alpha2Code)
        switchToMainActivity()
    }


}
