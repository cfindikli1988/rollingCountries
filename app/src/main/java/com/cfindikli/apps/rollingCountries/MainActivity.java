package com.cfindikli.apps.rollingCountries;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static com.cfindikli.apps.rollingCountries.R.id.imageView;
import static com.cfindikli.apps.rollingCountries.R.id.imageView5;
import static com.cfindikli.apps.rollingCountries.R.id.imageView6;


public class MainActivity extends AppCompatActivity {
    private static final Random RANDOM = new Random();
    private final int numberOfRoll = 5;
    private int sum1 = 0;
    private int sum2 = 0;
    private int[] tieBreakRoll = new int[2];
    private int counter1 = 0;
    private int counter2 = 0;
    private boolean isMute = false;
    private int bonusPoints1 = 0;
    private int bonusPoints2 = 0;
    private int currentDiceRollFirstCountry = 0;
    private int currentDiceRollSecondCountry = 0;
    private Button button;
    private TextView remainingRoll;
    private KonfettiView konfettiView1;
    private ImageView singleRollDiceResultFirstCountry;
    private ImageView singleRollDiceResultSecondCountry;
    private MediaPlayer mp;
    final int[] song = {R.raw.dicerolleffect,R.raw.queenwearethechampions,R.raw.whawha};

    private static int randomDiceValue() {
        return RANDOM.nextInt(6) + 1;
    }



    private static android.net.Uri getFlag(String shortCode) {

        final String uri = "http://flagpedia.net/data/flags/normal/" + shortCode + ".png";

        return Uri.parse(uri);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_activty);
        final TextView firstCountry =
                findViewById(R.id.textView10);
        final TextView secondCountry = findViewById(R.id.textView11);
        final TextView firstCountryResult = findViewById(R.id.textView2);
        final TextView secondCountryResult = findViewById(R.id.textView4);
        remainingRoll = findViewById(R.id.textView3);
        remainingRoll.setText(getResources().getString(R.string.text_remaining_roll) + numberOfRoll);
        singleRollDiceResultFirstCountry = findViewById(R.id.imageView1);
        singleRollDiceResultSecondCountry = findViewById(R.id.imageView2);
        final ImageView firstCountryFlag = findViewById(imageView);
        final ImageView secondCountryFlag = findViewById(imageView5);
        final ImageView volumeIcon = findViewById(imageView6);
        konfettiView1 = findViewById(R.id.konfettiView);



        mp = MediaPlayer.create(this, R.raw.dicerolleffect);

        button = findViewById(R.id.rollDices);

        Intent extras = getIntent();
        String imageUrl1 = extras.getStringExtra("uri1");
        String firstCountryName = extras.getStringExtra("firstCountryName");
        Uri uri1 = Uri.parse(imageUrl1);

        String imageUrl2 = extras.getStringExtra("uri2");
        String secondCountryName = extras.getStringExtra("secondCountryName");
        Uri uri2 = Uri.parse(imageUrl2);

        firstCountry.setText(firstCountryName);


        Picasso.with(getApplicationContext()).load(uri1).networkPolicy(NetworkPolicy.OFFLINE).resize(400, 267)
                .error(R.drawable.rollingdices)
                .into(firstCountryFlag);


        secondCountry.setText(secondCountryName);

        Picasso.with(getApplicationContext()).load(uri2).networkPolicy(NetworkPolicy.OFFLINE)
                .resize(400, 267)
                .error(R.drawable.rollingdices).into(secondCountryFlag);

        volumeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;

                if(isMute) {

                    volumeIcon.setImageResource(R.drawable.mute);
                    mp.setVolume(0,0);

                }
                else{
                    isMute=false;
                    volumeIcon.setImageResource(R.drawable.volume);
                    mp.setVolume(1,1);


                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {

                                          final Animation anim1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
                                          final Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
                                          final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                                              @Override
                                              public void onAnimationStart(Animation animation) {
                                                  mp.start();
                                                  button.setVisibility(View.INVISIBLE);

                                              }

                                              @Override
                                              public void onAnimationEnd(Animation animation) {
                                                  currentDiceRollFirstCountry = randomDiceValue();
                                                  currentDiceRollSecondCountry = randomDiceValue();

                                                  int res = getResources().getIdentifier("dice_" + currentDiceRollFirstCountry, "drawable", getPackageName());
                                                  int res1 = getResources().getIdentifier("dice_" + currentDiceRollSecondCountry, "drawable", getPackageName());

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

                                                              TastyToast.makeText(getApplicationContext(), "KAZANDIN!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                              afterMatch(secondCountryFlag);
                                                              throwKonfetti(konfettiView1);

                                                              changeTrack(1);

                                                              new Handler().postDelayed(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      endGame();



                                                                  }
                                                              }, 8000);
                                                          } else if (sum2 > sum1) {

                                                              TastyToast.makeText(getApplicationContext(), "İKİ ZARA 80LİK OLDUN!", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                              afterMatch(firstCountryFlag);
                                                              setBW(firstCountryFlag);

                                                                  changeTrack(2);


                                                              new Handler().postDelayed(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      endGame();

                                                                  }
                                                              }, 4000);
                                                          } else {
                                                              if (bonusPoints1 > bonusPoints2) {

                                                                  TastyToast.makeText(getApplicationContext(), "KAZANDIN\n" + "Avg: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                  afterMatch(secondCountryFlag);
                                                                  throwKonfetti(konfettiView1);

                                                                      changeTrack(1);

                                                                  new Handler().postDelayed(new Runnable() {
                                                                      @Override
                                                                      public void run() {
                                                                          endGame();

                                                                      }
                                                                  }, 8000);
                                                              } else if (bonusPoints2 > bonusPoints1) {


                                                                  TastyToast.makeText(MainActivity.this, "İKİ ZARA 80LİK OLDUN\n" + "Avg: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                  afterMatch(firstCountryFlag);
                                                                  changeTrack(2);

                                                                  new Handler().postDelayed(new Runnable() {
                                                                      @Override
                                                                      public void run() {
                                                                         endGame();
                                                                      }
                                                                  }, 4000);
                                                              } else {
                                                                  tieBreakRoll[0] = randomDiceValue();
                                                                  tieBreakRoll[1] = randomDiceValue();
                                                                  if (tieBreakRoll[0] == tieBreakRoll[1]) {

                                                                      TastyToast.makeText(MainActivity.this, "KAZANDIN\n" + "Avg: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")" + " TieBreak Atış: " + tieBreakRoll[0] + "-" + tieBreakRoll[1], TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                      afterMatch(secondCountryFlag);
                                                                      throwKonfetti(konfettiView1);

                                                                          changeTrack(1);

                                                                      new Handler().postDelayed(new Runnable() {
                                                                          @Override
                                                                          public void run() {
                                                                              endGame();

                                                                          }
                                                                      }, 8000);
                                                                  } else {

                                                                      TastyToast.makeText(MainActivity.this, "İKİ ZARA 80LİK OLDUN\n" + "Avg: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")" + " TieBreak Atış: " + tieBreakRoll[0] + "-" + tieBreakRoll[1], TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                                      afterMatch(firstCountryFlag);
                                                                      changeTrack(2);

                                                                      new Handler().postDelayed(new Runnable() {
                                                                          @Override
                                                                          public void run() {
                                                                              endGame();
                                                                          }
                                                                      }, 4000);


                                                                  }

                                                              }

                                                          }
                                                          button.setVisibility(View.INVISIBLE);
                                                      } else {
                                                          button.setVisibility(View.VISIBLE);
                                                      }

                                                  }

                                                  remainingRoll.setText(getResources().getString(R.string.text_remaining_roll) + String.valueOf(numberOfRoll - counter1));


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

    private void throwKonfetti(KonfettiView konfettiView) {
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLUE)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(600, 5000L);
    }

    private void changeTrack(int position)  {
        mp.stop();
        mp.reset();
        try {
            mp.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + song[position]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
        mp.start();


    }

    private void endGame() {
        mp.stop();
        recreate();
    }

    private void afterMatch(ImageView imageView) {


        remainingRoll.setVisibility(View.INVISIBLE);
        singleRollDiceResultFirstCountry.setVisibility(View.INVISIBLE);
        singleRollDiceResultSecondCountry.setVisibility(View.INVISIBLE);
        setBW(imageView);


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        super.onBackPressed();  // optional depending on your needs
    }


}

