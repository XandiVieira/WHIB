package com.relyon.whib.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import androidx.cardview.widget.CardView;

import com.relyon.whib.R;

public class DialogFinalWarn extends Dialog {

    public DialogFinalWarn(Activity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_final_warn);
        CardView dialog = findViewById(R.id.dialog);
        dialog.setOnClickListener(v -> dismiss());
    }
}