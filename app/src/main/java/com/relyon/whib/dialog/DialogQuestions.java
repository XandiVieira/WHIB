package com.relyon.whib.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;

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
        setContentView(R.layout.dialog_questions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
        dismiss();
    }
}