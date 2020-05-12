package com.cfindikli.apps.rollingCountries.model

import android.net.Uri

data class CountryModel(
		
		var name: String? = null,
		var alpha2Code: String? = null) {
	var imageUrl: Uri? = null
	var flag: Int? = null
	val levelName = listOf("Round 1", "Round 2", "Round 3", "Round 4", "Round 5", "QuarterFinal", "SemiFinal", "Final")
	var level: Int = 0
	var sum: Int = 0
	var currentDiceRoll: Int = 0
	var reselectType: Int = 0
	var numberOfRoll = 3
}