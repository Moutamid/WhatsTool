package com.moutamid.whatstool;

import static com.facebook.imageutils.HeifExifUtil.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class TextToEmojiActivity extends AppCompatActivity {

    Button clearTxtBtn;
    Button convertButton;
    EditText convertedText;
    Button copyBtn;
    EditText emojeeText;
    EditText inputText;
    Button shareButton;
    ImageView backBtn;

    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_emoji);

        this.inputText = findViewById(R.id.inputText);
        this.emojeeText = findViewById(R.id.emojeeTxt);
        this.convertedText = findViewById(R.id.convertedEmojeeTxt);
        this.convertButton = findViewById(R.id.convertEmojeeBtn);
        this.copyBtn = findViewById(R.id.copyTxtBtn);
        this.shareButton = findViewById(R.id.shareTxtBtn);
        this.clearTxtBtn = findViewById(R.id.clearTxtBtn);
        this.convertButton.setOnClickListener(new btnConvertListner());
        this.clearTxtBtn.setOnClickListener(new btnClearTextListner());
        this.convertedText.setOnClickListener(new btnConvertedTextListner());
        this.copyBtn.setOnClickListener(new btnCopyButtonListner());
        this.shareButton.setOnClickListener(new btnShareListner());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);

        adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        loadIntersAd();
        
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    //button convert click event listener
    private class btnConvertListner implements View.OnClickListener {
        public void onClick(View view) {
            if (TextToEmojiActivity.this.inputText.getText().toString().isEmpty()) {
                Toast.makeText(TextToEmojiActivity.this.getApplicationContext(), "Enter text", Toast.LENGTH_SHORT).show();
                return;
            }
            char[] charArray = TextToEmojiActivity.this.inputText.getText().toString().toCharArray();
            TextToEmojiActivity.this.convertedText.setText(".\n");
            for (char character : charArray) {
                byte[] localObject3;
                InputStream localObject2;
                if (character == '?') {
                    try {
                        InputStream localObject1 = TextToEmojiActivity.this.getBaseContext().getAssets().open("ques.txt");
                        localObject3 = new byte[localObject1.available()];
                        localObject1.read(localObject3);
                        localObject1.close();
                        TextToEmojiActivity.this.convertedText.append(new String(localObject3).replaceAll("[*]", TextToEmojiActivity.this.emojeeText.getText().toString()) + "\n\n");
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                } else if (character == ((char) (character & 95)) || Character.isDigit(character)) {
                    try {
                        localObject2 = TextToEmojiActivity.this.getBaseContext().getAssets().open(character + ".txt");
                        localObject3 = new byte[localObject2.available()];
                        localObject2.read(localObject3);
                        localObject2.close();
                        TextToEmojiActivity.this.convertedText.append(new String(localObject3).replaceAll("[*]", TextToEmojiActivity.this.emojeeText.getText().toString()) + "\n\n");
                    } catch (IOException ioe2) {
                        ioe2.printStackTrace();
                    }
                } else {
                    try {
                        localObject2 = TextToEmojiActivity.this.getBaseContext().getAssets().open("low" + character + ".txt");
                        localObject3 = new byte[localObject2.available()];
                        localObject2.read(localObject3);
                        localObject2.close();
                        TextToEmojiActivity.this.convertedText.append(new String(localObject3).replaceAll("[*]", TextToEmojiActivity.this.emojeeText.getText().toString()) + "\n\n");
                    } catch (IOException ioe22) {
                        ioe22.printStackTrace();
                    }
                }
            }
        }
    }

    //Button - clear Text click listener method
    private class btnClearTextListner implements View.OnClickListener {
        public void onClick(View view) {
            TextToEmojiActivity.this.convertedText.setText("");
        }
    }

    //Button  - Convert Text click listener method
    private class btnConvertedTextListner implements View.OnClickListener {
        @SuppressLint({"WrongConstant"})
        public void onClick(View view) {
            if (!TextToEmojiActivity.this.convertedText.getText().toString().isEmpty()) {
                ((ClipboardManager) TextToEmojiActivity.this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(TextToEmojiActivity.this.inputText.getText().toString(), TextToEmojiActivity.this.convertedText.getText().toString()));
                Toast.makeText(TextToEmojiActivity.this.getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Copy button click listener method
    private class btnCopyButtonListner implements View.OnClickListener {
        @SuppressLint({"WrongConstant"})
        public void onClick(View view) {
            if (TextToEmojiActivity.this.convertedText.getText().toString().isEmpty()) {
                Toast.makeText(TextToEmojiActivity.this.getApplicationContext(), "Convert text before copy", Toast.LENGTH_SHORT).show();
                return;
            }
            ((ClipboardManager) TextToEmojiActivity.this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(TextToEmojiActivity.this.inputText.getText().toString(), TextToEmojiActivity.this.convertedText.getText().toString()));
            Toast.makeText(TextToEmojiActivity.this.getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    //Share Button click listener method
    private class btnShareListner implements View.OnClickListener {
        public void onClick(View view) {
            if (TextToEmojiActivity.this.convertedText.getText().toString().isEmpty()) {
                Toast.makeText(TextToEmojiActivity.this.getApplicationContext(), "Convert text to share", Toast.LENGTH_LONG).show();
                return;
            }
            Intent shareIntent = new Intent();
            shareIntent.setAction("android.intent.action.SEND");
            shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra("android.intent.extra.TEXT", TextToEmojiActivity.this.convertedText.getText().toString());
            shareIntent.setType("text/plain");
            TextToEmojiActivity.this.startActivity(Intent.createChooser(shareIntent, "Select an app to share"));
        }
    }
    //Menu initialisation
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void loadIntersAd() {
        InterstitialAd.load(this,getResources().getString(R.string.ad_interst_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });

                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(TextToEmojiActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        }, 5000);
    }

}