package com.example.caglarfindikli.springforandroidexample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView


@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {

    private var pStatus = 0
    private val handler = Handler()
    internal lateinit var tv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val res = resources
        val drawable = res.getDrawable(R.drawable.circular)
        val mProgress = findViewById(R.id.circularProgressbar) as ProgressBar

        mProgress.visibility = View.INVISIBLE

        val start_Button = findViewById(R.id.button) as Button
        mProgress.progress = 0   // Main Progress
        mProgress.secondaryProgress = 100 // Secondary Progress
        mProgress.max = 100 // Maximum Progress
        mProgress.progressDrawable = drawable



        tv = findViewById(R.id.tv) as TextView

        tv.visibility = View.INVISIBLE



        start_Button.setOnClickListener { _ ->
            start_Button.visibility = View.INVISIBLE
            mProgress.visibility = View.VISIBLE
            tv.visibility = View.VISIBLE
            Thread(Runnable {

                while (pStatus < 100) {

                    pStatus += 1

                    handler.post {
                        // TODO Auto-generated method stub
                        mProgress.progress = pStatus
                        tv.text = pStatus.toString() + "%"
                    }
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
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
