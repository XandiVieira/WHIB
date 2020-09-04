package com.relyon.whib.activity.adm;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.adapter.RecyclerViewAdmServerAdapter;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AdmControlActivity extends AppCompatActivity {

    private AppCompatActivity activity;
    private ArrayList<Server> serverListFiltered;
    private List<String> subjectsAdded;

    private RecyclerView rvAdmServers;
    private Button reports;
    private Button complaints;
    private Button storeItem;
    private Button createServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_control);

        activity = this;

        setLayoutAttributes();

        storeItem.setOnClickListener(v -> startActivity(new Intent(this, AdmCreateStoreItem.class)));

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
                setServerAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        createServer.setOnClickListener(v -> callDialog());
        reports.setOnClickListener(v -> startActivity(new Intent(this, AdmReportsActivity.class)));
        complaints.setOnClickListener(v -> startActivity(new Intent(this, AdmComplaintsActivity.class)));
    }

    private void setLayoutAttributes() {
        rvAdmServers = findViewById(R.id.adm_server_list);
        reports = findViewById(R.id.reports);
        complaints = findViewById(R.id.complaints);
        storeItem = findViewById(R.id.storeItem);
        createServer = findViewById(R.id.createServerButton);
    }

    private void setServerAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvAdmServers.setLayoutManager(layoutManager);
        RecyclerViewAdmServerAdapter admServerAdapter = new RecyclerViewAdmServerAdapter(serverListFiltered, activity, getApplicationContext());
        rvAdmServers.setAdapter(admServerAdapter);
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
                List<Integer> takenNumbers = new ArrayList<>();
                Util.mSubjectDatabaseRef.removeEventListener(this);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subject subject1 = snapshot.getValue(Subject.class);
                    if (subject1 != null && subject1.getServers() != null) {
                        for (Server server : subject1.getServers().values()) {
                            takenNumbers.add(server.getTempInfo().getNumber());
                        }
                    }
                }
                Integer[] sortedTakenNumbers = new Integer[takenNumbers.size()];
                takenNumbers.toArray(sortedTakenNumbers);
                Arrays.sort(sortedTakenNumbers);
                Timeline tl = new Timeline(null, newSubject, null);
                ServerTempInfo serverTempInfo = new ServerTempInfo(0, true, (sortedTakenNumbers.length > 0 && sortedTakenNumbers[0] != null && sortedTakenNumbers[sortedTakenNumbers.length - 1] != null) ? findFirstMissing(sortedTakenNumbers) : 0);
                Server server = new Server(UUID.randomUUID().toString(), serverTempInfo, newSubject, tl);
                HashMap<String, Server> serversMap = new HashMap<>();
                serversMap.put(server.getServerUID(), server);
                final Subject subject = new Subject(newSubject, serversMap,
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