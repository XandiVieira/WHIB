package com.relyon.whib.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;

import java.util.Objects;

public class AboutUsActivity extends AppCompatActivity {

    private ImageView back;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        setLayoutAttributes();

        setAppVersion();

        back.setOnClickListener(v -> onBackPressed());
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
        version = findViewById(R.id.version);
    }

    private void setAppVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionTxt = pInfo.versionName;
            version.setText(getString(R.string.version, versionTxt));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(getString(R.string.error), Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, AboutActivity.class));
    }
}