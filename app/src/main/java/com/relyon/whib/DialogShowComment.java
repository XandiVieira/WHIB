package com.relyon.whib;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.relyon.whib.modelo.Comment;

import java.util.ArrayList;
import java.util.List;

public class DialogShowComment extends Dialog {

    private RecyclerView rvComment;
    private LinearLayout bg;
    private AppCompatActivity a;
    private Context context;
    private List<Comment> comment = new ArrayList<>();

    public DialogShowComment(AppCompatActivity a, Context context, Comment comment) {
        super(a);
        this.a = a;
        this.context = context;
        this.comment.add(comment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_show_comment);
        rvComment = findViewById(R.id.rvComment);
        bg = findViewById(R.id.bg);

        LinearLayoutManager layoutManager = new LinearLayoutManager(a, LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        rvComment.setLayoutManager(layoutManager);
        RecyclerViewCommentAdapter adapter = new RecyclerViewCommentAdapter(context, a, false, true);
        rvComment.setAdapter(adapter);
        adapter.addAll(comment, true, false, true);

        bg.setOnClickListener(v -> dismiss());
    }
}