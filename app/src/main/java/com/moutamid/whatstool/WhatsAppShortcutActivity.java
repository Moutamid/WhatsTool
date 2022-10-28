package com.moutamid.whatstool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.moutamid.whatstool.shake_Detector.ShakeCallback;
import com.moutamid.whatstool.shake_Detector.ShakeDetector;
import com.moutamid.whatstool.shake_Detector.ShakeOptions;

public class WhatsAppShortcutActivity extends AppCompatActivity {
    SwitchMaterial ShakeButton;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_shortcut);

        ShakeButton = findViewById(R.id.btnShake);

        if (!(ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_NETWORK_STATE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.SET_WALLPAPER") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.INTERNET") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.SYSTEM_ALERT_WINDOW") == 0) && Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.ACCESS_NETWORK_STATE", "android.permission.SET_WALLPAPER", "android.permission.INTERNET", "android.permission.SYSTEM_ALERT_WINDOW"}, 0);
        }
        ShakeButton.setOnClickListener(new btnShakeListner());
        this.shakeDetector = new ShakeDetector(new ShakeOptions().background(Boolean.FALSE).interval(1000).shakeCount(2).sensibility(2.0f)).start(this, new shakeDetectListner());
    }

    //Method of shake listener
    private class btnShakeListner implements View.OnClickListener {
        public void onClick(View view) {
            if (ShakeButton.isChecked()) {
                shakeDetector.stopShakeDetector(WhatsAppShortcutActivity.this.getBaseContext());
            } else {
                shakeDetector.startShakeService(WhatsAppShortcutActivity.this.getBaseContext());
            }
        }
    }

    //Method of when shake is detect
    private class shakeDetectListner implements ShakeCallback {
        public void onShake() {
            startActivity(WhatsAppShortcutActivity.this.getPackageManager().getLaunchIntentForPackage("com.whatsapp"));
        }
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        this.shakeDetector.destroy(getBaseContext());
        super.onDestroy();
    }
}