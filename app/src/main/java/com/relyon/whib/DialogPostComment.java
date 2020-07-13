package com.relyon.whib;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Sending;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.Date;

import me.toptas.fancyshowcase.FancyShowCaseView;

public class DialogPostComment extends Dialog implements
        View.OnClickListener {

    private Activity activity;
    public Dialog d;
    private Button comment;
    private EditText commentBox;
    private TextView counter;
    private int charactCounter;
    private String subject;
    private static int MAX_COMMENT_SIZE = 600;
    private ImageView menu;

    DialogPostComment(Activity a, String subjectObj, ImageView menu) {
        super(a);
        this.activity = a;
        this.subject = subjectObj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_post_comment);
        comment = findViewById(R.id.commentButton);
        commentBox = findViewById(R.id.commentBox);
        ImageView closeIcon = findViewById(R.id.closeIcon);
        counter = findViewById(R.id.counter);
        closeIcon.setOnClickListener(this);
        comment.setOnClickListener(this);

        // Enable Send button when there's text to send
        commentBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    comment.setEnabled(true);
                    charactCounter = MAX_COMMENT_SIZE - charSequence.length();
                    counter.setText(String.valueOf(charactCounter));
                } else {
                    comment.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commentButton:
                postComment();
                break;
            case R.id.closeIcon:
                activity.closeContextMenu();
                callTour();
                break;
            default:
                callTour();
                break;
        }
        dismiss();
    }

    private void postComment() {
        long date = new Date().getTime();
        Sending sending = new Sending("text", date, Util.getUser().getUserName(), Util.getUser().getUserUID(), subject);
        if (validateComment()) {
            Comment comment = new Comment(Util.getServer().getServerUID(), commentBox.getText().toString(), (float) 0.0, Util.getUser().getPhotoPath(), date, 0, (float) 0.0, sending, false, null);
            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline").child("commentList").push().setValue(comment);
            if (Util.getUser().getCommentList() == null) {
                Util.getUser().setCommentList(new ArrayList<>());
            }
            Util.getUser().getCommentList().add(new Comment(comment.getCommentUID(), comment.getServerUID(), Util.getServer().getSubject(), comment.getText(), comment.getRating(), comment.getUserPhotoURL(), comment.getTime(), comment.getNumberOfRatings(), comment.getSumOfRatings(), comment.getStickers()));
            Toast.makeText(getContext(), "Comentário postado!", Toast.LENGTH_SHORT).show();
            // Clear input box
            commentBox.setText("");
            callTour();
        }
    }

    private boolean validateComment() {
        if (commentBox.getText().length() < 50 && !Util.getUser().isAdmin()) {
            Toast.makeText(getContext(), "O comentário deve possuir pelo menos 50 caracteres!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (commentBox.getText().length() > MAX_COMMENT_SIZE) {
            Toast.makeText(getContext(), "O comentário deve possuir no máximo " + MAX_COMMENT_SIZE + " caracteres!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void callTour() {
        if (Util.getUser().isFirstTime()) {
            new FancyShowCaseView.Builder(activity).customView(R.layout.custom_tour_timeline_menu, view -> {
                //view.findViewById(R.id.skipTutorial).setOnClickListener(v -> Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("firstTime").setValue(false));
            }).focusBorderSize(10)
                    .focusRectAtPosition(1005, 80, 25, 80)
                    .build()
                    .show();
        }
    }
}