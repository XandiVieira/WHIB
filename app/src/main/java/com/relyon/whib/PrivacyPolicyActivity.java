package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.modelo.Util;

public class PrivacyPolicyActivity extends AppCompatActivity {

    public static final String KEY_PAGE_LOCATION = "file:///android_asset/privacyPolicy.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(v -> onBackPressed());

        WebView webView = findViewById(R.id.webView);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl(KEY_PAGE_LOCATION);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (Util.getUser() != null) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}