package com.relyon.whib;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class DisableNotificationDialog extends Dialog {

    private AppCompatActivity a;
    public Dialog d;

    public DisableNotificationDialog(AppCompatActivity a) {
        super(a);
        this.a = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.disable_notification_dialog);
    }
}
