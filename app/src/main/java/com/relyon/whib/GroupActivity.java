package com.relyon.whib;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Sending;
import com.relyon.whib.modelo.Util;
import com.vanniktech.emoji.EmojiButton;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiImageView;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.google.GoogleEmojiProvider;
import com.vanniktech.emoji.listeners.OnEmojiClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupActivity extends AppCompatActivity {

    private RecyclerView rvArgument;
    private EmojiEditText inputMessage;
    private ArrayList<Argument> argumentList = new ArrayList<>();
    private EmojiButton emojiButton;
    private View view;
    private EmojiPopup emojiPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new GoogleEmojiProvider());
        setContentView(R.layout.activity_group);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ImageView back = findViewById(R.id.back);
        TextView serverRoom = findViewById(R.id.serverRoom);
        TextView subject = findViewById(R.id.subject);
        rvArgument = findViewById(R.id.rvArgument);
        inputMessage = findViewById(R.id.inputMessage);
        LinearLayout sendView = findViewById(R.id.sendView);
        emojiButton = findViewById(R.id.sendEmoji);
        ImageView leaveGroup = findViewById(R.id.leaveGroup);

        inputMessage.setOnClickListener(v -> emojiPopup.dismiss());

        emojiButton.setOnClickListener(v -> {
            hideKeyboard(this);
            emojiPopup = EmojiPopup.Builder.fromRootView(inputMessage).setOnEmojiClickListener(new OnEmojiClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onEmojiClick(@NonNull EmojiImageView emoji, @NonNull Emoji imageView) {
                    String text = inputMessage.getText() != null ? inputMessage.getText().toString() : "";
                    if (emoji.getTooltipText() != null) {
                        inputMessage.setText(text + " " + emoji.getTooltipText().toString());
                        inputMessage.setSelection(inputMessage.getText().length());
                    }
                }
            }).build(inputMessage);
            emojiPopup.toggle(); // Toggles visibility of the Popup.
        });

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
                RecyclerViewArgumentAdapter adapter = new RecyclerViewArgumentAdapter(getApplicationContext(), argumentList);
                rvArgument.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        serverRoom.setText("Servidor " + Util.getServer().getTempInfo().getNumber() + " - Sala " + Util.getGroup().getNumber());
        subject.setText(Util.getServer().getSubject().getTitle());

        sendView.setOnClickListener(v -> {
            if (inputMessage.getText() != null && !inputMessage.getText().toString().isEmpty()) {
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
                sendMessage();
                rvArgument.scrollToPosition(argumentList.size() - 1);
            }
        });
        leaveGroup.setOnClickListener(v -> leaveGroup());

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
        switch (Util.getGroup().getMode()) {
            case "Áudio":
                createNewArgument("audio", null);
                break;
            case "text":
                createNewArgument("text", null);
                break;
            case "Time":
                createNewArgument("time", null);
                break;
        }
        inputMessage.setText("");
    }

    private void createNewArgument(String type, String audioPath) {

        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date current_date = cal.getTime();

        Sending sending = new Sending(type, current_date.getTime(), Util.getUser().getUserName(), Util.getUser().getUserUID(), Util.getSubject());
        Argument argument = new Argument(inputMessage.getText().toString(), audioPath, Util.getGroup().getGroupUID(), current_date.getTime(), sending);
        Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline")
                .child("commentList").child(Util.getGroup().getCommentUID())
                .child("group").child("argumentList").push().setValue(argument);

        if (argumentList.isEmpty() && !Util.getGroup().isReady() && Util.getUser().getUserUID().equals(Util.getComment().getAuthorsUID())) {
            Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline")
                    .child("commentList").child(Util.getGroup().getCommentUID())
                    .child("group").child("ready").setValue(true);
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent;
        if (Util.getServer() != null) {
            intent = new Intent(getApplicationContext(), TimelineActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
    }

    private void backToMainScreen() {
        Util.getUser().getTempInfo().setCurrentServer(null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(intent);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}