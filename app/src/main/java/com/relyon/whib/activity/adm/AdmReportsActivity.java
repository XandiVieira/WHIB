package com.relyon.whib.activity.adm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.adapter.RecyclerViewReportAdapter;
import com.relyon.whib.modelo.Report;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.List;

public class AdmReportsActivity extends AppCompatActivity {

    private Activity activity;
    private List<Report> reportList;
    private RecyclerViewReportAdapter reportAdapter;

    private RecyclerView rvReports;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_reports);

        activity = this;

        setLayoutAttributes();

        Util.mDatabaseRef.child(Constants.DATABASE_REF_REPORT).orderByChild(Constants.DATABASE_REF_REVIEWED).equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Report report = snap.getValue(Report.class);
                    if (report != null) {
                        report.setId(snap.getKey());
                        reportList.add(report);
                        empty.setVisibility(View.GONE);
                    }
                }
                setReportAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLayoutAttributes() {
        rvReports = findViewById(R.id.reports);
        empty = findViewById(R.id.empty);
    }

    private void setReportAdapter() {
        reportAdapter = new RecyclerViewReportAdapter(reportList);
        rvReports.setLayoutManager(new LinearLayoutManager(activity));
        rvReports.setAdapter(reportAdapter);
    }
}