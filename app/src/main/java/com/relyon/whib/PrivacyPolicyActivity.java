package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(v -> onBackPressed());

        /*Bundle b = new Bundle();
        b.putString(WebViewActivity.KEY_TITLE, getResources().getString(R.string.privacy_policy_title));
        b.putString(WebViewActivity.KEY_PAGE_LOCATION, "file:///android_asset/Scopefy_PoliticaPrivacidade.html");
        startWebViewActivity(b);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
    }
}
