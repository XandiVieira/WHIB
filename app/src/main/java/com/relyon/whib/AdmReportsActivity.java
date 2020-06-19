package com.relyon.whib;

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
import com.relyon.whib.modelo.Report;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.List;

public class AdmReportsActivity extends AppCompatActivity {

    private RecyclerView reports;
    private List<Report> reportList;
    private RecyclerViewReportAdapter recyclerViewReportAdapter;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_reports);

        reports = findViewById(R.id.reports);
        empty = findViewById(R.id.empty);

        Util.mDatabaseRef.child("report").orderByChild("reviewed").equalTo(false).addValueEventListener(new ValueEventListener() {
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
                recyclerViewReportAdapter = new RecyclerViewReportAdapter(reportList);
                reports.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                reports.setAdapter(recyclerViewReportAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}