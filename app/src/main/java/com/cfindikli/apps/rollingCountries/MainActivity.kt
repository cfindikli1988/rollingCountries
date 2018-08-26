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
    internal lateinit var firstCountryName: String
    internal lateinit var secondCountryName: String
    private lateinit var reselected: Array<String>
    private var numberOfRoll = 5
    private val tieBreakRoll = IntArray(2)
    private var isMute = false
    private var currentDiceRollFirstCountry: Int = 0
    private var currentDiceRollSecondCountry: Int = 0
    private var bonusPoints1: Int = 0
    private var bonusPoints2: Int = 0
    private var sum1: Int = 0
    private var sum2 = 0
    private var aggregateFirstCountry: Int = 0
    private var aggregateSecondCountry = 0
    private var mp: MediaPlayer? = null
    private var uri1: Uri? = null
    private var uri2: Uri? = null
    private var firstCountryShortCode: String? = null
    private var secondCountryShortCode: String? = null
    private var imageUrl1: String? = null
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
        getIntentExtras(intent)
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

    private fun getIntentExtras(extras: Intent) {
        imageUrl1 = extras.getStringExtra("uri1")
        firstCountryName = extras.getStringExtra("firstCountryName")
        firstCountryShortCode = extras.getStringExtra("shortCode1")
        uri1 = Uri.parse(imageUrl1)

        val imageUrl2 = extras.getStringExtra("uri2")
        secondCountryName = extras.getStringExtra("secondCountryName")
        secondCountryShortCode = extras.getStringExtra("shortCode2")
        uri2 = Uri.parse(imageUrl2)
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
                currentDiceRollFirstCountry = Utils.randomDiceValue()[0]
                currentDiceRollSecondCountry = Utils.randomDiceValue()[1]

                val firstDice = resources.getIdentifier("dice_$currentDiceRollFirstCountry", "drawable", packageName)
                val secondDice = resources.getIdentifier("dice_$currentDiceRollSecondCountry", "drawable", packageName)

                if (animation === anim1) {
                    if (numberOfRoll != 0) {
                        singleRollDiceResultFirstCountry!!.setImageResource(firstDice)
                        sum1 += currentDiceRollFirstCountry
                        firstCountryResult.text = sum1.toString()
                        if (currentDiceRollFirstCountry == 6) {
                            bonusPoints1++
                        }
                        if (currentDiceRollFirstCountry == 1) {
                            bonusPoints1--
                        }
                    }

                } else if (animation === anim2) {
                    if (numberOfRoll != 0) {
                        singleRollDiceResultSecondCountry!!.setImageResource(secondDice)
                        sum2 += currentDiceRollSecondCountry
                        secondCountryResult.text = sum2.toString()
                        if (currentDiceRollSecondCountry == 6) {
                            bonusPoints2++
                        }
                        if (currentDiceRollSecondCountry == 1) {
                            bonusPoints2--
                        }
                        numberOfRoll--
                    }

                    if (numberOfRoll == 0) {
                        assessResult()
                        rollDiceButton!!.visibility = View.INVISIBLE
                    } else if (numberOfRoll == 1 && (sum1 - sum2 >= 6 || sum2 - sum1 >= 6)) {
                        assessResult()
                    } else if (numberOfRoll == 2 && (sum1 - sum2 >= 11 || sum2 - sum1 >= 11)) {
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

        Picasso.with(applicationContext).load(uri1).resize(400, 267)
                .into(firstCountryFlag!!, object : Callback {
                    override fun onSuccess() {
                        firstCountry.text = firstCountryName
                    }

                    override fun onError() {

                        firstCountry.text = firstCountryName
                        firstFlag = resources.getIdentifier("flag_" + firstCountryShortCode!!, "drawable", packageName)
                        firstCountryFlag!!.setImageResource(firstFlag!!)


                    }
                })

        Picasso.with(applicationContext).load(uri2).networkPolicy(NetworkPolicy.OFFLINE).resize(400, 267)
                .into(secondCountryFlag!!, object : Callback {
                    override fun onSuccess() {
                        secondCountry.text = secondCountryName

                    }

                    override fun onError() {

                        secondCountry.text = secondCountryName
                        val secondFlag = resources.getIdentifier("flag_" + secondCountryShortCode!!, "drawable", packageName)
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
        aggregate!!.text = resources.getString(R.string.text_aggregate) + aggregateFirstCountry.toString() + "-" + aggregateSecondCountry.toString()
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
            } while (reselected[1] == firstCountryName || reselected[1] == secondCountryName)

            firstCountryName = reselected[1]
            firstCountryShortCode = reselected[0].toLowerCase()
            imageUrl1 = Utils.getFlag(firstCountryShortCode).toString()
            uri1 = Uri.parse(imageUrl1)
            setUI()
            aggregateFirstCountry = 0
            aggregateSecondCountry = 0
            aggregate!!.visibility = View.INVISIBLE
            isChangeMyTeamSelected = false

        }

        rollDiceButton!!.visibility = View.VISIBLE
        remainingRoll!!.visibility = View.VISIBLE
        numberOfRoll = 5
        remainingRoll!!.text = resources.getString(R.string.text_remaining_roll) + numberOfRoll.toString()
        singleRollDiceResultFirstCountry!!.visibility = View.VISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.VISIBLE
        sum1 = 0
        sum2 = 0
        bonusPoints1 = 0
        bonusPoints2 = 0
        firstCountryResult.text = sum1.toString()
        secondCountryResult.text = sum2.toString()
        singleRollDiceResultFirstCountry!!.setImageResource(R.drawable.dice_6)
        singleRollDiceResultSecondCountry!!.setImageResource(R.drawable.dice_6)
        singleRollDiceResultFirstCountry!!.visibility = View.INVISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.INVISIBLE
        shakeDetector!!.start(sensorManager)
    }

    private fun assessResult() {

        shakeDetector!!.stop()

        if (sum1 > sum2) {


            if (sum1 - sum2 >= 11) {
                aggregateFirstCountry += 2
            } else ++aggregateFirstCountry
            TastyToast.makeText(applicationContext, "YOU WIN!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
            afterMatch(secondCountryFlag)
            Utils.throwKonfetti(konfettiView!!)
            changeTrack(1)
            Handler().postDelayed({ endGame(secondCountryFlag) }, 8000)

        } else if (sum2 > sum1) {

            if (sum2 - sum1 >= 11) {
                aggregateSecondCountry += 2
            } else ++aggregateSecondCountry
            TastyToast.makeText(applicationContext, "YOU LOSE!", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
            afterMatch(firstCountryFlag)
            Utils.setBW(firstCountryFlag!!, 0f)
            changeTrack(2)
            Handler().postDelayed({ endGame(firstCountryFlag) }, 4000)

        } else {
            if (bonusPoints1 > bonusPoints2) {
                aggregateFirstCountry++
                TastyToast.makeText(applicationContext, "YOU WIN!\nBonus Points: ($bonusPoints1)-($bonusPoints2)", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                afterMatch(secondCountryFlag)
                Utils.throwKonfetti(konfettiView!!)
                changeTrack(1)
                Handler().postDelayed({ endGame(secondCountryFlag) }, 8000)
            } else if (bonusPoints2 > bonusPoints1) {
                aggregateSecondCountry++
                TastyToast.makeText(this@MainActivity, "YOU LOSE!\nBonus Points: ($bonusPoints1)-($bonusPoints2)", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                afterMatch(firstCountryFlag)
                changeTrack(2)
                Handler().postDelayed({ endGame(firstCountryFlag) }, 4000)
            } else {
                tieBreakRoll[0] = Utils.randomDiceValue()[0]
                tieBreakRoll[1] = Utils.randomDiceValue()[1]
                if (tieBreakRoll[0] == tieBreakRoll[1]) {
                    aggregateFirstCountry++
                    TastyToast.makeText(this@MainActivity, "YOU WIN!\n" + "Bonus Points: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")" + " TieBreak Roll: " + tieBreakRoll[0] + "-" + tieBreakRoll[1], TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                    afterMatch(secondCountryFlag)
                    Utils.throwKonfetti(konfettiView!!)
                    changeTrack(1)
                    Handler().postDelayed({ endGame(secondCountryFlag) }, 8000)
                } else {
                    aggregateSecondCountry++
                    TastyToast.makeText(this@MainActivity, "YOU LOSE!\n" + "Bonus Points: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")" + " TieBreak Roll: " + tieBreakRoll[0] + "-" + tieBreakRoll[1], TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
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









