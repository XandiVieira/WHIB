package com.relyon.whib;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

public class WarnReverseReport extends Dialog {

    private AppCompatActivity a;
    public Dialog d;

    public WarnReverseReport(AppCompatActivity a) {
        super(a);
        this.a = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_joke_warn);
    }
}