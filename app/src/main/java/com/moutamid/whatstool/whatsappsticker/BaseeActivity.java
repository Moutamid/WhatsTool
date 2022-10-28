package com.moutamid.whatstool.whatsappsticker;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseeActivity extends AppCompatActivity {
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
