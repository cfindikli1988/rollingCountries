package com.example.caglarfindikli.springforandroidexample;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static com.example.caglarfindikli.springforandroidexample.R.id.imageView;
import static com.example.caglarfindikli.springforandroidexample.R.id.imageView5;


public class MainActivity extends AppCompatActivity {
    public static final Random RANDOM = new Random();
    int sum1 = 0;
    int sum2 = 0;
    int counter1 = 0;
    int counter2 = 0;
    int numberOfRoll = 1;
    int bonusPoints1 = 0;
    int bonusPoints2 = 0;
    int currentDiceRollFirstCountry = 0;
    int currentDiceRollSecondCountry = 0;


    public static int randomDiceValue() {
        return RANDOM.nextInt(6) + 1;
    }

    public static int randomCountry() {
        return RANDOM.nextInt(250) + 1;
    }

    public static String getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o.toString().toLowerCase();
            }
        }
        return null;
    }

    public static android.net.Uri getFlag(String shortCode) {

        final String uri = "http://flagpedia.net/data/flags/normal/" + shortCode + ".png";

        return Uri.parse(uri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_activty);
        final TextView firstCountry =
                (TextView) findViewById(R.id.textView10);
        final TextView secondCountry = (TextView) findViewById(R.id.textView11);

        final TextView firstCountryResult = (TextView) findViewById(R.id.textView2);
        final TextView secondCountryResult = (TextView) findViewById(R.id.textView4);
        final TextView remainingRoll = (TextView) findViewById(R.id.textView3);
        remainingRoll.setText("Kalan Atış: " + numberOfRoll);
        final ImageView singleRollDiceResultFirstCountry = (ImageView) findViewById(R.id.imageView1);
        final ImageView singleRollDiceResultSecondCountry = (ImageView) findViewById(R.id.imageView2);
        final ImageView firstCountryFlag = (ImageView) findViewById(imageView);
        final ImageView secondCountryFlag = (ImageView) findViewById(imageView5);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.queenwearethechampions);
        final MediaPlayer mp1 = MediaPlayer.create(this, R.raw.dicerolleffect);
        final MediaPlayer mp2 = MediaPlayer.create(this, R.raw.whawha);
        final KonfettiView konfettiView1 = (KonfettiView) findViewById(R.id.konfettiView);

        final Button button = (Button) findViewById(R.id.rollDices);
        button.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {

                                          final Animation anim1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
                                          final Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
                                          final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                                              @Override
                                              public void onAnimationStart(Animation animation) {
                                                  mp1.start();

                                              }

                                              @Override
                                              public void onAnimationEnd(Animation animation) {
                                                  currentDiceRollFirstCountry = randomDiceValue();
                                                  currentDiceRollSecondCountry = randomDiceValue();

                                                  int res = getResources().getIdentifier("dice_" + currentDiceRollFirstCountry, "drawable", "com.example.caglarfindikli.springforandroidexample");
                                                  int res1 = getResources().getIdentifier("dice_" + currentDiceRollSecondCountry, "drawable", "com.example.caglarfindikli.springforandroidexample");

                                                  if (animation == anim1) {
                                                      if (counter1 < numberOfRoll) {
                                                          singleRollDiceResultFirstCountry.setImageResource(res);

                                                          sum1 += currentDiceRollFirstCountry;
                                                          firstCountryResult.setText(String.valueOf(sum1));
                                                          counter1++;
                                                          if (currentDiceRollFirstCountry == 6) {
                                                              bonusPoints1++;
                                                          }
                                                          if (currentDiceRollFirstCountry == 1) {
                                                              bonusPoints1--;
                                                          }
                                                      }


                                                  } else if (animation == anim2) {
                                                      if (counter2 < numberOfRoll) {
                                                          singleRollDiceResultSecondCountry.setImageResource(res1);
                                                          sum2 += currentDiceRollSecondCountry;
                                                          secondCountryResult.setText(String.valueOf(sum2));
                                                          counter2++;
                                                          if (currentDiceRollSecondCountry == 6) {
                                                              bonusPoints2++;
                                                          }
                                                          if (currentDiceRollSecondCountry == 1) {
                                                              bonusPoints2--;
                                                          }
                                                      }
                                                      if (numberOfRoll - counter1 == 0) {

                                                          if (sum1 > sum2) {
                                                              button.setVisibility(View.INVISIBLE);
                                                              Toast.makeText(MainActivity.this, "KAZANDIN", Toast.LENGTH_LONG).show();


                                                              konfettiView1.build()
                                                                      .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                                                                      .setDirection(0.0, 359.0)
                                                                      .setSpeed(1f, 5f)
                                                                      .setFadeOutEnabled(true)
                                                                      .setTimeToLive(2000L)
                                                                      .addShapes(Shape.RECT, Shape.CIRCLE)
                                                                      .addSizes(new Size(12, 5f))
                                                                      .setPosition(-50f, konfettiView1.getWidth() + 50f, -50f, -50f)
                                                                      .streamFor(400, 5000L);

                                                              setBW(secondCountryFlag);
                                                              mp.start();
                                                              new Handler().postDelayed(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      MainActivity.this.finish();

                                                                      mp.stop();
                                                                  }
                                                              }, 8000);
                                                          } else if (sum2 > sum1) {
                                                              button.setVisibility(View.INVISIBLE);
                                                              setBW(firstCountryFlag);

                                                              mp2.start();
                                                              Toast.makeText(MainActivity.this, "İKİ ZARA 80LİK OLDUN", Toast.LENGTH_LONG).show();
                                                              new Handler().postDelayed(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      MainActivity.this.finish();
                                                                  }
                                                              }, 4000);
                                                          } else {
                                                              if (bonusPoints1 > bonusPoints2) {
                                                                  button.setVisibility(View.INVISIBLE);
                                                                  Toast.makeText(MainActivity.this, "KAZANDIN" + "Avg: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")", Toast.LENGTH_LONG).show();

                                                                  konfettiView1.build()
                                                                          .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                                                                          .setDirection(0.0, 359.0)
                                                                          .setSpeed(1f, 5f)
                                                                          .setFadeOutEnabled(true)
                                                                          .setTimeToLive(2000L)
                                                                          .addShapes(Shape.RECT, Shape.CIRCLE)
                                                                          .addSizes(new Size(12, 5f))
                                                                          .setPosition(-50f, konfettiView1.getWidth() + 50f, -50f, -50f)
                                                                          .streamFor(400, 5000L);


                                                                  setBW(secondCountryFlag);
                                                                  mp.start();
                                                                  new Handler().postDelayed(new Runnable() {
                                                                      @Override
                                                                      public void run() {
                                                                          MainActivity.this.finish();
                                                                          mp.stop();
                                                                      }
                                                                  }, 8000);
                                                              } else if (bonusPoints2 > bonusPoints1) {
                                                                  button.setVisibility(View.INVISIBLE);
                                                                  setBW(firstCountryFlag);
                                                                  mp2.start();
                                                                  Toast.makeText(MainActivity.this, "İKİ ZARA 80LİK OLDUN" + "Avg: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")", Toast.LENGTH_LONG).show();
                                                                  new Handler().postDelayed(new Runnable() {
                                                                      @Override
                                                                      public void run() {
                                                                          MainActivity.this.finish();
                                                                      }
                                                                  }, 4000);
                                                              } else {
                                                                  int tieBreakRoll = randomDiceValue();

                                                                  if (tieBreakRoll % 2 == 0) {
                                                                      button.setVisibility(View.INVISIBLE);
                                                                      Toast.makeText(MainActivity.this, "KAZANDIN" + "Avg: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")" + "Rastgele Atış" + tieBreakRoll, Toast.LENGTH_LONG).show();
                                                                      setBW(secondCountryFlag);
                                                                      mp.start();
                                                                      new Handler().postDelayed(new Runnable() {
                                                                          @Override
                                                                          public void run() {
                                                                              MainActivity.this.finish();
                                                                              mp.stop();
                                                                          }
                                                                      }, 8000);
                                                                  } else {
                                                                      button.setVisibility(View.INVISIBLE);
                                                                      setBW(firstCountryFlag);
                                                                      mp2.start();
                                                                      Toast.makeText(MainActivity.this, "İKİ ZARA 80LİK OLDUN" + "Avg: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")" + "Rastgele Atış" + tieBreakRoll, Toast.LENGTH_LONG).show();
                                                                      new Handler().postDelayed(new Runnable() {
                                                                          @Override
                                                                          public void run() {
                                                                              MainActivity.this.finish();
                                                                          }
                                                                      }, 4000);


                                                                  }

                                                              }

                                                          }
                                                      }


                                                  }
                                                  remainingRoll.setText("Kalan Atış: " + String.valueOf(numberOfRoll - counter1));
                                              }

                                              @Override
                                              public void onAnimationRepeat(Animation animation) {

                                              }
                                          };


                                          anim1.setAnimationListener(animationListener);
                                          anim2.setAnimationListener(animationListener);

                                          singleRollDiceResultFirstCountry.startAnimation(anim1);
                                          singleRollDiceResultSecondCountry.startAnimation(anim2);


                                      }
                                  }
        );


        final AsyncTask<String, Void, String> simpleGetTask =
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        RestTemplate restTemplate = new RestTemplate();
                        //add the String message converter, since the result of
                        // the call will be a String
                        restTemplate.getMessageConverters().add(
                                new StringHttpMessageConverter());
                        // Make the HTTP GET request on the url (params[0]),
                        // marshaling the response to a String
                        return
                                restTemplate.getForObject(params[0], String.class);
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        // executed by the UI thread once the background
                        // thread is done getting the result
                        ObjectMapper mapper = new ObjectMapper();

                        Map<String, Object> map = new HashMap<String, Object>();

                        // convert JSON string to Map
                        try {
                            map = mapper.readValue(result, new TypeReference<Map<String, String>>() {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ArrayList<Object> countries = new ArrayList<>(map.values());
                        int randomSelectionFirstCountry = randomCountry();
                        int randomSelectionSecondCountry = randomCountry();
                        try {
                            Thread.currentThread().sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        firstCountry.setText(countries.get(randomSelectionFirstCountry - 1).toString());
                        Picasso.with(MainActivity.this).load(getFlag(getKeyFromValue(map, countries.get(randomSelectionFirstCountry - 1)))).resize(400, 267).error(R.drawable.rollingdices).into(firstCountryFlag);
                        firstCountryFlag.setImageURI(getFlag(getKeyFromValue(map, countries.get(randomSelectionFirstCountry - 1))));
                        secondCountry.setText(countries.get(randomSelectionSecondCountry - 1).toString());
                        Picasso.with(MainActivity.this).load(getFlag(getKeyFromValue(map, countries.get(randomSelectionSecondCountry - 1)))).resize(400, 267).error(R.drawable.rollingdices).into(secondCountryFlag);
                        secondCountryFlag.setImageURI(getFlag(getKeyFromValue(map, countries.get(randomSelectionSecondCountry - 1))));


                    }
                };

        String url = "http://country.io/names.json";
        // triggers the task; it will update the resultTextView once
        // it is done
        simpleGetTask.execute(url);


    }

    private void setBW(ImageView iv) {

        float brightness = 10; // change values to suite your need

        float[] colorMatrix = {
                0.33f, 0.33f, 0.33f, 0, brightness,
                0.33f, 0.33f, 0.33f, 0, brightness,
                0.33f, 0.33f, 0.33f, 0, brightness,
                0, 0, 0, 1, 0
        };

        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        iv.setColorFilter(colorFilter);
    }

}

