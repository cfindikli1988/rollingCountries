package com.cfindikli.apps.rollingCountries.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cfindikli.apps.rollingCountries.R
import com.cfindikli.apps.rollingCountries.model.CountryModel
import kotlinx.android.synthetic.main.row_layout.view.*

class RecyclerViewAdapter(private val countryList: ArrayList<CountryModel>, private val listener: Listener) : RecyclerView.Adapter<RecyclerViewAdapter.RowHolder>() {

    interface Listener {
        fun onItemClick(countryModel: CountryModel)
    }
    
    private val colors: Array<String> = arrayOf("#cca6ac", "#f691b2", "#f83a22", "#d06b64", "#fa573c", "#ff7537", "#ffad46", "#fad165", "#fbe983", "#b3dc6c", "#7bd148", "#92e1c0", "#16a765", "#42d692", "#9fc6e7", "#4986e7", "#9a9cff", "#b99aff", "#a47ae2", "#cd74e6", "#ac725e", "#c2c2c2", "#cabdbf")

    class RowHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(countryModel: CountryModel, colors: Array<String>, position: Int, listener: Listener) {

            itemView.setOnClickListener {
                listener.onItemClick(countryModel)
            }
            itemView.setBackgroundColor(Color.parseColor(colors[position % 23]))
            itemView.text_name.text = countryModel.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return countryList.count()
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(countryList[position], colors, position, listener)
    }


}