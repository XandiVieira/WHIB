package com.relyon.whib;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class WarnReason extends Dialog {

    private AppCompatActivity a;
    public Dialog d;

    public WarnReason(AppCompatActivity a) {
        super(a);
        this.a = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reason_warn);
    }
}
