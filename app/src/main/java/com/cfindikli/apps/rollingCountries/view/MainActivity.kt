package com.cfindikli.apps.rollingCountries.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cfindikli.apps.rollingCountries.R
import com.cfindikli.apps.rollingCountries.model.CountryModel
import com.cfindikli.apps.rollingCountries.utils.Utils
import com.sdsmdg.tastytoast.TastyToast
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.seismic.ShakeDetector
import kotlinx.android.synthetic.main.animation_activty.*
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), ShakeDetector.Listener {


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
        Utils.mp = MediaPlayer.create(applicationContext, R.raw.dicerolleffect)
    }


    @SuppressLint("SetTextI18n")
    internal fun adjustUIComponent() {
        levelName.text = Utils.firstCountryObj.levelName[Utils.firstCountryObj.level]
        remainingRoll!!.text = resources.getString(R.string.text_remaining_roll) + Utils.firstCountryObj.numberOfRoll
        resetValues()
        singleRollDiceResultFirstCountry!!.visibility = View.INVISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.INVISIBLE
    }


    private fun initializeShakeDetector() {
        Utils.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Utils.shakeDetector = ShakeDetector(this)
        Utils.shakeDetector!!.start(Utils.sensorManager,SENSOR_DELAY_GAME)
    }

    private fun setListeners() {
        rollDiceButton!!.setOnClickListener(ClickListener())
        volumeIcon!!.setOnClickListener(ClickListener())
    }


    internal fun volumeOnOff() {
        Utils.isMute = !Utils.isMute
        when {
            Utils.isMute -> {
                volumeIcon!!.setImageResource(R.drawable.mute)
                Utils.mp!!.setVolume(0f, 0f)
            }
            else -> {
                Utils.isMute = false
                volumeIcon!!.setImageResource(R.drawable.volume)
                Utils.mp!!.setVolume(1f, 1f)
            }
        }
    }

    internal fun rollDice() {
        Utils.anim1 = AnimationUtils.loadAnimation(this@MainActivity, R.anim.shake)
        Utils.anim2 = AnimationUtils.loadAnimation(this@MainActivity, R.anim.shake)
        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                rollDiceButton!!.visibility = View.INVISIBLE
                changeTrack(0)
                singleRollDiceResultFirstCountry!!.visibility = View.VISIBLE
                singleRollDiceResultSecondCountry!!.visibility = View.VISIBLE
            }

            @SuppressLint("SetTextI18n")
            override fun onAnimationEnd(animation: Animation) {
                Utils.firstCountryObj.currentDiceRoll = Utils.randomDiceValue()[0]
                Utils.secondCountryObj.currentDiceRoll = Utils.randomDiceValue()[1]
                val firstDice = resources.getIdentifier("dice_${Utils.firstCountryObj.currentDiceRoll}", "drawable", packageName)
                val secondDice = resources.getIdentifier("dice_${Utils.secondCountryObj.currentDiceRoll}", "drawable", packageName)


                when {
                    Utils.firstCountryObj.numberOfRoll != 0 -> {

                        when (animation) {
                            Utils.anim1 -> {
                                singleRollDiceResultFirstCountry!!.setImageResource(firstDice)
                                Utils.firstCountryObj.sum += Utils.firstCountryObj.currentDiceRoll
                                firstCountryResult.text = Utils.firstCountryObj.sum.toString()
                                singleRollDiceResultSecondCountry!!.setImageResource(secondDice)
                                Utils.secondCountryObj.sum += Utils.secondCountryObj.currentDiceRoll
                                secondCountryResult.text = Utils.secondCountryObj.sum.toString()
                                Utils.firstCountryObj.numberOfRoll--
                                remainingRoll!!.text = resources.getString(R.string.text_remaining_roll) + Utils.firstCountryObj.numberOfRoll.toString()
                                assesEarlyWinning()
                            }

                        }
                    }
                    else -> {
                        assessResult()
                    }
                }

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        }

        Utils.anim1!!.setAnimationListener(animationListener)
        Utils.anim2!!.setAnimationListener(animationListener)

        singleRollDiceResultFirstCountry!!.startAnimation(Utils.anim1)
        singleRollDiceResultSecondCountry!!.startAnimation(Utils.anim2)

    }

    private fun setUI() {
        Picasso.get().load(Utils.firstCountryObj.imageUrl).resize(400, 267)
                .into(firstCountryFlag!!, object : Callback {
                    override fun onSuccess() {
                        firstCountry.text = Utils.firstCountryObj.name
                    }

                    override fun onError(e: Exception?) {
                        firstCountry.text = Utils.firstCountryObj.name
                        Utils.firstCountryObj.flag = resources.getIdentifier("flag_" + Utils.firstCountryObj.alpha2Code, "drawable", packageName)
                        firstCountryFlag!!.setImageResource(Utils.firstCountryObj.flag!!)
                    }
                })

        Picasso.get().load(Utils.secondCountryObj.imageUrl).resize(400, 267)
                .into(secondCountryFlag!!, object : Callback {
                    override fun onSuccess() {
                        secondCountry.text = Utils.secondCountryObj.name
                    }

                    override fun onError(e: Exception?) {
                        secondCountry.text = Utils.secondCountryObj.name
                        Utils.secondCountryObj.flag = resources.getIdentifier("flag_" + Utils.secondCountryObj.alpha2Code, "drawable", packageName)
                        secondCountryFlag!!.setImageResource(Utils.secondCountryObj.flag!!)
                    }
                })
    }


    private fun changeTrack(position: Int) {
        Utils.mp!!.stop()
        Utils.mp!!.reset()
        try {
            Utils.mp!!.setDataSource(applicationContext, Uri.parse("android.resource://" + packageName + "/" + Utils.song[position]))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            Utils.mp!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        Utils.mp!!.start()
    }

    private fun endGame(imageView: ImageView?) {
        Utils.mp!!.stop()
        Utils.setBW(imageView!!, 1f)
        showAfterMatchDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun afterMatch(imageView: ImageView?) {
        rollDiceButton!!.visibility = View.INVISIBLE
        remainingRoll!!.visibility = View.INVISIBLE
        Utils.setBW(imageView!!, 0f)
    }


    @SuppressLint("ResourceType", "SetTextI18n")
    private fun rematch(level: Int, reselectType: Int) {
        when {

            level < Utils.firstCountryObj.levelName.size -> {

                when (reselectType) {
                    1 -> {
                        Utils.reselect(Utils.firstCountryObj)
                        goBackPreviousActivity()
                    }
                    2 -> {
                        Utils.reselect(Utils.secondCountryObj)
                        TastyToast.makeText(applicationContext, "Your Next Opponent: ${Utils.secondCountryObj.name}", TastyToast.LENGTH_SHORT, TastyToast.DEFAULT).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                    }
                    3 -> {
                        Utils.reselect(Utils.firstCountryObj)
                        Utils.reselect(Utils.secondCountryObj)
                    }
                }
                setUI()
            }
        }
        resetValues()
        Utils.shakeDetector!!.start(Utils.sensorManager, SENSOR_DELAY_GAME)
    }

    private fun assessResult() {
        Utils.shakeDetector!!.stop()
        when {
            Utils.firstCountryObj.sum > Utils.secondCountryObj.sum -> {
                ++Utils.firstCountryObj.level
                afterMatch(secondCountryFlag)
                winningCeremony(Utils.firstCountryObj.level)
            }
            Utils.secondCountryObj.sum > Utils.firstCountryObj.sum -> {
                Utils.firstCountryObj.level = 0
                TastyToast.makeText(applicationContext, "YOU LOSE!", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                afterMatch(firstCountryFlag)
                Utils.setBW(firstCountryFlag!!, 0f)
                changeTrack(2)
                Handler().postDelayed({ endGame(firstCountryFlag) }, 4000)
            }
            else -> {
                ++Utils.firstCountryObj.numberOfRoll
                rollDiceButton.text = resources.getString(R.string.text_tie_break)
                remainingRoll!!.visibility = View.INVISIBLE
            }
        }

    }

    private fun assesEarlyWinning() {

        when {
            (Utils.firstCountryObj.numberOfRoll == 1 && (Utils.firstCountryObj.sum - Utils.secondCountryObj.sum >= 6 || Utils.secondCountryObj.sum - Utils.firstCountryObj.sum >= 6)) -> {
                assessResult()
            }
            (Utils.firstCountryObj.numberOfRoll == 2 && (Utils.firstCountryObj.sum - Utils.secondCountryObj.sum >= 11 || Utils.secondCountryObj.sum - Utils.firstCountryObj.sum >= 11)) -> {
                assessResult()
            }
            else -> {
                rollDiceButton!!.visibility = View.VISIBLE
            }
        }
    }

    private fun winningCeremony(level: Int) {
        when (level) {
            Utils.firstCountryObj.levelName.size -> {
                Utils.throwConfetti(konfettiView!!)
                changeTrack(1)
                TastyToast.makeText(applicationContext, "THE WORLD CHAMPIONS\n" + Utils.firstCountryObj.name!!.toUpperCase(Locale.ENGLISH), TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                Handler().postDelayed({ endWinningCeremony(secondCountryFlag) }, 37000)
            }
            else -> {
                TastyToast.makeText(applicationContext, "YOU WIN!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                Handler().postDelayed({ endWinningCeremony(secondCountryFlag) }, 4000)
            }
        }
    }


    private fun endWinningCeremony(imageView: ImageView?) {

        Utils.mp!!.stop()
        Utils.setBW(imageView!!, 1f)

        when {
            Utils.firstCountryObj.level < Utils.firstCountryObj.levelName.size -> {
                Utils.firstCountryObj.reselectType = 2
                rematch(Utils.firstCountryObj.level, Utils.firstCountryObj.reselectType)
            }
            else -> {
                showAfterMatchDialog()
            }
        }

    }

    private fun resetValues() {
        Utils.firstCountryObj.numberOfRoll = CountryModel().numberOfRoll
        Utils.firstCountryObj.sum = 0
        Utils.secondCountryObj.sum = 0
        levelName.text = Utils.firstCountryObj.levelName[Utils.firstCountryObj.level]
        rollDiceButton!!.visibility = View.VISIBLE
        rollDiceButton!!.text = resources.getString(R.string.text_roll_dice)
        remainingRoll!!.visibility = View.VISIBLE
        remainingRoll!!.text = resources.getString(R.string.text_remaining_roll).plus(Utils.firstCountryObj.numberOfRoll.toString())
        singleRollDiceResultFirstCountry!!.visibility = View.VISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.VISIBLE
        firstCountryResult.text = Utils.firstCountryObj.sum.toString()
        secondCountryResult.text = Utils.secondCountryObj.sum.toString()
        singleRollDiceResultFirstCountry!!.setImageResource(R.drawable.dice_6)
        singleRollDiceResultSecondCountry!!.setImageResource(R.drawable.dice_6)
        singleRollDiceResultFirstCountry!!.visibility = View.INVISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.INVISIBLE
    }

    private fun showAfterMatchDialog() {

        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setMessage(R.string.after_match_text)
        alertDialog.setCancelable(false)

        alertDialog.setNeutralButton(Html.fromHtml("<font color='#3342FF'>Change Both Teams (Automatically)</font>")) { _, _ ->
            Utils.firstCountryObj.reselectType = 3
            rematch(Utils.firstCountryObj.level, Utils.firstCountryObj.reselectType)
        }

        alertDialog.setNegativeButton(Html.fromHtml("<font color='#3342FF'>Change Opponent (Automatically)</font>")) { dialog, _ ->
            Utils.firstCountryObj.reselectType = 2
            rematch(Utils.firstCountryObj.level, Utils.firstCountryObj.reselectType)
            dialog.cancel()
        }

        alertDialog.setPositiveButton(Html.fromHtml("<font color='#3342FF'>Just Change My Team (Manually)</font>")) { dialog, _ ->
            Utils.firstCountryObj.reselectType = 1
            rematch(Utils.firstCountryObj.level, Utils.firstCountryObj.reselectType)
            dialog.cancel()
        }


        val alert = alertDialog.create()
        alert.show()

        val mw = alert.findViewById<TextView>(android.R.id.message)
        mw.gravity = Gravity.CENTER

    }

    private fun showQuitDialog() {

        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setMessage(R.string.exit_game_text)
        alertDialog.setCancelable(false)

        alertDialog.setPositiveButton(Html.fromHtml("<font color='#3342FF'>Yes</font>")) { _, _ ->
            finishAffinity()

        }

        alertDialog.setNegativeButton(Html.fromHtml("<font color='#3342FF'>No</font>")) { dialog, _ ->
            dialog.cancel()
        }

        val alert = alertDialog.create()
        alert.show()

        val mw = alert.findViewById<TextView>(android.R.id.message)
        mw.gravity = Gravity.CENTER

    }

    private fun goBackPreviousActivity() {
        val i = Intent(this, StartActivity::class.java)
        startActivity(i)
    }

    override fun onBackPressed() {
        showQuitDialog()
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









