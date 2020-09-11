package com.relyon.whib.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.R;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Report;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.UUID;

public class DialogReport extends Dialog {

    public AppCompatActivity activity;
    private String reason;
    private Comment comment;

    private EditText inputReport;
    private TextView reason1, reason2, reason3, reason4, reason5;
    private Button report;

    public DialogReport(AppCompatActivity activity, Comment comment) {
        super(activity);
        this.activity = activity;
        this.comment = comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_report_user);
        if (getWindow() != null) {
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        setTransparentBackground();

        setLayoutAttributes();

        report.setOnClickListener(v -> {
            if (reason != null) {
                boolean isReverseReport = false;
                if (reason.equals(reason1.getText().toString())) {
                    dismiss();
                    showDialogReverseReport();
                    isReverseReport = true;
                }
                sendReport(inputReport.getText().toString(), reason, isReverseReport);
            } else {
                Toast.makeText(getContext(), activity.getString(R.string.select_a_reason), Toast.LENGTH_LONG).show();
            }
        });

        reason1.setOnClickListener(v -> {
            reason = reason1.getText().toString();
            reason1.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
            reason2.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason3.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason4.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason5.setBackground(getContext().getResources().getDrawable(R.color.white));
        });

        reason2.setOnClickListener(v -> {
            reason = reason2.getText().toString();
            reason1.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason2.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
            reason3.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason4.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason5.setBackground(getContext().getResources().getDrawable(R.color.white));
        });

        reason3.setOnClickListener(v -> {
            reason = reason3.getText().toString();
            reason1.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason2.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason3.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
            reason4.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason5.setBackground(getContext().getResources().getDrawable(R.color.white));
        });

        reason4.setOnClickListener(v -> {
            reason = reason5.getText().toString();
            reason1.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason2.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason3.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason4.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
            reason5.setBackground(getContext().getResources().getDrawable(R.color.white));
        });

        reason5.setOnClickListener(v -> {
            reason = reason5.getText().toString();
            reason1.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason2.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason3.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason4.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason5.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
        });
    }

    private void setTransparentBackground() {
        if (getWindow() != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setLayoutAttributes() {
        report = findViewById(R.id.report_button);
        reason1 = findViewById(R.id.reason1);
        reason2 = findViewById(R.id.reason2);
        reason3 = findViewById(R.id.reason3);
        reason4 = findViewById(R.id.reason4);
        reason5 = findViewById(R.id.reason5);
        inputReport = findViewById(R.id.input_report);
    }

    private void sendReport(String explanation, String reason, boolean isReverseReport) {
        final Report report = new Report(Util.getUser().getUserUID(), !isReverseReport ? comment.getAuthorsUID() : Util.getUser().getUserUID(), reason, explanation, comment.getText(), isReverseReport, comment.getCommentUID());
        Util.mDatabaseRef.child(Constants.DATABASE_REF_REPORT).child(UUID.randomUUID().toString()).setValue(report);
        dismiss();
        if (!isReverseReport) {
            Toast.makeText(getContext(), getContext().getString(R.string.report_sent), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogReverseReport() {
        DialogWarnReverseReport dialogWarnReverseReport = new DialogWarnReverseReport(activity);
        dialogWarnReverseReport.show();
    }
}