package com.relyon.whib;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DialogQuestions extends Dialog implements
        View.OnClickListener {

    public AppCompatActivity c;
    public ImageView closeIcon;

    public DialogQuestions(AppCompatActivity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.questions_dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
        dismiss();
    }
}