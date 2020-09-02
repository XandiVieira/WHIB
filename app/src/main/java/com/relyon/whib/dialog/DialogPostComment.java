package com.relyon.whib.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.relyon.whib.R;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Sending;
import com.relyon.whib.util.Util;
import com.relyon.whib.util.Constants;

import java.util.ArrayList;
import java.util.Date;

import me.toptas.fancyshowcase.FancyShowCaseView;

public class DialogPostComment extends Dialog implements
        View.OnClickListener {

    private Activity activity;
    public Dialog dialog;
    private int characterCounter;
    private String subject;
    private static int MAX_COMMENT_SIZE = 600;

    private Button comment;
    private EditText commentBox;
    private TextView counter;
    private ImageView closeIcon;

    public DialogPostComment(Activity a, String subjectObj) {
        super(a);
        this.activity = a;
        this.subject = subjectObj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_post_comment);
        setTransparentBackground();

        setLayoutAttributes();

        closeIcon.setOnClickListener(this);
        comment.setOnClickListener(this);

        commentBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    comment.setEnabled(true);
                    characterCounter = MAX_COMMENT_SIZE - charSequence.length();
                    counter.setText(String.valueOf(characterCounter));
                } else {
                    comment.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setLayoutAttributes() {
        comment = findViewById(R.id.commentButton);
        commentBox = findViewById(R.id.commentBox);
        closeIcon = findViewById(R.id.closeIcon);
        counter = findViewById(R.id.counter);
    }

    private void setTransparentBackground() {
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
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

            DatabaseReference commentListRef = Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child(Constants.DATABASE_REF_SERVERS).child(Util.getServer().getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST);
            String commentUID = commentListRef.push().getKey();
            comment.setCommentUID(commentUID);
            if (commentUID != null) {
                commentListRef.child(commentUID).setValue(comment);
            }

            if (Util.getUser().getCommentList() == null) {
                Util.getUser().setCommentList(new ArrayList<>());
            }
            Util.getUser().getCommentList().add(comment);
            Toast.makeText(getContext(), "Comentário postado!", Toast.LENGTH_SHORT).show();
            commentBox.setText("");
            callTour();
        }
    }

    private boolean validateComment() {
        if (commentBox.getText().length() < 30 && !Util.getUser().isAdmin()) {
            Toast.makeText(getContext(), "O comentário deve possuir pelo menos 30 caracteres!", Toast.LENGTH_SHORT).show();
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
            }).focusBorderSize(10)
                    .focusRectAtPosition(1005, 80, 25, 80)
                    .build()
                    .show();
        }
    }
}