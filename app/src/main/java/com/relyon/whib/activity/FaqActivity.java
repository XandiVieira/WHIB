package com.relyon.whib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;
import com.relyon.whib.activity.AboutActivity;

public class FaqActivity extends AppCompatActivity {

    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        setLayoutAttributes();

        back.setOnClickListener(v -> onBackPressed());
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, AboutActivity.class));
    }
}