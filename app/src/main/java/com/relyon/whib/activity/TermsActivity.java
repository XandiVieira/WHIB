package com.relyon.whib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

public class TermsActivity extends AppCompatActivity {

    private ImageView back;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        setLayoutAttributes();

        back.setOnClickListener(v -> onBackPressed());

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(Constants.TERMS_AND_CONDITIONS);
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