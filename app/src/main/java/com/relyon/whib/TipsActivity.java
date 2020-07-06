package com.relyon.whib;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
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
        ImageView piramide = findViewById(R.id.piramide);
        Button store = findViewById(R.id.store);
        back.setOnClickListener(v -> onBackPressed());

        store.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), StoreActivity.class)));

        piramide.setOnClickListener(v -> {
            Dialog fullScreen = new Dialog(this);
            if (fullScreen.getWindow() != null) {
                fullScreen.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                fullScreen.setContentView(getLayoutInflater().inflate(R.layout.full_screen, null));
                fullScreen.show();
            }
        });
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