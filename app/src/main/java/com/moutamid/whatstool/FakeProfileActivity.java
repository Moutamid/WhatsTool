package com.moutamid.whatstool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import static com.facebook.imageutils.HeifExifUtil.TAG;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Locale;

public class FakeProfileActivity extends AppCompatActivity {
    TextView num1, num2, name, status, statusDate, block, create, lastSeen;
    ImageView profileImage, backbtn;

    String personName, mobile, personStatus, persStatusDate, lastseen;
    Uri imageUri;

    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_profile);

        num1 = findViewById(R.id.num);
        num2 = findViewById(R.id.numb);
        name = findViewById(R.id.name);
        lastSeen = findViewById(R.id.lastSeen);
        profileImage = findViewById(R.id.profileImage);
        status = findViewById(R.id.status);
        statusDate = findViewById(R.id.statusdate);
        create = findViewById(R.id.create);
        block = findViewById(R.id.block);
        backbtn = findViewById(R.id.backbtn);

        personName = getIntent().getStringExtra("name");
        mobile = getIntent().getStringExtra("mobile");
        lastseen = getIntent().getStringExtra("lastSeen");
        personStatus = getIntent().getStringExtra("status");
        persStatusDate = getIntent().getStringExtra("statusDate");
        imageUri = getIntent().getParcelableExtra("ImageURI");

        num1.setText(mobile);
        num2.setText(mobile);
        name.setText(personName);
        profileImage.setImageURI(imageUri);
        status.setText(personStatus);
        statusDate.setText(persStatusDate);
        create.setText("Create group with " + personName);
        block.setText("Block " + personName);

        if (lastseen.toLowerCase(Locale.ROOT).contains("online")){
            lastSeen.setText("Online");
            lastSeen.setTextColor(getResources().getColor(R.color.primary));
        } else if (lastseen.toLowerCase(Locale.ROOT).contains("typing")){
            lastSeen.setText("Typing...");
            lastSeen.setTextColor(getResources().getColor(R.color.primary));
        } else {
            lastSeen.setText(lastseen);
        }

        backbtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adRequest = new AdRequest.Builder().build();

        loadIntersAd();

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
                    mInterstitialAd.show(FakeProfileActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        }, 5000);
    }
}