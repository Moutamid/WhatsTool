package com.moutamid.whatstool.whatsappsticker;

import static com.facebook.imageutils.HeifExifUtil.TAG;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.moutamid.whatstool.AboutActivity;
import com.moutamid.whatstool.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.whatstool.MainActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StickerPackListActivity extends AddStickerPackkkActivity {
    public static final String EXTRA_STICKER_PACK_LIST_DATA = "sticker_pack_list";

    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    public StickerPackListAdapter allStickerPacksListAdapter;
    private ImageView ivBack;
//    private AdView llAds;
    private final StickerPackListAdapter.OnAddButtonClickedListener onAddButtonClickedListener = new StickerPackListAdapter.OnAddButtonClickedListener() {
        public final void onAddButtonClicked(StickerPack stickerPack) {
            addStickerPackToWhatsApp(stickerPack.identifier, stickerPack.name);
        }
    };
    private LinearLayoutManager packLayoutManager;
    private RecyclerView packRecyclerView;
    private ArrayList<StickerPack> stickerPackList;
    private TextView tv_title;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_sticker_pack_list);

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

        ImageView backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

//        this.llAds = (AdView) findViewById(R.id.ll_ads);
//        Ads.bannerad(this.llAds, this);
        this.packRecyclerView = (RecyclerView) findViewById(R.id.sticker_pack_list);
        this.stickerPackList = getIntent().getParcelableArrayListExtra(EXTRA_STICKER_PACK_LIST_DATA);
        showStickerPackList(this.stickerPackList);
    }


    @Override
    public void onResume() {
        super.onResume();
        this.whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
        WhiteListCheckAsyncTask whiteListCheckAsyncTask2 = this.whiteListCheckAsyncTask;
        ArrayList<StickerPack> arrayList = this.stickerPackList;
        whiteListCheckAsyncTask2.execute(arrayList.toArray(new StickerPack[arrayList.size()]));
    }


    @Override
    public void onPause() {
        super.onPause();
        WhiteListCheckAsyncTask whiteListCheckAsyncTask2 = this.whiteListCheckAsyncTask;
        if (whiteListCheckAsyncTask2 != null && !whiteListCheckAsyncTask2.isCancelled()) {
            this.whiteListCheckAsyncTask.cancel(true);
        }
    }

    private void showStickerPackList(List<StickerPack> list) {
        this.allStickerPacksListAdapter = new StickerPackListAdapter(list, this.onAddButtonClickedListener);
        this.packRecyclerView.setAdapter(this.allStickerPacksListAdapter);
        this.packLayoutManager = new LinearLayoutManager(this);
        this.packLayoutManager.setOrientation(1);
        this.packRecyclerView.addItemDecoration(new DividerItemDecoration(this.packRecyclerView.getContext(), this.packLayoutManager.getOrientation()));
        this.packRecyclerView.setLayoutManager(this.packLayoutManager);
        this.packRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public final void onGlobalLayout() {
                StickerPackListActivity.this.recalculateColumnCount();
            }
        });
    }



    public void recalculateColumnCount() {
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size);
        StickerPackListItemViewHolder stickerPackListItemViewHolder = (StickerPackListItemViewHolder) this.packRecyclerView.findViewHolderForAdapterPosition(this.packLayoutManager.findFirstVisibleItemPosition());
        if (stickerPackListItemViewHolder != null) {
            this.allStickerPacksListAdapter.setMaxNumberOfStickersInARow(Math.min(5, Math.max(stickerPackListItemViewHolder.imageRowView.getMeasuredWidth() / dimensionPixelSize, 1)));
        }
    }

    static class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, List<StickerPack>> {
        private final WeakReference<StickerPackListActivity> stickerPackListActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackListActivity stickerPackListActivity) {
            this.stickerPackListActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }


        public final List<StickerPack> doInBackground(StickerPack... stickerPackArr) {
            StickerPackListActivity stickerPackListActivity = (StickerPackListActivity) this.stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity == null) {
                return Arrays.asList(stickerPackArr);
            }
            for (StickerPack stickerPack : stickerPackArr) {
                stickerPack.setIsWhitelisted(WhitelistCheck.isWhitelisted(stickerPackListActivity, stickerPack.identifier));
            }
            return Arrays.asList(stickerPackArr);
        }


        @Override
        public void onPostExecute(List<StickerPack> list) {
            StickerPackListActivity stickerPackListActivity = (StickerPackListActivity) this.stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity != null) {
                stickerPackListActivity.allStickerPacksListAdapter.setStickerPackList(list);
                stickerPackListActivity.allStickerPacksListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
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
                    mInterstitialAd.show(StickerPackListActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        }, 5000);
    }

}
