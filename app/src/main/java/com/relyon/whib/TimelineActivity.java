package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    private ImageView menu;
    private Subject subjectObj;
    private ArrayList<Comment> commentList;
    private RecyclerView rvComments;
    private AppCompatActivity activity;
    private boolean canPost = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        final ImageView back = findViewById(R.id.back);
        TextView subject = findViewById(R.id.subject);
        final ImageButton commentBt = findViewById(R.id.commentIcon);
        menu = findViewById(R.id.menu);
        rvComments = findViewById(R.id.rvComments);
        commentList = new ArrayList<>();
        activity = this;

        subjectObj = getIntent().getExtras().getParcelable("subject");
        assert subjectObj != null;
        subject.setText(subjectObj.getTitle());

        Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    comment.setCommentUID(snap.getKey());
                    commentList.add(comment);
                }
                if (!commentList.isEmpty()) {
                    initRecyclerViewComment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(TimelineActivity.this, menu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_timeline, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals(getString(R.string.settings))) {
                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (item.getTitle().equals(getString(R.string.store))) {
                            Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (item.getTitle().equals(getString(R.string.profile))) {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (item.getTitle().equals(getString(R.string.about))) {
                            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainScreen();
            }
        });

        commentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canPost) {
                    openCommentBox();
                } else {
                    Toast.makeText(getApplicationContext(), "Você já postou um comentário neste servidor ou já faz parte de um grupo!", Toast.LENGTH_SHORT).show();
                }
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

    private void backToMainScreen() {
        Util.getUser().getTempInfo().setCurrentServer(null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initRecyclerViewComment() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(layoutManager);
        RecyclerViewCommentAdapter adapter = new RecyclerViewCommentAdapter(getApplicationContext(), commentList, activity);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvComments.getContext(),
                layoutManager.getOrientation());
        rvComments.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider));
        rvComments.setAdapter(adapter);
    }

    private void openCommentBox() {
        DialogPostComment cdd = new DialogPostComment(this, subjectObj);
        cdd.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("tempInfo").child("currentServer").setValue(null);
        Util.getServer().getTempInfo().setQtdUsers(Util.getServer().getTempInfo().getQtdUsers() - 1);
        Util.getmServerDatabaseRef().child(Util.getServer().getServerUID()).child("tempInfo").setValue(Util.getServer().getTempInfo());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.getmServerDatabaseRef().child(Util.getServer().getServerUID()).child("tempInfo").setValue(Util.getServer().getTempInfo());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Util.getServer().getTempInfo().setQtdUsers(Util.getServer().getTempInfo().getQtdUsers() + 1);
        Util.getmServerDatabaseRef().child(Util.getServer().getServerUID()).child("tempInfo").setValue(Util.getServer().getTempInfo());
    }
}