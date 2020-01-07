package com.relyon.whib;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private boolean update;

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
                callDialog();
            }
        });
    }

    private void callDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Novo servidor");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                update = true;
                createServer(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createServer(String newSubject) {
        final Subject subject = new Subject(UUID.randomUUID().toString(), newSubject,
                getCurrentDate(), setNewPopularity(), true);
        final List<Server> serverList = new ArrayList<>();
        Util.getmServerDatabaseRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (update){
                   for (DataSnapshot snap : dataSnapshot.getChildren()) {
                       Server server = snap.getValue(Server.class);
                       serverList.add(server);
                   }
                   Timeline tl = new Timeline(null, subject, null);
                   ServerTempInfo serverTempInfo = new ServerTempInfo(0, true, serverList.size() + 1);
                   Server server = new Server(UUID.randomUUID().toString(), serverTempInfo, subject, tl);
                   update = false;
                   Util.mServerDatabaseRef.child(server.getServerUID()).setValue(server);
               }
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

    private Popularity setNewPopularity() {
        return new Popularity(0, 0, 1);
    }
}
