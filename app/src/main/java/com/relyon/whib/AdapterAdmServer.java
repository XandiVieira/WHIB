package com.relyon.whib;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;

public class AdapterAdmServer extends BaseAdapter {

    private final ArrayList<Server> admServerList;
    private final AppCompatActivity act;

    AdapterAdmServer(ArrayList<Server> admServerList, AppCompatActivity act) {
        this.admServerList = admServerList;
        this.act = act;
    }

    @Override
    public int getCount() {
        return admServerList.size();
    }

    @Override
    public Object getItem(int position) {
        return admServerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = act.getLayoutInflater().inflate(R.layout.adm_server_item, parent, false);
        final Server server = admServerList.get(position);
        TextView subjectTitle = view.findViewById(R.id.subjectTitle);
        final Switch disableSwitch = view.findViewById(R.id.disableServer);
        ImageButton deleteButton = view.findViewById(R.id.delete);
        subjectTitle.setText(server.getSubject().getTitle());

        disableSwitch.setChecked(server.getTempInfo().isActivated());

        if (disableSwitch.isChecked()) {
            disableSwitch.setText("Ativo");
        } else {
            disableSwitch.setText("Desativo");
        }

        subjectTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(view, position);
            }
        });

        disableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    disableSwitch.setText("Ativo");
                } else {
                    disableSwitch.setText("Desativo");
                }
                server.getTempInfo().setActivated(disableSwitch.isChecked());
                Util.mServerDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Server server1 = snapshot.getValue(Server.class);
                            if (server1.getSubject().getTitle().equals(server.getSubject().getTitle())) {
                                Util.mServerDatabaseRef.child(server1.getServerUID()).child("tempInfo").child("activated").setValue(server.getTempInfo().isActivated());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }

    private void callDialog(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Title");

// Set up the input
        final EditText input = new EditText(view.getContext());
        input.setText(admServerList.get(position).getSubject().getTitle());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Util.mServerDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Server> helperList =  new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Server server = snapshot.getValue(Server.class);
                            if (server != null && server.getSubject() != null && server.getSubject().getTitle() != null && server.getSubject().getTitle().equals(admServerList.get(position).getSubject().getTitle())) {
                                helperList.add(server);
                            }
                        }
                        for (Server server : helperList){
                            Util.mServerDatabaseRef.child(server.getServerUID()).child("subject").child("title").setValue(input.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
}
