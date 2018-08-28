package com.cfindikli.apps.rollingCountries

import android.net.Uri

data class Country(var countryName: String? = null, var imageUrl: Uri? = null, var shortCode: String? = null, var flag: Int? = null) {
    var sum: Int = 0
    var bonusPoint: Int = 0
    var currentDiceRoll: Int = 0
    var aggregate: Int = 0
    var numberOfRoll = 5
    var tieBreakRoll = IntArray(2)
    lateinit var reselected: Array<String>
}