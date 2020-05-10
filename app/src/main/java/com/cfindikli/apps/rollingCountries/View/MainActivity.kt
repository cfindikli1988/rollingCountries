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
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cfindikli.apps.rollingCountries.Model.CountryModel
import com.cfindikli.apps.rollingCountries.Utils.Companion.firstCountryObj
import com.cfindikli.apps.rollingCountries.Utils.Companion.secondCountryObj
import com.cfindikli.apps.rollingCountries.View.StartActivity
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
        levelName.text = firstCountryObj.levelName[firstCountryObj.level]
        remainingRoll!!.text = resources.getString(R.string.text_remaining_roll) + firstCountryObj.numberOfRoll
        resetValues()
        singleRollDiceResultFirstCountry!!.visibility = View.INVISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.INVISIBLE
    }


    private fun initializeShakeDetector() {
        Utils.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Utils.shakeDetector = ShakeDetector(this)
        Utils.shakeDetector!!.start(Utils.sensorManager)
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
                firstCountryObj.currentDiceRoll = Utils.randomDiceValue()[0]
                secondCountryObj.currentDiceRoll = Utils.randomDiceValue()[1]
                val firstDice = resources.getIdentifier("dice_${firstCountryObj.currentDiceRoll}", "drawable", packageName)
                val secondDice = resources.getIdentifier("dice_${secondCountryObj.currentDiceRoll}", "drawable", packageName)


                when {
                    firstCountryObj.numberOfRoll != 0 -> {

                        when (animation) {
                            Utils.anim1 -> {
                                singleRollDiceResultFirstCountry!!.setImageResource(firstDice)
                                firstCountryObj.sum += firstCountryObj.currentDiceRoll
                                firstCountryResult.text = firstCountryObj.sum.toString()
                                singleRollDiceResultSecondCountry!!.setImageResource(secondDice)
                                secondCountryObj.sum += secondCountryObj.currentDiceRoll
                                secondCountryResult.text = secondCountryObj.sum.toString()
                                firstCountryObj.numberOfRoll--
                                remainingRoll!!.text = resources.getString(R.string.text_remaining_roll) + firstCountryObj.numberOfRoll.toString()
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
        Picasso.get().load(firstCountryObj.imageUrl).resize(400, 267)
                .into(firstCountryFlag!!, object : Callback {
                    override fun onSuccess() {
                        firstCountry.text = firstCountryObj.name
                    }

                    override fun onError(e: Exception?) {
                        firstCountry.text = firstCountryObj.name
                        firstCountryObj.flag = resources.getIdentifier("flag_" + firstCountryObj.alpha2Code, "drawable", packageName)
                        firstCountryFlag!!.setImageResource(firstCountryObj.flag!!)
                    }
                })

        Picasso.get().load(secondCountryObj.imageUrl).resize(400, 267)
                .into(secondCountryFlag!!, object : Callback {
                    override fun onSuccess() {
                        secondCountry.text = secondCountryObj.name
                    }

                    override fun onError(e: Exception?) {
                        secondCountry.text = secondCountryObj.name
                        secondCountryObj.flag = resources.getIdentifier("flag_" + secondCountryObj.alpha2Code, "drawable", packageName)
                        secondCountryFlag!!.setImageResource(secondCountryObj.flag!!)
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

            level < firstCountryObj.levelName.size -> {

                when (reselectType) {
                    1 -> {
                        Utils.reselect(firstCountryObj)
                        goBackPreviousActivity()
                    }
                    2 -> {
                        Utils.reselect(secondCountryObj)
                        TastyToast.makeText(applicationContext, "Your Next Opponent: ${secondCountryObj.name}", TastyToast.LENGTH_SHORT, TastyToast.DEFAULT).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                    }
                    3 -> {
                        Utils.reselect(firstCountryObj)
                        Utils.reselect(secondCountryObj)
                    }
                }
                setUI()
            }
        }
        resetValues()
        Utils.shakeDetector!!.start(Utils.sensorManager)
    }

    private fun assessResult() {
        Utils.shakeDetector!!.stop()
        when {
            firstCountryObj.sum > secondCountryObj.sum -> {
                ++firstCountryObj.level
                afterMatch(secondCountryFlag)
                winningCeremony(firstCountryObj.level)
            }
            secondCountryObj.sum > firstCountryObj.sum -> {
                firstCountryObj.level = 0
                TastyToast.makeText(applicationContext, "YOU LOSE!", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
                afterMatch(firstCountryFlag)
                Utils.setBW(firstCountryFlag!!, 0f)
                changeTrack(2)
                Handler().postDelayed({ endGame(firstCountryFlag) }, 4000)
            }
            else -> {
                ++firstCountryObj.numberOfRoll
                rollDiceButton.text = resources.getString(R.string.text_tie_break)
                remainingRoll!!.visibility = View.INVISIBLE
            }
        }

    }

    private fun assesEarlyWinning() {

        when {
            (firstCountryObj.numberOfRoll == 1 && (firstCountryObj.sum - secondCountryObj.sum >= 6 || secondCountryObj.sum - firstCountryObj.sum >= 6)) -> {
                assessResult()
            }
            (firstCountryObj.numberOfRoll == 2 && (firstCountryObj.sum - secondCountryObj.sum >= 11 || secondCountryObj.sum - firstCountryObj.sum >= 11)) -> {
                assessResult()
            }
            else -> {
                rollDiceButton!!.visibility = View.VISIBLE
            }
        }
    }

    private fun winningCeremony(level: Int) {
        when (level) {
            firstCountryObj.levelName.size -> {
                Utils.throwConfetti(konfettiView!!)
                changeTrack(1)
                TastyToast.makeText(applicationContext, "THE WORLD CHAMPIONS\n" + firstCountryObj.name!!.toUpperCase(Locale.getDefault()), TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
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
            firstCountryObj.level < firstCountryObj.levelName.size -> {
                firstCountryObj.reselectType = 2
                rematch(firstCountryObj.level, firstCountryObj.reselectType)
            }
            else -> {
                showAfterMatchDialog()
            }
        }

    }

    private fun resetValues() {
        firstCountryObj.numberOfRoll = CountryModel().numberOfRoll
        firstCountryObj.sum = 0
        secondCountryObj.sum = 0
        levelName.text = firstCountryObj.levelName[firstCountryObj.level]
        rollDiceButton!!.visibility = View.VISIBLE
        rollDiceButton!!.text = resources.getString(R.string.text_roll_dice)
        remainingRoll!!.visibility = View.VISIBLE
        remainingRoll!!.text = resources.getString(R.string.text_remaining_roll).plus(firstCountryObj.numberOfRoll.toString())
        singleRollDiceResultFirstCountry!!.visibility = View.VISIBLE
        singleRollDiceResultSecondCountry!!.visibility = View.VISIBLE
        firstCountryResult.text = firstCountryObj.sum.toString()
        secondCountryResult.text = secondCountryObj.sum.toString()
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
            firstCountryObj.reselectType = 3
            rematch(firstCountryObj.level, firstCountryObj.reselectType)
        }

        alertDialog.setNegativeButton(Html.fromHtml("<font color='#3342FF'>Change Opponent (Automatically)</font>")) { dialog, _ ->
            firstCountryObj.reselectType = 2
            rematch(firstCountryObj.level, firstCountryObj.reselectType)
            dialog.cancel()
        }

        alertDialog.setPositiveButton(Html.fromHtml("<font color='#3342FF'>Just Change My Team (Manually)</font>")) { dialog, _ ->
            firstCountryObj.reselectType = 1
            rematch(firstCountryObj.level, firstCountryObj.reselectType)
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









