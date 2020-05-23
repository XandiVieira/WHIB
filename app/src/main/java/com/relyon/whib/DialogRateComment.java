package com.relyon.whib;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Group;
import com.relyon.whib.modelo.GroupTempInfo;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.modelo.Valuation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DialogRateComment extends Dialog implements
        View.OnClickListener {

    private AppCompatActivity c;
    public Dialog d;
    private TextView ratingTV;
    private float rating;
    public Comment comment;
    private List<Object> commentList;

    DialogRateComment(AppCompatActivity a, float rating, Comment comment, ArrayList<Object> elements) {
        super(a);
        this.c = a;
        this.rating = rating;
        this.comment = comment;
        this.commentList = elements;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rate_comment_dialog);
        Button rate = findViewById(R.id.rate_Button);
        Button cancel = findViewById(R.id.cancel_button);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingTV = findViewById(R.id.ratingTV);
        cancel.setOnClickListener(this);
        ratingTV.setText(String.valueOf(rating));
        rate.setOnClickListener(this);
        ratingBar.setRating(rating);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            ratingTV.setText(String.valueOf(rating));
            ratingBar1.setRating(rating);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rate_Button:
                confirmRate();
                break;
            case R.id.cancel_button:
                c.closeContextMenu();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void confirmRate() {
        comment.setNumberOfRatings(comment.getNumberOfRatings() + 1);
        comment.setSumOfRatings(comment.getSumOfRatings() + rating);
        comment.setRating(comment.getSumOfRatings() / comment.getNumberOfRatings());
        comment.getAlreadyRatedList().add(Util.getUser().getUserUID());
        Toast.makeText(getContext(), "Avaliação confirmada!", Toast.LENGTH_SHORT).show();
        int i = 0;
        for (int j = 0; j < commentList.size(); j++) {
            if (commentList.get(j) instanceof Comment) {
                Comment comment = (Comment) commentList.get(j);
                if (comment.isAGroup()) {
                    i++;
                }
            }
        }
        if (comment.getNumberOfRatings() >= 10 && comment.getRating() >= 3.5) {
            comment.setAGroup(true);
            if (comment.getGroup() == null) {
                createNewGroup(i + 1);
            }
        }
        Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").child(comment.getCommentUID()).setValue(comment);
        Util.mUserDatabaseRef.child(comment.getAuthorsUID()).child("valuation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Valuation valuation = dataSnapshot.getValue(Valuation.class);
                if (valuation != null) {
                    valuation.setNumberOfRatings(valuation.getNumberOfRatings() + 1);
                    valuation.setSumOfRatings(valuation.getSumOfRatings() + comment.getRating());
                }
                Util.mUserDatabaseRef.child(comment.getAuthorsUID()).child("valuation").setValue(valuation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createNewGroup(int i) {
        List<String> userUIDList = new ArrayList<>();
        userUIDList.add(Util.getUser().getUserUID());
        List<User> users = new ArrayList<>();
        users.add(Util.getUser());
        GroupTempInfo groupTempInfo = new GroupTempInfo(users, false);
        Group group = new Group(UUID.randomUUID().toString(), comment.getSubject().getSubjectUID(), i, Util.getServer().getTempInfo().getNumber(),
                groupTempInfo, "text", new ArrayList<>(), userUIDList,
                new ArrayList<>(), false, comment.getCommentUID());
        comment.setGroup(group);
        //Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").child(comment.getCommentUID()).child("commentGroup").setValue(group);
        sendNotification();
    }

    private void sendNotification() {
    }
}