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
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Util;

import java.util.Date;

public class DialogPostComment extends Dialog implements
        View.OnClickListener {

    private Activity c;
    public Dialog d;
    private Button comment;
    private EditText commentBox;
    private TextView counter;
    private int charactCounter;
    private Subject subject;
    private static int MAX_COMMENT_SIZE = 600;

    DialogPostComment(Activity a, Subject subjectObj) {
        super(a);
        this.c = a;
        this.subject = subjectObj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.post_comment_dialog);
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
                c.closeContextMenu();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void postComment() {
        long date = new Date().getTime();
        Sending sending = new Sending("text", date, Util.getUser().getUserName(), Util.getUser().getUserUID(), subject);
        if (validateComment()) {
            Comment comment = new Comment(commentBox.getText().toString(), (float) 0.0, Util.getUser().getPhotoPath(), date, 0, (float) 0.0, sending, false, null);
            Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").push().setValue(comment);
            Toast.makeText(getContext(), "Comentário postado!", Toast.LENGTH_SHORT).show();
            // Clear input box
            commentBox.setText("");
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
}