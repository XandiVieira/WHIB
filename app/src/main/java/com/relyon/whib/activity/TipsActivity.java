package com.relyon.whib.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;
import com.relyon.whib.dialog.DialogFinalWarn;
import com.relyon.whib.util.Util;
import com.relyon.whib.util.Constants;

public class TipsActivity extends AppCompatActivity {

    private ImageView back;
    private ImageView pyramid;
    private Button store;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        setLayoutAttributes();

        back.setOnClickListener(v -> onBackPressed());
        store.setOnClickListener(v -> startActivity(new Intent(this, StoreActivity.class)));

        if (Util.getUser().isFirstTime() || getIntent().hasExtra(Constants.SHOW_LAST_WARN) && getIntent().getBooleanExtra(Constants.SHOW_LAST_WARN, false) && Util.getUser().isFirstTime()) {
            DialogFinalWarn warn = new DialogFinalWarn(this);
            warn.show();
        }

        pyramid.setOnClickListener(v -> {
            Dialog fullScreen = new Dialog(this);
            if (fullScreen.getWindow() != null) {
                fullScreen.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                fullScreen.setContentView(getLayoutInflater().inflate(R.layout.full_screen, null));
                fullScreen.show();
            }
        });
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
        pyramid = findViewById(R.id.piramide);
        store = findViewById(R.id.store);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent;
        if (getIntent().hasExtra(Constants.CAME_FROM_PROFILE) && getIntent().getBooleanExtra(Constants.CAME_FROM_PROFILE, false)) {
            intent = new Intent(this, ProfileActivity.class);
        } else if (Util.getServer() != null) {
            intent = new Intent(this, TimelineActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }
}