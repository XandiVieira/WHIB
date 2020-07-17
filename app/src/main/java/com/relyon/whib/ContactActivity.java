package com.relyon.whib;

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
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Complaint;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ContactActivity extends AppCompatActivity {

    private List<Complaint> complaintList = new ArrayList<>();
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ImageView back = findViewById(R.id.back);
        Button btFaq = findViewById(R.id.bt_faq);
        Button send = findViewById(R.id.send_complaint);
        empty = findViewById(R.id.empty);
        RecyclerView myComplaints = findViewById(R.id.myComplaints);

        Util.mDatabaseRef.child("complaint").orderByChild("senderUID").equalTo(Util.getUser().getUserUID()).addValueEventListener(new ValueEventListener() {
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
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                myComplaints.setLayoutManager(layoutManager);
                Collections.sort(complaintList, Complaint.dateComparator);
                RecyclerViewComplaintAdapter adapter = new RecyclerViewComplaintAdapter(getApplicationContext(), complaintList);
                myComplaints.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        EditText complaintTxt = findViewById(R.id.complaint);

        back.setOnClickListener(v -> onBackPressed());

        btFaq.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), FaqActivity.class));
        });

        send.setOnClickListener(v -> {
            if (complaintTxt.getText().toString().isEmpty() || complaintTxt.getText().toString().length() < 10) {
                Toast.makeText(getApplicationContext(), "Detalhe melhor o seu problema.", Toast.LENGTH_SHORT).show();
            } else {
                Complaint complaint = new Complaint(UUID.randomUUID().toString(), Util.getUser().getUserUID(), complaintTxt.getText().toString(), new Date().getTime());
                Util.mDatabaseRef.child("complaint").child(complaint.getComplaintId()).setValue(complaint);
                Toast.makeText(getApplicationContext(), "Requisição enviada com sucesso!", Toast.LENGTH_SHORT).show();
                complaintTxt.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
    }
}