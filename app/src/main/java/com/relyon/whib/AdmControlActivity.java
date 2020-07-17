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

import java.util.ArrayList;
import java.util.Arrays;
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
        Util.mSubjectDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> helperList = new ArrayList<>();
                Util.mSubjectDatabaseRef.removeEventListener(this);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subject subject1 = snapshot.getValue(Subject.class);
                    if (subject1 != null && subject1.getServers() != null) {
                        for (Server server : subject1.getServers().values()) {
                            helperList.add(server.getTempInfo().getNumber());
                        }
                    }
                }
                Integer[] number = new Integer[helperList.size()];
                helperList.toArray(number);
                Arrays.sort(number);
                Timeline tl = new Timeline(null, newSubject, null);
                ServerTempInfo serverTempInfo = new ServerTempInfo(0, true, (number.length > 0 && number[0] != null && number[number.length - 1] != null) ? findFirstMissing(number) : 0);
                Server server = new Server(UUID.randomUUID().toString(), serverTempInfo, newSubject, tl);
                HashMap<String, Server> map = new HashMap<>();
                map.put(server.getServerUID(), server);
                final Subject subject = new Subject(newSubject, map,
                        new Date().getTime(), true);
                Util.mSubjectDatabaseRef.child(server.getSubject()).setValue(subject);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public int findFirstMissing(Integer[] numbers) {
        for (int i = 0; i < numbers.length; i++) {
            int target = numbers[i];
            while (target < numbers.length && target != numbers[target]) {
                int new_target = numbers[target];
                numbers[target] = target;
                target = new_target;
            }
        }

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] != i) {
                return i;
            }
        }
        return numbers.length;
    }
}