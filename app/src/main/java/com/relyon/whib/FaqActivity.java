package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FaqActivity extends AppCompatActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_PAGE_LOCATION = "https://whib.flycricket.io/privacy.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(v -> onBackPressed());

        String pageLocation = KEY_PAGE_LOCATION;
        WebView webView = findViewById(R.id.webView);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl(pageLocation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
    }
}