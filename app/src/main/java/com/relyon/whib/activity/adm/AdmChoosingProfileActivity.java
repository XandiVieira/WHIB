package com.relyon.whib.activity.adm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;
import com.relyon.whib.activity.MainActivity;
import com.relyon.whib.activity.ProfileActivity;

public class AdmChoosingProfileActivity extends AppCompatActivity {

    private LinearLayout admLayout;
    private LinearLayout extraLayout;
    private LinearLayout standardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_choosing_profile);

        setLayoutAttributes();

        admLayout.setOnClickListener(v -> startActivity(new Intent(this, AdmControlActivity.class)));

        extraLayout.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        standardLayout.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void setLayoutAttributes() {
        admLayout = findViewById(R.id.adm);
        extraLayout = findViewById(R.id.extra);
        standardLayout = findViewById(R.id.standard);
    }
}