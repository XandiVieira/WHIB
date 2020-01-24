package com.relyon.whib;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.relyon.whib.modelo.Comment;

public class DialogReport extends Dialog {

    private EditText inputReport;
    private TextView reason1, reason2, reason3;
    private String reason;
    private Comment comment;

    public DialogReport(AppCompatActivity a, Comment comment) {
        super(a);
        this.comment = comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.report_user);
        Button report = findViewById(R.id.report_button);
        reason1 = findViewById(R.id.reason1);
        reason2 = findViewById(R.id.reason2);
        reason3 = findViewById(R.id.reason3);
        inputReport = findViewById(R.id.input_report);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reason != null) {
                    sendReport(inputReport.getText().toString(), reason);
                } else {
                    Toast.makeText(getContext(), "Selecione uma das razões anteriores!", Toast.LENGTH_LONG).show();
                }
            }
        });

        reason1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = reason2.getText().toString();
                reason1.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
                reason2.setBackground(getContext().getResources().getDrawable(R.color.white));
                reason3.setBackground(getContext().getResources().getDrawable(R.color.white));
            }
        });

        reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = reason2.getText().toString();
                reason1.setBackground(getContext().getResources().getDrawable(R.color.white));
                reason2.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
                reason3.setBackground(getContext().getResources().getDrawable(R.color.white));
            }
        });

        reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = reason3.getText().toString();
                reason1.setBackground(getContext().getResources().getDrawable(R.color.white));
                reason2.setBackground(getContext().getResources().getDrawable(R.color.white));
                reason3.setBackground(getContext().getResources().getDrawable(R.color.colorPrimary));
            }
        });
    }

    private void sendReport(String explanation, String reason) {

        dismiss();
        Toast.makeText(getContext(), "A sua denúncia foi enviada com sucesso!", Toast.LENGTH_SHORT).show();
    }
}
