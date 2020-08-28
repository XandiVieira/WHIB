package com.relyon.whib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;
import com.relyon.whib.modelo.Util;

public class PrivacyPolicyActivity extends AppCompatActivity {

    public static final String KEY_PAGE_LOCATION = "file:///android_asset/privacyPolicy.html";

    private ImageView back;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        setLayoutAttributes();

        back.setOnClickListener(v -> onBackPressed());

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl(KEY_PAGE_LOCATION);
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
        webView = findViewById(R.id.webView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (Util.getUser() != null) {
            startActivity(new Intent(this, AboutActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}