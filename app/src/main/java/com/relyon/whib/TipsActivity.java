package com.relyon.whib;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.modelo.Util;

public class TipsActivity extends AppCompatActivity {

    private boolean zoom = false;
    private ImageView piramide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        ImageView back = findViewById(R.id.back);
        piramide = findViewById(R.id.piramide);
        back.setOnClickListener(v -> onBackPressed());

        piramide.setOnClickListener(v -> {
            Dialog fullScreen = new Dialog(this);
            fullScreen.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            fullScreen.setContentView(getLayoutInflater().inflate(R.layout.full_screen,null));
            fullScreen.show();
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