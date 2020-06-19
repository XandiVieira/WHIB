package com.relyon.whib;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        LinearLayout aboutUs = findViewById(R.id.about_us);
        LinearLayout faq = findViewById(R.id.faq);
        LinearLayout terms = findViewById(R.id.use_terms);
        LinearLayout privacyPolicy = findViewById(R.id.privacy_policy);
        LinearLayout contactUs = findViewById(R.id.contact_us);
        ImageView back = findViewById(R.id.back);
        TextView version = findViewById(R.id.version);

        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionTxt = pInfo.versionName;
            version.setText("Version " + versionTxt);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Error", e.getMessage());
        }

        aboutUs.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AboutUsActivity.class)));

        faq.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FaqActivity.class)));

        terms.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), TermsActivity.class));
        });

        privacyPolicy.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
        });

        contactUs.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ContactActivity.class)));

        back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}