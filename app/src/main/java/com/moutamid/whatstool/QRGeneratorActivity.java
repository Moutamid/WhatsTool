package com.moutamid.whatstool;

import static com.facebook.imageutils.HeifExifUtil.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class QRGeneratorActivity extends AppCompatActivity {

    public static final int Q_RCODE_WIDTH = 500;
    Bitmap qr;
    EditText text;
    Button generate, share;
    ImageView qrView;
    RelativeLayout rl;
    ImageView backBtn;

    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        backBtn = findViewById(R.id.backBtn);
        text = findViewById(R.id.etText);
        generate = findViewById(R.id.generateBTN);
        share = findViewById(R.id.shareCodeBTN);
        qrView = findViewById(R.id.qrView);
        rl = findViewById(R.id.rl);

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

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        generate.setOnClickListener(v -> {
            String text = this.text.getText().toString();
            try {
                qr = textToImageEncode(text);
                qrView.setImageBitmap(qr);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        });

        share.setOnClickListener(v -> {
            rl.setDrawingCacheEnabled(true);
            rl.setDrawingCacheEnabled(true);
            rl.buildDrawingCache();
            Bitmap drawingCache = rl.getDrawingCache();
            drawingCache.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
            Intent intent = new Intent("android.intent.action.SEND");
            intent.putExtra("android.intent.extra.STREAM", Uri.parse(MediaStore.Images.Media.insertImage(QRGeneratorActivity.this.getContentResolver(), drawingCache, "", (String) null)));
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent, "Share image via..."));
        });

    }

    public Bitmap textToImageEncode(String str) throws WriterException {
        int i;
        Resources resources;
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix encode = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, Q_RCODE_WIDTH, Q_RCODE_WIDTH, (Map<EncodeHintType, ?>) null);
            int width = encode.getWidth();
            int height = encode.getHeight();
            int[] iArr = new int[(width * height)];
            for (int i2 = 0; i2 < height; i2++) {
                int i3 = i2 * width;
                for (int i4 = 0; i4 < width; i4++) {
                    int i5 = i3 + i4;
                    if (encode.get(i4, i2)) {
                        resources = getResources();
                        i = R.color.black;
                    } else {
                        resources = getResources();
                        i = R.color.white;
                    }
                    iArr[i5] = resources.getColor(i);
                }
            }
            Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            createBitmap.setPixels(iArr, 0, Q_RCODE_WIDTH, 0, 0, width, height);
            return createBitmap;
        } catch (IllegalArgumentException unused) {
            return null;
        }
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
                    mInterstitialAd.show(QRGeneratorActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        }, 5000);
    }
}