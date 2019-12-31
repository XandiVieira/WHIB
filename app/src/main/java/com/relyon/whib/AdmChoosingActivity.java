package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.relyon.whib.modelo.Util;

public class AdmChoosingActivity extends AppCompatActivity {

    private LinearLayout adm;
    private LinearLayout extra;
    private LinearLayout standard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adm_choosing);

        adm = findViewById(R.id.adm);
        extra = findViewById(R.id.extra);
        standard = findViewById(R.id.standard);

        adm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ControlAdminActivity.class);
                startActivity(intent);
            }
        });

        extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.getUser().setExtra(true);
                Util.getmUserDatabaseRef().child(Util.getUser().getUserUID()).setValue(Util.getUser());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        standard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.getUser().setExtra(false);
                Util.getmUserDatabaseRef().child(Util.getUser().getUserUID()).setValue(Util.getUser());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
