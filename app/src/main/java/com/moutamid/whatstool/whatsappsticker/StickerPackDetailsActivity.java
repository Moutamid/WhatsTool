package com.moutamid.whatstool.whatsappsticker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.moutamid.whatstool.R;


import java.lang.ref.WeakReference;

public class StickerPackDetailsActivity extends AddStickerPackkkActivity {
    public static final String EXTRA_SHOW_UP_BUTTON = "show_up_button";
    public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    public static final String EXTRA_STICKER_PACK_DATA = "sticker_pack";
    public static final String EXTRA_STICKER_PACK_EMAIL = "sticker_pack_email";
    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";
    public static final String EXTRA_STICKER_PACK_PRIVACY_POLICY = "sticker_pack_privacy_policy";
    public static final String EXTRA_STICKER_PACK_TRAY_ICON = "sticker_pack_tray_icon";
    public static final String EXTRA_STICKER_PACK_WEBSITE = "sticker_pack_website";
//    private LinearLayout adContainer;
    private View addButton;
    private View alreadyAddedText;

    public View divider;
    private final RecyclerView.OnScrollListener dividerScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int i) {
            super.onScrollStateChanged(recyclerView, i);
            updateDivider(recyclerView);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int i, int i2) {
            super.onScrolled(recyclerView, i, i2);
            updateDivider(recyclerView);
        }

        private void updateDivider(RecyclerView recyclerView) {
            int i = 0;
            boolean z = recyclerView.computeVerticalScrollOffset() > 0;
            if (StickerPackDetailsActivity.this.divider != null) {
                View divider = StickerPackDetailsActivity.this.divider;
                if (!z) {
                    i = 4;
                }
                divider.setVisibility(i);
            }
        }
    };
    private GridLayoutManager layoutManager;
    private int numColumns;
    private final ViewTreeObserver.OnGlobalLayoutListener pageLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            StickerPackDetailsActivity stickerPackDetailsActivity = StickerPackDetailsActivity.this;
            stickerPackDetailsActivity.setNumColumns(stickerPackDetailsActivity.recyclerView.getWidth() / StickerPackDetailsActivity.this.recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size));
        }
    };

    RecyclerView recyclerView;
    private StickerPack stickerPack;
    private StickerPreviewAdapter stickerPreviewAdapter;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    private ImageView iv_back;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.sticker_pack_details);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        AdView adView = (AdView) findViewById(R.id.ll_ads);
//        Ads.bannerad(adView, this);
        boolean booleanExtra = getIntent().getBooleanExtra(EXTRA_SHOW_UP_BUTTON, false);
        this.stickerPack = (StickerPack) getIntent().getParcelableExtra(EXTRA_STICKER_PACK_DATA);
        TextView textView = (TextView) findViewById(R.id.pack_name);
        TextView textView2 = (TextView) findViewById(R.id.author);
        ImageView imageView = (ImageView) findViewById(R.id.tray_image);
        TextView textView3 = (TextView) findViewById(R.id.pack_size);
        this.addButton = findViewById(R.id.add_to_whatsapp_button);
        this.alreadyAddedText = findViewById(R.id.already_added_text);
        this.layoutManager = new GridLayoutManager(this, 1);
        this.recyclerView = (RecyclerView) findViewById(R.id.sticker_list);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this.pageLayoutListener);
        this.recyclerView.addOnScrollListener(this.dividerScrollListener);
        this.divider = findViewById(R.id.divider);
        if (this.stickerPreviewAdapter == null) {
            this.stickerPreviewAdapter = new StickerPreviewAdapter(getLayoutInflater(), R.drawable.sticker_error, getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size), getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_padding), this.stickerPack);
            this.recyclerView.setAdapter(this.stickerPreviewAdapter);
        }
        textView.setText(this.stickerPack.name);
        textView2.setText(this.stickerPack.publisher);
        imageView.setImageURI(StickerPackLoader.getStickerAssetUri(this.stickerPack.identifier, this.stickerPack.trayImageFile));
        textView3.setText(Formatter.formatShortFileSize(this, this.stickerPack.getTotalSize()));
        this.addButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickerPackDetailsActivity.this.lambda$onCreate$0$StickerPackDetailsActivity(view);
            }
        });
//        this.adContainer = (LinearLayout) findViewById(R.id.adView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(booleanExtra);
            getSupportActionBar().setTitle(booleanExtra ? R.string.title_activity_sticker_pack_details_multiple_pack : R.string.title_activity_sticker_pack_details_single_pack);
        }
    }

    public void lambda$onCreate$0$StickerPackDetailsActivity(View view) {
        addStickerPackToWhatsApp(this.stickerPack.identifier, this.stickerPack.name);
    }

    private void launchInfoActivity(String str, String str2, String str3, String str4) {
        Intent intent = new Intent(this, StickerPackInfoActivity.class);
        intent.putExtra(EXTRA_STICKER_PACK_ID, this.stickerPack.identifier);
        intent.putExtra(EXTRA_STICKER_PACK_WEBSITE, str);
        intent.putExtra(EXTRA_STICKER_PACK_EMAIL, str2);
        intent.putExtra(EXTRA_STICKER_PACK_PRIVACY_POLICY, str3);
        intent.putExtra(EXTRA_STICKER_PACK_TRAY_ICON, str4);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        StickerPack stickerPack2;
        if (menuItem.getItemId() != R.id.action_info || (stickerPack2 = this.stickerPack) == null) {
            return super.onOptionsItemSelected(menuItem);
        }
        launchInfoActivity(stickerPack2.publisherWebsite, this.stickerPack.publisherEmail, this.stickerPack.privacyPolicyWebsite, StickerPackLoader.getStickerAssetUri(this.stickerPack.identifier, this.stickerPack.trayImageFile).toString());
        return true;
    }


    public void setNumColumns(int i) {
        if (this.numColumns != i) {
            this.layoutManager.setSpanCount(i);
            this.numColumns = i;
            StickerPreviewAdapter stickerPreviewAdapter2 = this.stickerPreviewAdapter;
            if (stickerPreviewAdapter2 != null) {
                stickerPreviewAdapter2.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        this.whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
        this.whiteListCheckAsyncTask.execute(new StickerPack[]{this.stickerPack});
    }


    @Override
    public void onPause() {
        super.onPause();
        WhiteListCheckAsyncTask whiteListCheckAsyncTask2 = this.whiteListCheckAsyncTask;
        if (whiteListCheckAsyncTask2 != null && !whiteListCheckAsyncTask2.isCancelled()) {
            this.whiteListCheckAsyncTask.cancel(true);
        }
    }


    public void updateAddUI(Boolean bool) {
        if (bool.booleanValue()) {
            this.addButton.setVisibility(View.GONE);
            this.alreadyAddedText.setVisibility(View.VISIBLE);
            return;
        }
        this.addButton.setVisibility(View.VISIBLE);
        this.alreadyAddedText.setVisibility(View.GONE);
    }

    static class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, Boolean> {
        private final WeakReference<StickerPackDetailsActivity> stickerPackDetailsActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackDetailsActivity stickerPackDetailsActivity) {
            this.stickerPackDetailsActivityWeakReference = new WeakReference<>(stickerPackDetailsActivity);
        }


        public final Boolean doInBackground(StickerPack... stickerPackArr) {
            StickerPack stickerPack = stickerPackArr[0];
            StickerPackDetailsActivity stickerPackDetailsActivity = (StickerPackDetailsActivity) this.stickerPackDetailsActivityWeakReference.get();
            if (stickerPackDetailsActivity == null) {
                return false;
            }
            return Boolean.valueOf(WhitelistCheck.isWhitelisted(stickerPackDetailsActivity, stickerPack.identifier));
        }


        @Override
        public void onPostExecute(Boolean bool) {
            StickerPackDetailsActivity stickerPackDetailsActivity = (StickerPackDetailsActivity) this.stickerPackDetailsActivityWeakReference.get();
            if (stickerPackDetailsActivity != null) {
                stickerPackDetailsActivity.updateAddUI(bool);
            }
        }
    }
}
