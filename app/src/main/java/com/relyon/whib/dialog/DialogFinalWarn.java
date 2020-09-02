package com.relyon.whib.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.cardview.widget.CardView;

import com.relyon.whib.R;

public class DialogFinalWarn extends Dialog {

    private CardView cardView;

    public DialogFinalWarn(Activity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_final_warn);
        setTransparentBackground();

        setLayoutAttributes();

        cardView.setOnClickListener(v -> dismiss());
    }

    private void setTransparentBackground() {
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setLayoutAttributes() {
        cardView = findViewById(R.id.dialog);
    }
}