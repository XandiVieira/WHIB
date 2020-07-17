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
import com.relyon.whib.modelo.Complaint;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.Collections;

public class AdmComplaintsActivity extends AppCompatActivity {

    private RecyclerView complaints;
    private ArrayList<Complaint> complaintList = new ArrayList<>();
    private RecyclerViewComplaintAdapter recyclerViewComplaintAdapter;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_complaints);

        complaints = findViewById(R.id.reports);
        empty = findViewById(R.id.empty);

        Util.mDatabaseRef.child("complaint").orderByChild("answered").equalTo(false).addValueEventListener(new ValueEventListener() {
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
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                complaints.setLayoutManager(layoutManager);
                Collections.sort(complaintList, Complaint.dateComparator);
                recyclerViewComplaintAdapter = new RecyclerViewComplaintAdapter(getApplicationContext(), complaintList);
                complaints.setAdapter(recyclerViewComplaintAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}