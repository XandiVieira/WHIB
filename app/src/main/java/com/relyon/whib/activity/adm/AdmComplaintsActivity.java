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
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.Collections;

public class AdmComplaintsActivity extends AppCompatActivity {

    private Activity activity;
    private ArrayList<Complaint> complaintList;
    private RecyclerViewComplaintAdapter complaintAdapter;

    private RecyclerView rvComplaints;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_complaints);

        activity = this;

        setLayoutAttributes();

        Util.mDatabaseRef.child(Constants.DATABASE_REF_COMPLAINT).orderByChild(Constants.DATABASE_REF_ANSWERED).equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaintList = new ArrayList<>();
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
                setComplaintAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLayoutAttributes() {
        rvComplaints = findViewById(R.id.reports);
        empty = findViewById(R.id.empty);
    }

    private void setComplaintAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvComplaints.setLayoutManager(layoutManager);
        Collections.sort(complaintList, Complaint.dateComparator);
        complaintAdapter = new RecyclerViewComplaintAdapter(activity, complaintList);
        rvComplaints.setAdapter(complaintAdapter);
    }
}