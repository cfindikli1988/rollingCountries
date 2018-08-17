package com.cfindikli.apps.rollingCountries;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static com.cfindikli.apps.rollingCountries.R.id.imageView;
import static com.cfindikli.apps.rollingCountries.R.id.imageView5;
import static com.cfindikli.apps.rollingCountries.R.id.imageView6;


public class MainActivity extends AppCompatActivity {
    private static final Random RANDOM = new Random();
    final int[] song = {R.raw.dicerolleffect, R.raw.queenwearethechampions, R.raw.whawha};
    final private int numberOfRoll = 5;
    TextView firstCountryResult;
    TextView secondCountryResult;
    TextView firstCountry;
    TextView secondCountry;
    int shortCode1;
    int shortCode2;
    String firstCountryName;
    String secondCountryName;
    private int[] tieBreakRoll = new int[2];
    private boolean isMute = false;
    private int currentDiceRollFirstCountry, currentDiceRollSecondCountry, bonusPoints1, bonusPoints2, sum1, sum2, counter1, counter2 = 0;
    private int aggregateFirstCountry, aggregateSecondCountry = 0;
    private Button button;
    private TextView remainingRoll, aggregate;
    private KonfettiView konfettiView1;
    private ImageView singleRollDiceResultFirstCountry, singleRollDiceResultSecondCountry, firstCountryFlag, secondCountryFlag;
    private MediaPlayer mp;
    private Uri uri1, uri2;
    private String prefix = "flag_";
    private String FirstCountryShortCode;
    private String SecondCountryShortCode;
    private String imageUrl1;
    private String imageUrl2;

    private static int randomDiceValue() {
        return RANDOM.nextInt(6) + 1;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_activty);
        firstCountry =
                findViewById(R.id.textView10);
        secondCountry = findViewById(R.id.textView11);
        firstCountryResult = findViewById(R.id.textView2);
        secondCountryResult = findViewById(R.id.textView4);
        remainingRoll = findViewById(R.id.textView3);
        aggregate = findViewById(R.id.textView5);
        aggregate.setVisibility(View.INVISIBLE);
        remainingRoll.setText(getResources().getString(R.string.text_remaining_roll) + numberOfRoll);
        singleRollDiceResultFirstCountry = findViewById(R.id.imageView1);
        singleRollDiceResultSecondCountry = findViewById(R.id.imageView2);
        singleRollDiceResultFirstCountry.setVisibility(View.INVISIBLE);
        singleRollDiceResultSecondCountry.setVisibility(View.INVISIBLE);
        firstCountryFlag = findViewById(imageView);
        secondCountryFlag = findViewById(imageView5);
        final ImageView volumeIcon = findViewById(imageView6);
        konfettiView1 = findViewById(R.id.konfettiView);

        mp = MediaPlayer.create(this, R.raw.dicerolleffect);
        button = findViewById(R.id.rollDices);

        Intent extras = getIntent();
        imageUrl1 = extras.getStringExtra("uri1");
        firstCountryName = extras.getStringExtra("firstCountryName");
        FirstCountryShortCode = extras.getStringExtra("shortCode1");
        uri1 = Uri.parse(imageUrl1);

        imageUrl2 = extras.getStringExtra("uri2");
        secondCountryName = extras.getStringExtra("secondCountryName");
        SecondCountryShortCode = extras.getStringExtra("shortCode2");
        uri2 = Uri.parse(imageUrl2);


        shortCode1 = getResources().getIdentifier("flag_" + FirstCountryShortCode, "drawable", getPackageName());
        shortCode2 = getResources().getIdentifier("flag_" + SecondCountryShortCode, "drawable", getPackageName());

        setUI();


        volumeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;

                if (isMute) {
                    volumeIcon.setImageResource(R.drawable.mute);
                    mp.setVolume(0, 0);
                } else {
                    isMute = false;
                    volumeIcon.setImageResource(R.drawable.volume);
                    mp.setVolume(1, 1);
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
                                                  singleRollDiceResultFirstCountry.setVisibility(View.VISIBLE);
                                                  singleRollDiceResultSecondCountry.setVisibility(View.VISIBLE);
                                              }

                                              @Override
                                              public void onAnimationEnd(Animation animation) {
                                                  currentDiceRollFirstCountry = randomDiceValue();
                                                  currentDiceRollSecondCountry = randomDiceValue();

                                                  int firstDice = getResources().getIdentifier("dice_" + currentDiceRollFirstCountry, "drawable", getPackageName());
                                                  int test = getResources().getIdentifier("dice_6", "drawable", getPackageName());
                                                  System.out.println(test);
                                                  int secondDice = getResources().getIdentifier("dice_" + currentDiceRollSecondCountry, "drawable", getPackageName());

                                                  if (animation == anim1) {
                                                      if (counter1 < numberOfRoll) {
                                                          singleRollDiceResultFirstCountry.setImageResource(firstDice);

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
                                                          singleRollDiceResultSecondCountry.setImageResource(secondDice);
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
                                                          assessResult();
                                                          button.setVisibility(View.INVISIBLE);
                                                      } else if (numberOfRoll - counter1 == 1 && (sum1 - sum2 >= 6 || sum2 - sum1 >= 6)) {
                                                          assessResult();
                                                      } else if (numberOfRoll - counter1 == 2 && (sum1 - sum2 >= 11 || sum2 - sum1 >= 11)) {
                                                          assessResult();
                                                      } else button.setVisibility(View.VISIBLE);
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


    private void setUI() {


        if (checkURL(imageUrl1) == true && checkURL(imageUrl2) == true) {

            firstCountry.setText(firstCountryName);


            Picasso.with(getApplicationContext()).load(uri1).networkPolicy(NetworkPolicy.OFFLINE).resize(400, 267).error(R.drawable.rollingdices)
                    .into(firstCountryFlag);


            secondCountry.setText(secondCountryName);


            Picasso.with(getApplicationContext()).load(uri2).networkPolicy(NetworkPolicy.OFFLINE).resize(400, 267).error(R.drawable.rollingdices)
                    .into(secondCountryFlag);


        } else if (checkURL(imageUrl1) == false && checkURL(imageUrl2) == true) {


            firstCountry.setText(firstCountryName);

            Integer firstFlag = getResources().getIdentifier("flag_" + FirstCountryShortCode, "drawable", getPackageName());

            firstCountryFlag.setImageResource(firstFlag);

            secondCountry.setText(secondCountryName);


            Picasso.with(getApplicationContext()).load(uri2).networkPolicy(NetworkPolicy.OFFLINE).resize(400, 267).error(R.drawable.rollingdices)
                    .into(secondCountryFlag);


        } else if (checkURL(imageUrl1) == true && checkURL(imageUrl2) == false) {

            firstCountry.setText(firstCountryName);


            Picasso.with(getApplicationContext()).load(uri1).networkPolicy(NetworkPolicy.OFFLINE).resize(400, 267).error(R.drawable.rollingdices)
                    .into(firstCountryFlag);

            secondCountry.setText(secondCountryName);


            Integer secondFlag = getResources().getIdentifier("flag_" + SecondCountryShortCode, "drawable", getPackageName());

            secondCountryFlag.setImageResource(secondFlag);

        } else {

            firstCountry.setText(firstCountryName);

            Integer firstFlag = getResources().getIdentifier("flag_" + FirstCountryShortCode, "drawable", getPackageName());

            firstCountryFlag.setImageResource(firstFlag);

            secondCountry.setText(secondCountryName);

            Integer secondFlag = getResources().getIdentifier("flag_" + SecondCountryShortCode, "drawable", getPackageName());

            secondCountryFlag.setImageResource(secondFlag);

        }


    }

    private boolean checkURL(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            int responseCode = urlConnection.getResponseCode();
            urlConnection.disconnect();
            boolean isValid = (responseCode == 200) ? true : false;
            return isValid;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException | NetworkOnMainThreadException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void setBW(ImageView iv, Float isUnfiltered) {

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(isUnfiltered);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        iv.setColorFilter(filter);
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

    private void changeTrack(int position) {
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

    private void endGame(ImageView imageview) {
        mp.stop();
        setBW(imageview, 1F);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("What would you like to do next?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Change Both Teams",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Keep Both Teams",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rematch();
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void afterMatch(ImageView imageView) {
        remainingRoll.setVisibility(View.INVISIBLE);

        singleRollDiceResultFirstCountry.setVisibility(View.INVISIBLE);
        singleRollDiceResultSecondCountry.setVisibility(View.INVISIBLE);

        setBW(imageView, 0F);

    }



    private void rematch() {


        aggregate.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        remainingRoll.setVisibility(View.VISIBLE);
        remainingRoll.setText(getResources().getString(R.string.text_remaining_roll) + numberOfRoll);
        singleRollDiceResultFirstCountry.setVisibility(View.VISIBLE);
        singleRollDiceResultSecondCountry.setVisibility(View.VISIBLE);
        sum1 = 0;
        sum2 = 0;
        counter1 = 0;
        counter2 = 0;
        firstCountryResult.setText(String.valueOf(sum1));
        secondCountryResult.setText(String.valueOf(sum2));
        aggregate.setText(getResources().getString(R.string.text_aggregate) + aggregateFirstCountry + "-" + aggregateSecondCountry);
        singleRollDiceResultFirstCountry.setImageResource(2131165275);
        singleRollDiceResultSecondCountry.setImageResource(2131165275);


    }

    private void assessResult() {
        if (sum1 > sum2) {
            assessHeavyDefeat();

            TastyToast.makeText(getApplicationContext(), "YOU WIN!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            afterMatch(secondCountryFlag);
            throwKonfetti(konfettiView1);
            changeTrack(1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    endGame(secondCountryFlag);

                }
            }, 8000);

        } else if (sum2 > sum1) {

            assessHeavyDefeat();
            TastyToast.makeText(getApplicationContext(), "YOU LOSE!", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            afterMatch(firstCountryFlag);
            setBW(firstCountryFlag, 0F);
            changeTrack(2);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    endGame(firstCountryFlag);
                }
            }, 4000);

        } else {
            if (bonusPoints1 > bonusPoints2) {
                aggregateFirstCountry++;
                TastyToast.makeText(getApplicationContext(), "YOU WIN!\n" + "Bonus Points: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                afterMatch(secondCountryFlag);
                throwKonfetti(konfettiView1);
                changeTrack(1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endGame(secondCountryFlag);

                    }
                }, 8000);
            } else if (bonusPoints2 > bonusPoints1) {
                aggregateSecondCountry++;
                TastyToast.makeText(MainActivity.this, "YOU LOSE!\n" + "Bonus Points: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")", TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                afterMatch(firstCountryFlag);
                changeTrack(2);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endGame(firstCountryFlag);
                    }
                }, 4000);
            } else {
                tieBreakRoll[0] = randomDiceValue();
                tieBreakRoll[1] = randomDiceValue();
                if (tieBreakRoll[0] == tieBreakRoll[1]) {
                    aggregateFirstCountry++;
                    TastyToast.makeText(MainActivity.this, "YOU WIN!\n" + "Bonus Points: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")" + " TieBreak Roll: " + tieBreakRoll[0] + "-" + tieBreakRoll[1], TastyToast.LENGTH_LONG, TastyToast.SUCCESS).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                    afterMatch(secondCountryFlag);
                    throwKonfetti(konfettiView1);
                    changeTrack(1);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            endGame(secondCountryFlag);
                        }
                    }, 8000);
                } else {
                    aggregateSecondCountry++;
                    TastyToast.makeText(MainActivity.this, "YOU LOSE!\n" + "Bonus Points: " + "(" + bonusPoints1 + ")" + "-" + "(" + bonusPoints2 + ")" + " TieBreak Roll: " + tieBreakRoll[0] + "-" + tieBreakRoll[1], TastyToast.LENGTH_LONG, TastyToast.ERROR).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                    afterMatch(firstCountryFlag);
                    changeTrack(2);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            endGame(firstCountryFlag);
                        }
                    }, 4000);


                }

            }

        }
    }

    private void assessHeavyDefeat()
    {
        if(sum1-sum2>=11 ){
            aggregateFirstCountry+=2;

        }
        else if(sum2-sum1 >= 11){

            aggregateSecondCountry+=2;

        }
        else if(sum1>sum2)
        {
            aggregateFirstCountry++;
        }
        else{
            aggregateSecondCountry++;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}

