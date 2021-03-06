package com.relyon.whib.activity;

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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.adapter.RecyclerViewArgumentAdapter;
import com.relyon.whib.dialog.DialogShowComment;
import com.relyon.whib.dialog.DialogStickers;
import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Sending;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;
import com.vanniktech.emoji.EmojiButton;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupActivity extends AppCompatActivity {

    private Activity activity;
    private ArrayList<Argument> arguments;
    private Comment comment;
    private Server server;
    private String commentUID;
    private RecyclerViewArgumentAdapter argumentAdapter;
    private boolean isForSticker = true;
    private boolean cameFromProfile = false;

    private RecyclerView rvArgument;
    private EmojiEditText inputMessage;
    private EmojiPopup emojiPopup;
    private EmojiButton emojiButton;
    private TextView empty;
    private TextView numberOfRoom;
    private TextView txtSubject;
    private LinearLayout sendLayout;
    private ImageView back;
    private ImageView sendIcon;
    private ImageView showComment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new GoogleEmojiProvider());
        setContentView(R.layout.activity_group);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        activity = this;

        setLayoutAttributes();

        if (intentContainsNeededParams()) {
            String subject = getIntent().getStringExtra(Constants.SUBJECT);
            String serverUID = getIntent().getStringExtra(Constants.SERVER_ID);
            commentUID = getIntent().getStringExtra(Constants.COMMENT_ID);
            Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(subject).child(Constants.DATABASE_REF_SERVERS).child(serverUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    server = snapshot.getValue(Server.class);
                    if (server != null) {
                        snapshot.getRef().child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST).child(commentUID).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                comment = snapshot.getValue(Comment.class);
                                if (comment != null) {
                                    verifyUserCameFromProfile();

                                    setArgumentAdapter();

                                    if (arguments.isEmpty() && !comment.getGroup().isReady() && Util.getUser().getUserUID().equals(comment.getAuthorsUID())) {
                                        empty.setVisibility(View.VISIBLE);
                                    }

                                    if (getIntent().hasExtra(Constants.SERVER_ID) && getIntent().hasExtra(Constants.COMMENT_ID)) {
                                        String serverId = getIntent().getStringExtra(Constants.SERVER_ID);
                                        String commentId = getIntent().getStringExtra(Constants.COMMENT_ID);

                                        if (serverId != null && commentId != null) {
                                            retrieveOwnerComment(serverId, commentId);
                                            retrieveArguments(serverId, commentId);
                                        }
                                    }
                                }

                                inputMessage.setOnClickListener(v -> {
                                    if (emojiPopup != null) {
                                        emojiPopup.dismiss();
                                    }
                                });

                                inputMessage.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence text, int start, int before, int count) {
                                        changeSendButton(text);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                                emojiButton.setOnClickListener(v -> {
                                    hideKeyboard(activity);
                                    emojiPopup = EmojiPopup.Builder.fromRootView(inputMessage).setOnEmojiClickListener((emoji, imageView) -> {
                                        String text = inputMessage.getText() != null ? inputMessage.getText().toString() : "";
                                        if (emoji.getTooltipText() != null) {
                                            inputMessage.setText(text + " " + emoji.getTooltipText().toString());
                                            inputMessage.setSelection(inputMessage.getText().length());
                                        }
                                    }).build(inputMessage);
                                    emojiPopup.toggle();
                                });

                                back.setOnClickListener(v -> onBackPressed());

                                numberOfRoom.setText(server != null ? "Servidor " + (server.getTempInfo().getNumber() + 1) + " - Sala " + comment.getGroup().getNumber() : "Sala " + comment.getGroup().getNumber());
                                txtSubject.setText(server.getSubject());

                                sendLayout.setOnClickListener(v -> {
                                    if (isForSticker) {
                                        DialogStickers cdd = new DialogStickers(activity, Util.getUser().getProducts() != null ? new ArrayList<>(Util.getUser().getProducts().values()) : new ArrayList<>(), arguments, comment, null, null, false);
                                        cdd.show();
                                    } else {
                                        if (inputMessage.getText() != null && !inputMessage.getText().toString().isEmpty()) {
                                            if (emojiPopup != null && emojiPopup.isShowing()) {
                                                emojiPopup.dismiss();
                                            }
                                            sendMessage();
                                        }
                                    }
                                });

                                showComment.setOnClickListener(v -> showComment());

                                Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(server.getServerUID()).child(Constants.DATABASE_REF_TEMP_INFO).child(Constants.DATABASE_REF_ACTIVATED).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Boolean activated = dataSnapshot.getValue(Boolean.class);
                                        if (activated != null && !activated) {
                                            onBackPressed();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private boolean intentContainsNeededParams() {
        return getIntent().hasExtra(Constants.SUBJECT) && getIntent().hasExtra(Constants.SERVER_ID) && getIntent().getStringExtra(Constants.SUBJECT) != null && getIntent().getStringExtra(Constants.SERVER_ID) != null;
    }

    private void verifyUserCameFromProfile() {
        if (getIntent().hasExtra(Constants.CAME_FROM_PROFILE)) {
            if (getIntent().getBooleanExtra(Constants.CAME_FROM_PROFILE, false)) {
                cameFromProfile = true;
            }
        }
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
        numberOfRoom = findViewById(R.id.serverRoom);
        txtSubject = findViewById(R.id.subject);
        rvArgument = findViewById(R.id.rv_argument);
        inputMessage = findViewById(R.id.input_message);
        empty = findViewById(R.id.empty);
        sendLayout = findViewById(R.id.send_view);
        emojiButton = findViewById(R.id.send_emoji);
        sendIcon = findViewById(R.id.send_icon);
        showComment = findViewById(R.id.show_comment);
    }

    private void setArgumentAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(false);
        rvArgument.setLayoutManager(layoutManager);
        arguments = new ArrayList<>();
        argumentAdapter = new RecyclerViewArgumentAdapter(activity, arguments);
        rvArgument.setAdapter(argumentAdapter);
    }

    private void retrieveOwnerComment(String serverId, String commentId) {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(serverId).child(Constants.DATABASE_REF_TIMELINE)
                .child(Constants.DATABASE_REF_COMMENT_LIST).child(commentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(serverId).child(Constants.DATABASE_REF_TIMELINE)
                        .child(Constants.DATABASE_REF_COMMENT_LIST).child(commentId).removeEventListener(this);
                comment = dataSnapshot.getValue(Comment.class);
                showComment.setVisibility(View.VISIBLE);
                sendLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveArguments(String serverId, String commentId) {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(serverId).child(Constants.DATABASE_REF_TIMELINE)
                .child(Constants.DATABASE_REF_COMMENT_LIST).child(commentId).child(Constants.DATABASE_REF_GROUP).child(Constants.DATABASE_REF_ARGUMENT_LIST).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arguments.add(snapshot.getValue(Argument.class));
                argumentAdapter.notifyItemInserted(arguments.size() - 1);
                rvArgument.scrollToPosition(arguments.size() - 1);
                if (empty.getVisibility() == View.VISIBLE) {
                    empty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeSendButton(CharSequence text) {
        if (text.length() > 0) {
            sendIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.send_icon, null));
            isForSticker = false;
        } else {
            sendIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sticker, null));
            isForSticker = true;
        }
    }

    private void showComment() {
        DialogShowComment dialogShowComment = new DialogShowComment(GroupActivity.this, this, comment);
        dialogShowComment.show();
    }

    private void sendMessage() {
        switch (comment.getGroup().getMode()) {
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

        Sending sending = new Sending(type, current_date.getTime(), Util.getUser().getUserName(), Util.getUser().getUserUID(), comment.getSubject());
        Argument argument = new Argument(inputMessage.getText().toString(), audioPath, comment.getGroup().getGroupUID(), current_date.getTime(), sending);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(server.getServerUID()).child(Constants.DATABASE_REF_TIMELINE)
                .child(Constants.DATABASE_REF_COMMENT_LIST).child(comment.getGroup().getCommentUID())
                .child(Constants.DATABASE_REF_GROUP).child(Constants.DATABASE_REF_ARGUMENT_LIST).push().setValue(argument);

        if (arguments.isEmpty() && !comment.getGroup().isReady() && Util.getUser().getUserUID().equals(comment.getAuthorsUID())) {
            Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(server.getServerUID()).child(Constants.DATABASE_REF_TIMELINE)
                    .child(Constants.DATABASE_REF_COMMENT_LIST).child(comment.getGroup().getCommentUID())
                    .child(Constants.DATABASE_REF_GROUP).child(Constants.DATABASE_REF_READY).setValue(true);
            empty.setVisibility(View.GONE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent;
        if (cameFromProfile) {
            intent = new Intent(this, ProfileActivity.class).putExtra(Constants.USER_ID, comment.getAuthorsUID());
        } else if (server != null) {
            intent = new Intent(this, TimelineActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }
}