package com.relyon.whib.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Group;
import com.relyon.whib.modelo.GroupTempInfo;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Valuation;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DialogRateComment extends Dialog implements
        View.OnClickListener {

    private Activity activity;
    private float rating;
    private List<Object> commentList;
    private Boolean commentOwnerIsExtra;

    private TextView ratingTV;
    public Comment comment;
    private Button rate;
    private Button cancel;
    private MaterialRatingBar ratingBar;

    public DialogRateComment(Activity activity, float rating, Comment comment, ArrayList<Object> elements, Boolean commentOwnerIsExtra) {
        super(activity);
        this.activity = activity;
        this.rating = rating;
        this.comment = comment;
        this.commentList = elements;
        this.commentOwnerIsExtra = commentOwnerIsExtra;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rate_comment);
        setTransparentBackground();

        setLayoutAttributes();

        cancel.setOnClickListener(this);
        ratingTV.setText(String.valueOf(rating));
        rate.setOnClickListener(this);
        ratingBar.setRating(rating);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            ratingTV.setText(String.valueOf(rating));
            ratingBar1.setRating(rating);
            this.rating = rating;
        });
    }

    private void setLayoutAttributes() {
        rate = findViewById(R.id.rate_Button);
        cancel = findViewById(R.id.cancel_button);
        ratingBar = findViewById(R.id.ratingBar);
        ratingTV = findViewById(R.id.ratingTV);
    }

    private void setTransparentBackground() {
        if (getWindow() != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rate_Button:
                confirmRate();
                break;
            case R.id.cancel_button:
                activity.closeContextMenu();
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
        isWorthyToBeAGroup(i + 1);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(Util.getServer().getSubject()).child(Constants.DATABASE_REF_SERVERS).child(Util.getServer().getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST).child(comment.getCommentUID()).setValue(comment);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(comment.getAuthorsUID()).child(Constants.DATABASE_REF_VALUATION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(comment.getAuthorsUID()).child(Constants.DATABASE_REF_VALUATION).removeEventListener(this);
                Valuation valuation = dataSnapshot.getValue(Valuation.class);
                if (valuation != null) {
                    valuation.setNumberOfRatings(valuation.getNumberOfRatings() + 1);
                    valuation.setSumOfRatings(valuation.getSumOfRatings() + comment.getRating());
                }
                Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(comment.getAuthorsUID()).child(Constants.DATABASE_REF_VALUATION).setValue(valuation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isWorthyToBeAGroup(int serverNumber) {
        if (commentOwnerIsExtra) {
            if (comment.getGroup() == null) {
                createNewGroup(serverNumber);
            }
        } else {
            Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(comment.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(comment.getServerUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(comment.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(comment.getServerUID()).removeEventListener(this);
                    long numberOfComments = snapshot.getChildrenCount();
                    int numberOfStickers = 0;
                    if (comment.getStickers() != null) {
                        for (Product product : comment.getStickers().values()) {
                            numberOfStickers += product.getQuantity();
                        }
                    }

                    int requiredAverage = requiredAverageToBecomeGroup(numberOfComments);
                    if (comment.getNumberOfRatings() > ((numberOfComments / 2) - numberOfStickers) && comment.getRating() >= requiredAverage && comment.getGroup() == null) {
                        createNewGroup(serverNumber);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private int requiredAverageToBecomeGroup(long numberOfComments) {
        //Used to calculate necessary average to become group based on number of comments -> from 3 to 4.05
        int commentsOnAverageScale = (int) (numberOfComments / 6.25);
        return (int) (3 + (commentsOnAverageScale * 0.07));
    }

    private void createNewGroup(int serverNumber) {
        List<String> userUIDList = new ArrayList<>();
        userUIDList.add(Util.getUser().getUserUID());
        List<User> users = new ArrayList<>();
        users.add(Util.getUser());
        GroupTempInfo groupTempInfo = new GroupTempInfo(users, false);
        Group group = new Group(UUID.randomUUID().toString(), comment.getSubject(), serverNumber, Util.getServer().getTempInfo().getNumber(),
                groupTempInfo, "text", new ArrayList<>(), userUIDList, false, comment.getCommentUID());
        comment.setGroup(group);
        comment.setAGroup(true);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(comment.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(comment.getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST).child(comment.getCommentUID()).child(Constants.DATABASE_REF_A_GROUP).setValue(true);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(comment.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(comment.getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST).child(comment.getCommentUID()).child(Constants.DATABASE_REF_GROUP).setValue(group);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(comment.getAuthorsUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(comment.getAuthorsUID()).removeEventListener(this);
                User user = snapshot.getValue(User.class);
                if (user != null && user.getPreferences() != null && user.getPreferences().isNotification()) {
                    Util.prepareNotification(Constants.CONGRATS_YOUR_COMMENT_IS_A_GROUP, Constants.YOUR_COMMENT_WAS_WELL_RATED, comment.getCommentUID(), "group", comment, getContext());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}