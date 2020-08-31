package com.relyon.whib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.relyon.whib.util.Util;
import com.relyon.whib.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ContactActivity extends AppCompatActivity {

    private Activity activity;
    private List<Complaint> complaintList = new ArrayList<>();

    private TextView empty;
    private EditText complaintTxt;
    private ImageView back;
    private Button faqButton;
    private Button sendButton;
    private RecyclerView myComplaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        activity = this;

        setLayoutAttributes();

        retrieveComplaints();

        back.setOnClickListener(v -> onBackPressed());
        faqButton.setOnClickListener(v -> startActivity(new Intent(this, FaqActivity.class)));
        sendButton.setOnClickListener(v -> validateComplaint());
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
        faqButton = findViewById(R.id.bt_faq);
        sendButton = findViewById(R.id.send_complaint);
        empty = findViewById(R.id.empty);
        myComplaints = findViewById(R.id.myComplaints);
        complaintTxt = findViewById(R.id.complaint);
    }

    private void retrieveComplaints() {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_COMPLAINT).orderByChild(Constants.DATABASE_REF_USER_SENDER_ID).equalTo(Util.getUser().getUserUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaintList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Complaint complaint = snap.getValue(Complaint.class);
                    if (complaint != null) {
                        complaintList.add(complaint);
                    }
                }
                if (complaintList.size() > 0) {
                    empty.setText(getString(R.string.your_requests));
                } else {
                    empty.setText(getString(R.string.no_requests));
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                myComplaints.setLayoutManager(layoutManager);
                Collections.sort(complaintList, Complaint.dateComparator);
                RecyclerViewComplaintAdapter adapter = new RecyclerViewComplaintAdapter(activity, complaintList);
                myComplaints.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validateComplaint() {
        if (complaintTxt.getText().toString().isEmpty() || complaintTxt.getText().toString().length() < 10) {
            Toast.makeText(this, R.string.add_more_detail, Toast.LENGTH_SHORT).show();
        } else {
            sendComplaint();
            Toast.makeText(this, R.string.request_sent, Toast.LENGTH_SHORT).show();
            complaintTxt.setText("");
        }
    }

    private void sendComplaint() {
        Complaint complaint = new Complaint(UUID.randomUUID().toString(), Util.getUser().getUserUID(), complaintTxt.getText().toString(), new Date().getTime());
        Util.mDatabaseRef.child(Constants.DATABASE_REF_COMPLAINT).child(complaint.getComplaintId()).setValue(complaint);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, AboutActivity.class));
    }
}