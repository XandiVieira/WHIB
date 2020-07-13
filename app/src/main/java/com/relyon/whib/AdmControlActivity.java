package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.modelo.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AdmControlActivity extends AppCompatActivity {

    private ListView admServerList;
    private ArrayList<Server> serverListFiltered;
    private List<String> subjectsAdded;
    private AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adm_control);

        serverListFiltered = new ArrayList<>();
        subjectsAdded = new ArrayList<>();
        admServerList = findViewById(R.id.admServerList);
        Button reports = findViewById(R.id.reports);
        Button complaints = findViewById(R.id.complaints);
        Button storeItem = findViewById(R.id.storeItem);
        storeItem.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AdmCreateStoreItem.class)));
        activity = this;

        Util.mSubjectDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectsAdded = new ArrayList<>();
                serverListFiltered = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subject subject = snapshot.getValue(Subject.class);
                    if (subject != null && subject.getServers() != null) {
                        for (Server server : subject.getServers().values()) {
                            if (!subjectsAdded.contains(server.getSubject())) {
                                serverListFiltered.add(server);
                                subjectsAdded.add(server.getSubject());
                            }
                        }
                    }
                }
                AdapterAdmServer adapter = new AdapterAdmServer(serverListFiltered, activity);
                admServerList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Button createServer = findViewById(R.id.createServerButton);

        createServer.setOnClickListener(v -> callDialog());
        reports.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AdmReportsActivity.class)));
        complaints.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AdmComplaintsActivity.class)));
    }

    private void callDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Novo servidor");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> createServer(input.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createServer(String newSubject) {
        final List<Server> serverList = new ArrayList<>();
        Util.mSubjectDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Util.mSubjectDatabaseRef.removeEventListener(this);
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Server server = snap.getValue(Server.class);
                    serverList.add(server);
                }
                Timeline tl = new Timeline(null, newSubject, null);
                ServerTempInfo serverTempInfo = new ServerTempInfo(0, true, serverList.size() + 1);
                Server server = new Server(UUID.randomUUID().toString(), serverTempInfo, newSubject, tl);
                HashMap<String, Server> map = new HashMap<>();
                map.put(server.getServerUID(), server);
                final Subject subject = new Subject(UUID.randomUUID().toString(), map,
                        getCurrentDate(), true);
                Util.mSubjectDatabaseRef.child(server.getSubject()).setValue(subject);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");

        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        return dateFormat_hora.format(data_atual);
    }
}