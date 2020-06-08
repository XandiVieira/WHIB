package com.relyon.whib;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DialogPromoExtra extends Dialog implements
        View.OnClickListener {

    public AppCompatActivity c;
    public Dialog d;
    public Button yes, no;
    public ImageView closeIcon;

    public DialogPromoExtra(AppCompatActivity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_extra_version);
        yes = findViewById(R.id.advantagesButton);
        no = findViewById(R.id.continueButton);
        closeIcon = findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(this);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.advantagesButton:
                goExtraAdvantages();
                break;
            case R.id.continueButton:
                goExtra();
                break;
            case R.id.closeIcon:
                c.closeContextMenu();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void goExtraAdvantages() {
    }

    private void goExtra() {
    }
}