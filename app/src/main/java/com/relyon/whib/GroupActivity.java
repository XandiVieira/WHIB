package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Sending;
import com.relyon.whib.modelo.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupActivity extends AppCompatActivity {

    private RecyclerView rvArgument;
    private EditText inputMessage;
    private ArrayList<Argument> argumentList;
    private AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        activity = this;
        ImageView back = findViewById(R.id.back);
        TextView serverRoom = findViewById(R.id.serverRoom);
        TextView subject = findViewById(R.id.subject);
        rvArgument = findViewById(R.id.rvArgument);
        inputMessage = findViewById(R.id.inputMessage);
        LinearLayout sendView = findViewById(R.id.sendView);
        ImageView leaveGroup = findViewById(R.id.leaveGroup);

        Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline")
                .child("commentList").child(Util.getGroup().getCommentUID()).child("group").child("argumentList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                argumentList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Argument argument = snap.getValue(Argument.class);
                    argumentList.add(argument);
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                rvArgument.setLayoutManager(layoutManager);
                RecyclerViewArgumentAdapter adapter = new RecyclerViewArgumentAdapter(activity, argumentList);
                rvArgument.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        serverRoom.setText("Servidor " + Util.getServer().getTempInfo().getNumber() + " - Sala " + Util.getGroup().getNumber());
        subject.setText(Util.getServer().getSubject().getTitle());

        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup();
            }
        });

        Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("tempInfo").child("activated").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean activated = dataSnapshot.getValue(Boolean.class);
                if (!activated) {
                    backToMainScreen();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        if (Util.getGroup().getMode().equals("Áudio")) {
            createNewArgument("audio", null);
        } else if (Util.getGroup().getMode().equals("text")) {
            createNewArgument("text", null);
        } else if (Util.getGroup().getMode().equals("Time")) {
            createNewArgument("time", null);
        }
        inputMessage.setText("");
    }

    private void createNewArgument(String type, String audioPath) {
        SimpleDateFormat dateFormat_date = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
        SimpleDateFormat dateFormat_time = new SimpleDateFormat("HH:mm");

        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        String current_date = dateFormat_date.format(data_atual);
        String current_time = dateFormat_time.format(data_atual);
        Sending sending = new Sending(type, current_date, Util.getUser().getUserName(), Util.getUser().getUserUID(), Util.getSubject());
        Argument argument = new Argument(inputMessage.getText().toString(), audioPath, Util.getGroup().getGroupUID(), current_time, sending);
        Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline")
                .child("commentList").child(Util.getGroup().getCommentUID())
                .child("group").child("argumentList").push().setValue(argument);
    }

    private void leaveGroup() {
        for (int i = 0; i < Util.getComment().getGroup().getUserListUID().size(); i++) {
            if (Util.getComment().getGroup().getUserListUID().get(i).equals(Util.getUser().getUserUID())) {
                Util.getComment().getGroup().getUserListUID().remove(i);
                Util.getUser().getTempInfo().setCurrentGroup(null);
            }
        }
        if (Util.getComment().getGroup().getUserListUID().isEmpty()) {
            Util.setComment(null);
            Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("tempInfo").child("currentGroup").setValue(null);
            Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline")
                    .child("commentList").child(Util.getGroup().getCommentUID()).setValue(null);
        } else {
            Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("tempInfo").child("currentGroup").setValue(null);
            Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline")
                    .child("commentList").child(Util.getGroup().getCommentUID())
                    .child("group").child("userListUID").setValue(Util.getComment().getGroup().getUserListUID());

        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void backToMainScreen() {
        Util.getUser().getTempInfo().setCurrentServer(null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}