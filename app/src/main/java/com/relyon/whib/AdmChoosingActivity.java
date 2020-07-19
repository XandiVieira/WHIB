package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.modelo.Util;

public class AdmChoosingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adm_choosing);

        LinearLayout adm = findViewById(R.id.adm);
        LinearLayout extra = findViewById(R.id.extra);
        LinearLayout standard = findViewById(R.id.standard);

        adm.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdmControlActivity.class);
            startActivity(intent);
        });

        extra.setOnClickListener(v -> {
            Util.getUser().setExtra(true);
            Util.getmUserDatabaseRef().child(Util.getUser().getUserUID()).setValue(Util.getUser());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        standard.setOnClickListener(v -> {
            Util.getUser().setExtra(false);
            Util.getmUserDatabaseRef().child(Util.getUser().getUserUID()).setValue(Util.getUser());
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}