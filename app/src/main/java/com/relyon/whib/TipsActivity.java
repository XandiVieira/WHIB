package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.modelo.Util;

public class TipsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent;
        if (Util.getServer() != null) {
            intent = new Intent(getApplicationContext(), TimelineActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
    }
}
