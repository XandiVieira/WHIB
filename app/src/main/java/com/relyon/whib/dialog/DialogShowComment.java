package com.relyon.whib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.relyon.whib.R;
import com.relyon.whib.adapter.RecyclerViewCommentAdapter;
import com.relyon.whib.modelo.Comment;

public class DialogShowComment extends Dialog {

    private AppCompatActivity activity;
    private Context context;
    private Comment comment;

    private RecyclerView rvComment;
    private LinearLayout background;

    public DialogShowComment(AppCompatActivity activity, Context context, Comment comment) {
        super(activity);
        this.activity = activity;
        this.context = context;
        this.comment = comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_show_comment);

        setLayoutAttributes();

        setAdapterToShowComment();

        background.setOnClickListener(v -> dismiss());
    }

    private void setLayoutAttributes() {
        rvComment = findViewById(R.id.rvComment);
        background = findViewById(R.id.bg);
    }

    private void setAdapterToShowComment() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        rvComment.setLayoutManager(layoutManager);
        RecyclerViewCommentAdapter adapter = new RecyclerViewCommentAdapter(context, activity, false, true);
        rvComment.setAdapter(adapter);
        adapter.addComment(comment);
    }
}