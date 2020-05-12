package com.cfindikli.apps.rollingCountries.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cfindikli.apps.rollingCountries.R
import com.cfindikli.apps.rollingCountries.adapter.RecyclerViewAdapter
import com.cfindikli.apps.rollingCountries.model.CountryModel
import com.cfindikli.apps.rollingCountries.service.CountryAPI
import com.cfindikli.apps.rollingCountries.utils.Utils
import com.sdsmdg.tastytoast.TastyToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_start.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity(), RecyclerViewAdapter.Listener {
	
	
	private var recyclerViewAdapter: RecyclerViewAdapter? = null
	private var compositeDisposable: CompositeDisposable? = null
	
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_start)
		TastyToast.makeText(applicationContext, "Select Your Country", TastyToast.LENGTH_SHORT, TastyToast.DEFAULT).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
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
				.baseUrl(Utils.BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build().create(CountryAPI::class.java)
		
		
		compositeDisposable?.add(retrofit.getData()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponse))
		
		
	}
	
	
	private fun handleResponse(countryList: List<CountryModel>) {
		Utils.countryModelList = ArrayList(countryList)
		Utils.countryModelList.let {
			recyclerViewAdapter = RecyclerViewAdapter(it as ArrayList<CountryModel>, this@StartActivity)
			recyclerView.adapter = recyclerViewAdapter
		}
	}
	
	private fun showBeforeMatchDialog(countryModel: CountryModel) {
		
		val alertDialog = AlertDialog.Builder(this@StartActivity)
		alertDialog.setMessage(Html.fromHtml("Are you sure want to proceed your selected country as " + "<b>" + countryModel.name!!.toUpperCase(Locale.ENGLISH) + "</b>" + " ?", Html.FROM_HTML_MODE_LEGACY))
		alertDialog.setCancelable(false)
		
		alertDialog.setPositiveButton(Html.fromHtml("<font color='#3342FF'>Yes</font>")) { _, _ ->
			Utils.firstCountryObj = countryModel
			Utils.firstCountryObj.imageUrl = Utils.getFlag(Utils.firstCountryObj.alpha2Code)
			TastyToast.makeText(applicationContext, "Selected Country: ${countryModel.name}", TastyToast.LENGTH_SHORT, TastyToast.DEFAULT).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
			Utils.reselect(Utils.secondCountryObj)
			switchToMainActivity()
		}
		
		alertDialog.setNegativeButton(Html.fromHtml("<font color='#3342FF'>No</font>")) { dialog, _ ->
			
			dialog.cancel()
		}
		
		val alert = alertDialog.create()
		alert.show()
		
		val mw = alert.findViewById<TextView>(android.R.id.message)
		mw.gravity = Gravity.CENTER
		
	}
	
	
	override fun onItemClick(countryModel: CountryModel) {
		showBeforeMatchDialog(countryModel)
	}
	
	
}
