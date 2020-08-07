package com.relyon.whib;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.Util;

import java.util.List;

public class DialogStickers extends Dialog {

    private Activity a;
    public Dialog d;
    private List<Product> productList;
    private List<Argument> argumentList;
    private boolean isForChat;
    private Comment comment;
    private RecyclerViewCommentAdapter recyclerViewCommentAdapter;
    private int position;

    DialogStickers(Activity a, List<Product> productList, List<Argument> argumentList, boolean isForChat, Comment comment, RecyclerViewCommentAdapter recyclerViewCommentAdapter, Integer position) {
        super(a);
        this.a = a;
        this.productList = productList;
        this.argumentList = argumentList;
        this.isForChat = isForChat;
        this.comment = comment;
        this.recyclerViewCommentAdapter = recyclerViewCommentAdapter;
        this.position = position;
        this.d = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_stickers);

        RecyclerView sticker = findViewById(R.id.myStickers);
        TextView empty = findViewById(R.id.empty);

        if (productList == null || productList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        Util.mSubjectDatabaseRef.child(comment.getSubject()).child("servers").child(comment.getServerUID()).child("timeline").child("commentList").child(comment.getCommentUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Comment comment = snapshot.getValue(Comment.class);
                if (comment != null) {
                    comment.setCommentUID(snapshot.getKey());
                    RecyclerViewGalleryAdapter adapter = new RecyclerViewGalleryAdapter(Util.getUser().getProducts(), productList, getContext(), false, true, false, argumentList, d, comment, recyclerViewCommentAdapter, position, a);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                            layoutManager.getOrientation());
                    sticker.addItemDecoration(dividerItemDecoration);
                    sticker.setLayoutManager(layoutManager);
                    sticker.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}