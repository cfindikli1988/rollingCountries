package com.cfindikli.apps.rollingCountries

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.cfindikli.apps.rollingCountries.Utils.Companion.firstCountryObj
import com.cfindikli.apps.rollingCountries.Utils.Companion.secondCountryObj
import com.sdsmdg.tastytoast.TastyToast
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.seismic.ShakeDetector
import kotlinx.android.synthetic.main.animation_activty.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(), ShakeDetector.Listener {

    private val song = arrayOf(R.raw.dicerolleffect, R.raw.queenwearethechampions, R.raw.whawha)
    private lateinit var reselected: Array<String>
    private var numberOfRoll = 5
    private val tieBreakRoll = IntArray(2)
    private var isMute = false
    private var mp: MediaPlayer? = null
    private var isChangeMyTeamSelected = false
    private var firstFlag: Int? = null
    private var shakeDetector: ShakeDetector? = null
    private var sensorManager: SensorManager? = null
    private var anim1: Animation? = null
    private var anim2: Animation? = null



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.animation_activty)
        adjustUIComponent()
        initializeMediaPlayer()
        setListeners()
        setUI()
        initializeShakeDetector()
    }


    private fun initializeMediaPlayer() {
        mp = MediaPlayer.create(this, R.raw.dicerolleffect)
    }


    @SuppressLint("SetTextI18n")
    internal fun adjustUIComponent() {
        aggregate!!.visibility = View.INVISIBLE
        remainingRoll!!.text = resources.getString(R.string.text_remaining_roll) + numberOfRoll
        singleRollDiceResultFirstCountry!!.visibility = View.INVISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.INVISIBLE
    }




    private fun initializeShakeDetector() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector(this)
        shakeDetector!!.start(sensorManager)
    }

    private fun setListeners() {
        rollDiceButton!!.setOnClickListener(ClickListener())
        volumeIcon!!.setOnClickListener(ClickListener())
    }


    internal fun volumeOnOff() {
        isMute = !isMute

        if (isMute) {
            volumeIcon!!.setImageResource(R.drawable.mute)
            mp!!.setVolume(0f, 0f)
        } else {
            isMute = false
            volumeIcon!!.setImageResource(R.drawable.volume)
            mp!!.setVolume(1f, 1f)
        }

    }

    internal fun rollDice() {


        anim1 = AnimationUtils.loadAnimation(this@MainActivity, R.anim.shake)
        anim2 = AnimationUtils.loadAnimation(this@MainActivity, R.anim.shake)
        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

                rollDiceButton!!.visibility = View.INVISIBLE
                changeTrack(0)
                singleRollDiceResultFirstCountry!!.visibility = View.VISIBLE
                singleRollDiceResultSecondCountry!!.visibility = View.VISIBLE
            }

            @SuppressLint("SetTextI18n")
            override fun onAnimationEnd(animation: Animation) {

                firstCountryObj.currentDiceRoll = Utils.randomDiceValue()[0]
                secondCountryObj.currentDiceRoll = Utils.randomDiceValue()[1]

                val firstDice = resources.getIdentifier("dice_${firstCountryObj.currentDiceRoll}", "drawable", packageName)
                val secondDice = resources.getIdentifier("dice_${secondCountryObj.currentDiceRoll}", "drawable", packageName)

                if (animation === anim1) {
                    if (numberOfRoll != 0) {
                        singleRollDiceResultFirstCountry!!.setImageResource(firstDice)
                        firstCountryObj.sum += firstCountryObj.currentDiceRoll
                        firstCountryResult.text = firstCountryObj.sum.toString()
                        if (firstCountryObj.currentDiceRoll == 6) {
                            firstCountryObj.bonusPoint++
                        }
                        if (firstCountryObj.currentDiceRoll == 1) {
                            firstCountryObj.bonusPoint--
                        }
                    }

                } else if (animation === anim2) {
                    if (numberOfRoll != 0) {
                        singleRollDiceResultSecondCountry!!.setImageResource(secondDice)
                        secondCountryObj.sum += secondCountryObj.currentDiceRoll
                        secondCountryResult.text = secondCountryObj.sum.toString()
                        if (secondCountryObj.currentDiceRoll == 6) {
                            secondCountryObj.bonusPoint++
                        }
                        if (secondCountryObj.currentDiceRoll == 1) {
                            secondCountryObj.bonusPoint--
                        }
                        numberOfRoll--
                    }

                    if (numberOfRoll == 0) {
                        assessResult()
                        rollDiceButton!!.visibility = View.INVISIBLE
                    } else if (numberOfRoll == 1 && (firstCountryObj.sum - secondCountryObj.sum >= 6 || secondCountryObj.sum - firstCountryObj.sum >= 6)) {
                        assessResult()
                    } else if (numberOfRoll == 2 && (firstCountryObj.sum - secondCountryObj.sum >= 11 || secondCountryObj.sum - firstCountryObj.sum >= 11)) {
                        assessResult()
                    } else
                        rollDiceButton!!.visibility = View.VISIBLE

                }

                remainingRoll!!.text = resources.getString(R.string.text_remaining_roll) + numberOfRoll.toString()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        }

        anim1!!.setAnimationListener(animationListener)
        anim2!!.setAnimationListener(animationListener)

        singleRollDiceResultFirstCountry!!.startAnimation(anim1)
        singleRollDiceResultSecondCountry!!.startAnimation(anim2)

    }

    private fun setUI() {

        Picasso.with(applicationContext).load(firstCountryObj.imageUrl).resize(400, 267)
                .into(firstCountryFlag!!, object : Callback {
                    override fun onSuccess() {
                        firstCountry.text = firstCountryObj.countryName
                    }

                    override fun onError() {

                        firstCountry.text = firstCountryObj.countryName
                        firstFlag = resources.getIdentifier("flag_" + firstCountryObj.shortCode, "drawable", packageName)
                        firstCountryFlag!!.setImageResource(firstFlag!!)


                    }
                })

        Picasso.with(applicationContext).load(secondCountryObj.imageUrl).networkPolicy(NetworkPolicy.OFFLINE).resize(400, 267)
                .into(secondCountryFlag!!, object : Callback {
                    override fun onSuccess() {
                        secondCountry.text = secondCountryObj.countryName

                    }

                    override fun onError() {

                        secondCountry.text = secondCountryObj.countryName
                        val secondFlag = resources.getIdentifier("flag_" + secondCountryObj.shortCode, "drawable", packageName)
                        secondCountryFlag!!.setImageResource(secondFlag)


                    }
                })
    }


    private fun changeTrack(position: Int) {
        mp!!.stop()
        mp!!.reset()
        try {
            mp!!.setDataSource(applicationContext, Uri.parse("android.resource://" + packageName + "/" + song[position]))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            mp!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        mp!!.start()
    }

    private fun endGame(imageView: ImageView?) {
        mp!!.stop()
        Utils.setBW(imageView!!, 1f)
        val builder1 = AlertDialog.Builder(this@MainActivity)
        builder1.setMessage("What would you like to do next?")
        builder1.setCancelable(false)

        builder1.setPositiveButton(
                "Change Both Teams"
        ) { dialog, _ ->
            onBackPressed()
            dialog.cancel()
        }

        builder1.setNegativeButton(
                "Keep Both Teams"
        ) { dialog, _ ->
            rematch()
            dialog.cancel()
        }
        builder1.setNeutralButton("Change My Team Only") { dialog, _ ->
            isChangeMyTeamSelected = true
            rematch()
            dialog.cancel()

        }

        val alert11 = builder1.create()
        alert11.show()
    }

    @SuppressLint("SetTextI18n")
    private fun afterMatch(imageView: ImageView?) {
        remainingRoll!!.visibility = View.INVISIBLE
        aggregate!!.visibility = View.VISIBLE
        aggregate!!.text = resources.getString(R.string.text_aggregate) + firstCountryObj.aggregate.toString() + "-" + secondCountryObj.aggregate.toString()
        Utils.setBW(imageView!!, 0f)
    }


    private fun reselect() {
        reselected = Objects.requireNonNull(Objects.requireNonNull<List<Any>>(Utils.response)[Utils.randomCountry()[Random().nextInt(2)]]).toString().split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }


    @SuppressLint("ResourceType", "SetTextI18n")
    private fun rematch() {
        if (isChangeMyTeamSelected) {

            do {
                reselect()
            } while (reselected[1] == firstCountryObj.countryName || reselected[1] == secondCountryObj.countryName)

            firstCountryObj.countryName = reselected[1]
            firstCountryObj.shortCode = reselected[0].toLowerCase()
            firstCountryObj.imageUrl = Utils.getFlag(firstCountryObj.shortCode)
            setUI()
            firstCountryObj.aggregate = 0
            secondCountryObj.aggregate = 0
            aggregate!!.visibility = View.INVISIBLE
            isChangeMyTeamSelected = false


        }

        rollDiceButton!!.visibility = View.VISIBLE
        remainingRoll!!.visibility = View.VISIBLE
        numberOfRoll = 5
        remainingRoll!!.text = resources.getString(R.string.text_remaining_roll) + numberOfRoll.toString()
        singleRollDiceResultFirstCountry!!.visibility = View.VISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.VISIBLE
        firstCountryObj.sum = 0
        secondCountryObj.sum = 0
        firstCountryObj.bonusPoint = 0
        secondCountryObj.bonusPoint = 0
        firstCountryResult.text = firstCountryObj.sum.toString()
        secondCountryResult.text = secondCountryObj.sum.toString()
        singleRollDiceResultFirstCountry!!.setImageResource(R.drawable.dice_6)
        singleRollDiceResultSecondCountry!!.setImageResource(R.drawable.dice_6)
        singleRollDiceResultFirstCountry!!.visibility = View.INVISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.INVISIBLE
        shakeDetector!!.start(sensorManager)
    }

    private fun assessResult() {

        shakeDetector!!.stop()

        if (firstCountryObj.sum > secondCountryObj.sum) {

            if (firstCountryObj.sum - secondCountryObj.sum >= 11) {
                firstCountryObj.aggregate += 2
            } else ++firstCountryObj.aggregate
            TastyToast.makeText(applicationContext, "YOU WIN!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
            afterMatch(secondCountryFlag)
            Utils.throwKonfetti(konfettiView!!)
            changeTrack(1)
            Handler().postDelayed({ endGame(secondCountryFlag) }, 8000)

        } else if (secondCountryObj.sum > firstCountryObj.sum) {

            if (secondCountryObj.sum - firstCountryObj.sum >= 11) {
                secondCountryObj.aggregate += 2
            } else ++secondCountryObj.aggregate
            TastyToast.makeText(applicationContext, "YOU LOSE!", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
            afterMatch(firstCountryFlag)
            Utils.setBW(firstCountryFlag!!, 0f)
            changeTrack(2)
            Handler().postDelayed({ endGame(firstCountryFlag) }, 4000)

        } else {
            if (firstCountryObj.bonusPoint > secondCountryObj.bonusPoint) {
                firstCountryObj.aggregate++
                TastyToast.makeText(applicationContext, "YOU WIN!\nBonus Points: (${firstCountryObj.bonusPoint})-(${secondCountryObj.bonusPoint})", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                afterMatch(secondCountryFlag)
                Utils.throwKonfetti(konfettiView!!)
                changeTrack(1)
                Handler().postDelayed({ endGame(secondCountryFlag) }, 8000)
            } else if (secondCountryObj.bonusPoint > firstCountryObj.bonusPoint) {
                secondCountryObj.aggregate++
                TastyToast.makeText(this@MainActivity, "YOU LOSE!\nBonus Points: (${firstCountryObj.bonusPoint})-(${secondCountryObj.bonusPoint})", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                afterMatch(firstCountryFlag)
                changeTrack(2)
                Handler().postDelayed({ endGame(firstCountryFlag) }, 4000)
            } else {
                tieBreakRoll[0] = Utils.randomDiceValue()[0]
                tieBreakRoll[1] = Utils.randomDiceValue()[1]
                if (tieBreakRoll[0] == tieBreakRoll[1]) {
                    firstCountryObj.aggregate++
                    TastyToast.makeText(this@MainActivity, "YOU WIN!\n" + "Bonus Points: " + "(" + firstCountryObj.bonusPoint + ")" + "-" + "(" + secondCountryObj.bonusPoint + ")" + " TieBreak Roll: " + tieBreakRoll[0] + "-" + tieBreakRoll[1], TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                    afterMatch(secondCountryFlag)
                    Utils.throwKonfetti(konfettiView!!)
                    changeTrack(1)
                    Handler().postDelayed({ endGame(secondCountryFlag) }, 8000)
                } else {
                    secondCountryObj.aggregate++
                    TastyToast.makeText(this@MainActivity, "YOU LOSE!\n" + "Bonus Points: " + "(" + firstCountryObj.bonusPoint + ")" + "-" + "(" + secondCountryObj.bonusPoint + ")" + " TieBreak Roll: " + tieBreakRoll[0] + "-" + tieBreakRoll[1], TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                    afterMatch(firstCountryFlag)
                    changeTrack(2)
                    Handler().postDelayed({ endGame(firstCountryFlag) }, 4000)
                }

            }

        }
    }


    override fun onBackPressed() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    override fun hearShake() {
        rollDice()
    }

    internal inner class ClickListener : View.OnClickListener {

        override fun onClick(view: View) {
            when (view.id) {
                R.id.rollDiceButton -> rollDice()
                R.id.volumeIcon -> volumeOnOff()
            }
        }
    }


}









