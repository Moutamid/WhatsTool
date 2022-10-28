package com.moutamid.whatstool.statussaver;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.moutamid.whatstool.MainActivity;
import com.moutamid.whatstool.R;

public class RecentStoriesActivity extends AppCompatActivity {
    Fragment fragment = null;
    FrameLayout simpleFrameLayout;
    TabLayout tabLayout;
    ImageView backBtn;


    //Tab layout Listener
    private class btnTabLayoutListner implements TabLayout.OnTabSelectedListener {
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case 0:
                    RecentStoriesActivity.this.fragment = new VideoFragment();
                    break;
                case 1:
                    RecentStoriesActivity.this.fragment = new ImagesFragment();
                    break;
            }
            FragmentTransaction ft = RecentStoriesActivity.this.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.simpleFrameLayout, RecentStoriesActivity.this.fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }

        public void onTabUnselected(TabLayout.Tab tab) {
        }

        public void onTabReselected(TabLayout.Tab tab) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_stories);
        /*getSupportActionBar().setTitle("Recent Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        goToSecondFragment();
        backBtn = findViewById(R.id.backBtn);
        this.simpleFrameLayout = findViewById(R.id.simpleFrameLayout);
        this.tabLayout = findViewById(R.id.simpleTabLayout);
        TabLayout.Tab firstTab = this.tabLayout.newTab();
        firstTab.setText("Video");
        this.tabLayout.addTab(firstTab);
        TabLayout.Tab secondTab = this.tabLayout.newTab();
        secondTab.setText("Images");
        this.tabLayout.addTab(secondTab);
        this.tabLayout.setOnTabSelectedListener(new btnTabLayoutListner());

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void goToSecondFragment() {
        this.fragment = new VideoFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.simpleFrameLayout, this.fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
