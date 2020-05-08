package com.cfindikli.apps.rollingCountries.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cfindikli.apps.rollingCountries.Model.CountryModel
import com.cfindikli.apps.rollingCountries.R
import kotlinx.android.synthetic.main.row_layout.view.*

class RecyclerViewAdapter(private val countryList: ArrayList<CountryModel>, private val listener: Listener) : RecyclerView.Adapter<RecyclerViewAdapter.RowHolder>() {

    interface Listener {
        fun onItemClick(countryModel: CountryModel)
    }

    private val colors: Array<String> = arrayOf("#13bd27", "#29c1e1", "#b129e1", "#d3df13", "#f6bd0c", "#a1fb93", "#0d9de3", "#ffe48f")

    class RowHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(countryModel: CountryModel, colors: Array<String>, position: Int, listener: Listener) {

            itemView.setOnClickListener {
                listener.onItemClick(countryModel)
            }
            itemView.setBackgroundColor(Color.parseColor(colors[position % 8]))
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