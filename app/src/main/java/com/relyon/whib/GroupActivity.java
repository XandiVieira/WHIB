package com.relyon.whib;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupActivity extends AppCompatActivity {

    private RecyclerView rvArgument;
    private EmojiEditText inputMessage;
    private ArrayList<Argument> argumentList = new ArrayList<>();
    private EmojiPopup emojiPopup;
    private boolean isForSticker = true;
    private boolean cameFromProfile = false;
    private TextView empty;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new GoogleEmojiProvider());
        setContentView(R.layout.activity_group);

        if (getIntent().hasExtra("cameFromProfile")) {
            if (getIntent().getBooleanExtra("cameFromProfile", false)) {
                cameFromProfile = true;
            }
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ImageView back = findViewById(R.id.back);
        TextView serverRoom = findViewById(R.id.serverRoom);
        TextView subject = findViewById(R.id.subject);
        rvArgument = findViewById(R.id.rvArgument);
        inputMessage = findViewById(R.id.inputMessage);
        empty = findViewById(R.id.empty);
        LinearLayout sendView = findViewById(R.id.sendView);
        EmojiButton emojiButton = findViewById(R.id.sendEmoji);
        ImageView sendIcon = findViewById(R.id.sendIcon);
        ImageView leaveGroup = findViewById(R.id.leaveGroup);

        if (argumentList.isEmpty() && !Util.getGroup().isReady() && Util.getUser().getUserUID().equals(Util.getComment().getAuthorsUID())) {
            empty.setVisibility(View.VISIBLE);
        }

        if (getIntent().hasExtra("serverId") && getIntent().hasExtra("commentId")) {
            String serverId = getIntent().getStringExtra("serverId");
            String commentId = getIntent().getStringExtra("commentId");

            if (serverId != null && commentId != null) {
                Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(serverId).child("timeline")
                        .child("commentList").child(commentId).child("group").child("argumentList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        argumentList = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Argument argument = snap.getValue(Argument.class);
                            argumentList.add(argument);
                        }
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        layoutManager.setStackFromEnd(true);
                        rvArgument.setLayoutManager(layoutManager);
                        RecyclerViewArgumentAdapter adapter = new RecyclerViewArgumentAdapter(getApplicationContext(), argumentList);
                        rvArgument.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        inputMessage.setOnClickListener(v -> emojiPopup.dismiss());

        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    sendIcon.setImageDrawable(getResources().getDrawable(R.mipmap.send_icon));
                    isForSticker = false;
                } else {
                    sendIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_sticker));
                    isForSticker = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emojiButton.setOnClickListener(v -> {
            hideKeyboard(this);
            emojiPopup = EmojiPopup.Builder.fromRootView(inputMessage).setOnEmojiClickListener((emoji, imageView) -> {
                String text = inputMessage.getText() != null ? inputMessage.getText().toString() : "";
                if (emoji.getTooltipText() != null) {
                    inputMessage.setText(text + " " + emoji.getTooltipText().toString());
                    inputMessage.setSelection(inputMessage.getText().length());
                }
            }).build(inputMessage);
            emojiPopup.toggle(); // Toggles visibility of the Popup.
        });

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        serverRoom.setText("Servidor " + Util.getServer().getTempInfo().getNumber() + " - Sala " + Util.getGroup().getNumber());
        subject.setText(Util.getServer().getSubject());

        sendView.setOnClickListener(v -> {
            if (isForSticker) {
                DialogStickers cdd = new DialogStickers(this, Util.getUser().getProducts() != null ? new ArrayList<>(Util.getUser().getProducts().values()) : new ArrayList<>(), argumentList, true, null);
                cdd.show();
            } else {
                if (inputMessage.getText() != null && !inputMessage.getText().toString().isEmpty()) {
                    if (emojiPopup != null && emojiPopup.isShowing()) {
                        emojiPopup.dismiss();
                    }
                    sendMessage();
                    hideKeyboard(this);
                }
            }
        });
        leaveGroup.setOnClickListener(v -> leaveGroup());

        Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("tempInfo").child("activated").addListenerForSingleValueEvent(new ValueEventListener() {
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

        Sending sending = new Sending(type, current_date.getTime(), Util.getUser().getUserName(), Util.getUser().getUserUID(), Util.getSubject().getTitle());
        Argument argument = new Argument(inputMessage.getText().toString(), audioPath, Util.getGroup().getGroupUID(), current_date.getTime(), sending);
        Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline")
                .child("commentList").child(Util.getGroup().getCommentUID())
                .child("group").child("argumentList").push().setValue(argument);

        if (argumentList.isEmpty() && !Util.getGroup().isReady() && Util.getUser().getUserUID().equals(Util.getComment().getAuthorsUID())) {
            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline")
                    .child("commentList").child(Util.getGroup().getCommentUID())
                    .child("group").child("ready").setValue(true);
            empty.setVisibility(View.GONE);
        }
    }

    private void leaveGroup() {
        for (int i = 0; i < Util.getComment().getGroup().getUserListUID().size(); i++) {
            if (Util.getComment().getGroup().getUserListUID().get(i).equals(Util.getUser().getUserUID())) {
                Util.getComment().getGroup().getUserListUID().remove(i);
            }
        }
        if (Util.getComment().getGroup().getUserListUID().isEmpty()) {
            Util.setComment(null);
            Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("tempInfo").child("currentGroup").setValue(null);
            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline")
                    .child("commentList").child(Util.getGroup().getCommentUID()).setValue(null);
        } else {
            Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("tempInfo").child("currentGroup").setValue(null);
            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline")
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
        if (cameFromProfile) {
            intent = new Intent(getApplicationContext(), ProfileActivity.class);
        } else if (Util.getServer() != null) {
            intent = new Intent(getApplicationContext(), TimelineActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
    }

    private void backToMainScreen() {
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