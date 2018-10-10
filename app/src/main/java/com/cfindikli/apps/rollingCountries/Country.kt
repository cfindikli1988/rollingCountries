package com.cfindikli.apps.rollingCountries

import android.net.Uri

data class Country(var countryName: String? = null, var imageUrl: Uri? = null, var shortCode: String? = null, var flag: Int? = null) {

    var level: Int = 0
    var levelName = listOf("Round 1", "Round 2", "Round 3", "Round 4", "Round 5", "QuarterFinal", "SemiFinal", "Final")
    var sum: Int = 0
    var currentDiceRoll: Int = 0
    var numberOfRoll = 1
    var reselected: Array<String>? = null
    var reselectType: Int = 0
}