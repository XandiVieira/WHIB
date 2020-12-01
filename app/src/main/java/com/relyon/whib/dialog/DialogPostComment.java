package com.relyon.whib.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.relyon.whib.R;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Sending;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.toptas.fancyshowcase.FancyShowCaseView;

public class DialogPostComment extends Dialog implements
        View.OnClickListener {

    private Activity activity;
    private int characterCounter;
    private String subject;

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
                    characterCounter = Constants.MAX_COMMENT_SIZE - charSequence.length();
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
        if (getWindow() != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

            DatabaseReference commentListRef = Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(Util.getServer().getSubject()).child(Constants.DATABASE_REF_SERVERS).child(Util.getServer().getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST);
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
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + commentUID);
            callTour();
        }
    }

    private boolean validateComment() {
        if (commentBox.getText().length() < Constants.MIN_COMMENT_SIZE && !Util.getUser().isAdmin()) {
            Toast.makeText(getContext(), "O comentário deve possuir pelo menos" + Constants.MIN_COMMENT_SIZE + "caracteres!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (commentBox.getText().length() > Constants.MAX_COMMENT_SIZE) {
            Toast.makeText(getContext(), "O comentário deve possuir no máximo " + Constants.MAX_COMMENT_SIZE + " caracteres!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void preNotif(String subject) {
        String topic = "/topics/new_subject"; //topic has to match what the receiver subscribed to

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();

        try {
            notifcationBody.put("title", "Temos um novo assunto. Venha conferir!");
            notifcationBody.put("message", subject);   //Enter your notification message
            notification.put("to", topic);
            notification.put("data", notifcationBody);
            Log.e("TAG", "try");
        } catch (JSONException e) {
            Log.e("TAG", "onCreate: " + e.getMessage());
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        Log.e("TAG", "sendNotification");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.FCM_API, notification, response -> Log.i("TAG", "onResponse: $response"), error -> {
            Toast.makeText(getContext(), "Request error", Toast.LENGTH_LONG).show();
            Log.i("TAG", "onErrorResponse: Didn't work");
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", Constants.serverKey);
                params.put("Content-Type", Constants.contentType);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
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