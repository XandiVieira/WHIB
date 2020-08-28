package com.relyon.whib.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.relyon.whib.R;
import com.relyon.whib.activity.SettingsActivity;

public class DialogCongratsSubscription extends Dialog {

    public DialogCongratsSubscription(Activity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_congrats_subscription);
        CardView dialog = findViewById(R.id.dialog);
        TextView advantages = findViewById(R.id.advantages);
        dialog.setOnClickListener(v -> dismiss());

        advantages.setOnClickListener(v -> getContext().startActivity(new Intent(getContext(), SettingsActivity.class)));
    }
}