package com.example.caglarfindikli.springforandroidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by caglarfindikli on 4.10.2017.
 */

public class TestActivity extends AppCompatActivity {
    Button btn;
    private ProgressBar progressBar;
    TextView txt;
    Intent i;

    Integer count =1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setMax(100);
        btn = (Button) findViewById(R.id.button2);
        btn.setText("Start");
        txt = (TextView) findViewById(R.id.textView);
        progressBar.setVisibility(View.INVISIBLE);
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View view) {
                count =1;
                progressBar.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
                progressBar.setProgress(0);
                switch (view.getId()) {
                    case R.id.button2:
                        new MyTask().execute(100);
                        break;
                }
            }
        };
        btn.setOnClickListener(listener);


    }
    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            for (; count <= params[0]; count+=4) {
                try {
                    Thread.sleep(100);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Intent i = new Intent(TestActivity.this,MainActivity.class);
            startActivity(i);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            txt.setText(" ");
            btn.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            txt.setText("Game Starting...");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            txt.setText("Yükleniyor..."+ "%" +values[0]);
            progressBar.setProgress(values[0]);

        }

    }
}
