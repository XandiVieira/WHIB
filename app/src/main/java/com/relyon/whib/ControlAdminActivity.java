package com.relyon.whib;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Popularity;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.modelo.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ControlAdminActivity extends AppCompatActivity {

    private Button createServer;
    private ListView admServerList;
    private List<Server> serverListComplete;
    private ArrayList<Server> serverListFiltered;
    private List<String> subjectsAdded;
    private AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_control);

        serverListComplete = new ArrayList<>();
        serverListFiltered = new ArrayList<>();
        subjectsAdded = new ArrayList<>();
        admServerList = findViewById(R.id.admServerList);
        activity = this;

        Util.getmServerDatabaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serverListComplete = new ArrayList<>();
                subjectsAdded = new ArrayList<>();
                serverListFiltered = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    serverListComplete.add(snap.getValue(Server.class));
                }
                for (int i = 0; i < serverListComplete.size(); i++) {
                    if (!subjectsAdded.contains(serverListComplete.get(i).getSubject().getTitle())) {
                        serverListFiltered.add(serverListComplete.get(i));
                        subjectsAdded.add(serverListComplete.get(i).getSubject().getTitle());
                    }
                }
                AdapterAdmServer adapter = new AdapterAdmServer(serverListFiltered, activity);
                admServerList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        createServer = findViewById(R.id.createServerButton);

        createServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createServers();
            }
        });
    }

    private void createServers() {
        ArrayList<Server> serverList = new ArrayList<>();
        for (int i = 0; i < Util.subjectList.size(); i++) {
            if (!Util.subjectList.get(i).equals("")) {
                Subject subject2 = new Subject(UUID.randomUUID().toString(), Util.subjectList.get(i),
                        getCurrentDate(), setNewPopularity(), true);
                ServerTempInfo serverTempInfo2 = new ServerTempInfo(0, true, serverList.size() + 1);
                String type;
                if (i == 0) {
                    type = "main";
                } else {
                    type = "secondary";
                }
                Timeline tl = new Timeline(null, subject2, null);
                serverList.add(new Server(UUID.randomUUID().toString(), type, serverTempInfo2, subject2, tl));
                Util.getmServerDatabaseRef().child(serverList.get(i).getType()).child(serverList.get(i).getServerUID()).setValue(serverList.get(i));
            }
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");

        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        return dateFormat_hora.format(data_atual);
    }

    private Popularity setNewPopularity() {
        return new Popularity(0, 0, 1);
    }
}
