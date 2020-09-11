package com.relyon.whib.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;
import com.relyon.whib.util.Util;

public class DialogWarnUserBlocked extends Dialog {

    private Long endDate;

    private TextView tvEndDate;

    public DialogWarnUserBlocked(AppCompatActivity activity, Long endDate) {
        super(activity);
        this.endDate = endDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_user_blocked);
        setTransparentBackground();

        setLayoutAttributes();

        tvEndDate.setText(Util.formatDate(endDate, "dd/MM/yyyy"));
    }

    private void setTransparentBackground() {
        if (getWindow() != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setLayoutAttributes() {
        tvEndDate = findViewById(R.id.end_date);
    }
}