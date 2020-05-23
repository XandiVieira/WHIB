package com.relyon.whib;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Report;
import com.relyon.whib.modelo.Util;

import java.util.UUID;

public class DialogReport extends Dialog {

    private EditText inputReport;
    private TextView reason1, reason2, reason3, reason4;
    private String reason;
    private Comment comment;
    public AppCompatActivity a;

    DialogReport(AppCompatActivity a, Comment comment) {
        super(a);
        this.a = a;
        this.comment = comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.report_user_dialog);
        Button report = findViewById(R.id.report_button);
        reason1 = findViewById(R.id.reason1);
        reason2 = findViewById(R.id.reason2);
        reason3 = findViewById(R.id.reason3);
        reason4 = findViewById(R.id.reason4);
        inputReport = findViewById(R.id.input_report);

        report.setOnClickListener(v -> {
            if (reason != null) {
                if (reason.equals(reason1.getText().toString())) {
                    dismiss();
                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(a);
                } else {
                    sendReport(inputReport.getText().toString(), reason);
                }
            } else {
                Toast.makeText(getContext(), a.getString(R.string.select_a_reason), Toast.LENGTH_LONG).show();
            }
        });

        reason1.setOnClickListener(v -> {
            reason = reason1.getText().toString();
            reason1.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
            reason2.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason3.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason4.setBackground(getContext().getResources().getDrawable(R.color.white));
        });

        reason2.setOnClickListener(v -> {
            reason = reason2.getText().toString();
            reason1.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason2.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
            reason3.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason4.setBackground(getContext().getResources().getDrawable(R.color.white));
        });

        reason3.setOnClickListener(v -> {
            reason = reason3.getText().toString();
            reason1.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason2.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason3.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
            reason4.setBackground(getContext().getResources().getDrawable(R.color.white));
        });

        reason4.setOnClickListener(v -> {
            reason = reason4.getText().toString();
            reason1.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason2.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason3.setBackground(getContext().getResources().getDrawable(R.color.white));
            reason4.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
        });
    }

    public static class ViewDialog {

        void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.reverse_report_dialog);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.show();
        }
    }

    private void sendReport(String explanation, String reason) {
        final Report report = new Report(Util.getUser().getUserUID(), comment.getAuthorsUID(), reason, explanation, comment.getText());
        Util.getmReportDatabaseRef().child(UUID.randomUUID().toString()).setValue(report);
        dismiss();
        Toast.makeText(getContext(), getContext().getString(R.string.report_sent), Toast.LENGTH_SHORT).show();
    }
}
