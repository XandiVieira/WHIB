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
import com.relyon.whib.adapter.RecyclerViewComplaintAdapter;
import com.relyon.whib.modelo.Complaint;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.Constants;

import java.util.ArrayList;
import java.util.Collections;

public class AdmComplaintsActivity extends AppCompatActivity {

    private RecyclerView rvComplaints;
    private ArrayList<Complaint> complaintList;
    private RecyclerViewComplaintAdapter complaintAdapter;
    private TextView empty;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_complaints);

        activity = this;

        complaintList = new ArrayList<>();
        rvComplaints = findViewById(R.id.reports);
        empty = findViewById(R.id.empty);

        Util.mDatabaseRef.child(Constants.DATABASE_REF_COMPLAINT).orderByChild(Constants.DATABASE_REF_ANSWERED).equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Complaint complaint = snap.getValue(Complaint.class);
                    if (complaint != null) {
                        complaintList.add(complaint);
                    }
                }
                if (complaintList.size() > 0) {
                    empty.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.VISIBLE);
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                rvComplaints.setLayoutManager(layoutManager);
                Collections.sort(complaintList, Complaint.dateComparator);
                complaintAdapter = new RecyclerViewComplaintAdapter(activity, complaintList);
                rvComplaints.setAdapter(complaintAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}