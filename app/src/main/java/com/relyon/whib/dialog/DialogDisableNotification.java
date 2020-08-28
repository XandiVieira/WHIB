package com.relyon.whib.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;

public class DialogDisableNotification extends Dialog {

    private AppCompatActivity a;
    public Dialog d;

    public DialogDisableNotification(AppCompatActivity a) {
        super(a);
        this.a = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_disable_notification);
    }
}
