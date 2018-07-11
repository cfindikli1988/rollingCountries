package com.cfindikli.apps.rollingCountries

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import com.cfindikli.apps.rollingCountries.R

import ru.katso.livebutton.LiveButton


@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {

    private var pStatus = 0
    private val handler = Handler()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val res = resources
        val drawable = res.getDrawable(R.drawable.circular)
        val mProgress = findViewById<ProgressBar>(R.id.google_progress)


        mProgress.visibility = View.INVISIBLE

        val start_Button = findViewById<LiveButton>(R.id.button)
        mProgress.progress = 0   // Main Progress
        mProgress.secondaryProgress = 100 // Secondary Progress
        mProgress.max = 100 // Maximum Progress
        mProgress.progressDrawable = drawable


        start_Button.setOnClickListener { _ ->
            start_Button.visibility = View.INVISIBLE
            mProgress.visibility = View.VISIBLE
            Thread(Runnable {

                while (pStatus < 100) {

                    pStatus += 1

                    handler.post {
                        mProgress.progress = pStatus
                        //tv.text = pStatus.toString() + "%"
                    }
                    try {

                        Thread.sleep(50) //thread will take approx 3 seconds to finish
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }

                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }).start()

        }


    }
}
