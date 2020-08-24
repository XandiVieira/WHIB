package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AdmChoosingProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adm_choosing_profile);

        LinearLayout adm = findViewById(R.id.adm);
        LinearLayout extra = findViewById(R.id.extra);
        LinearLayout standard = findViewById(R.id.standard);

        adm.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdmControlActivity.class);
            startActivity(intent);
        });

        extra.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        standard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}