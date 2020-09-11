package com.relyon.whib.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.activity.StoreActivity;
import com.relyon.whib.adapter.RecyclerViewCommentAdapter;
import com.relyon.whib.adapter.RecyclerViewGalleryAdapter;
import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DialogStickers extends Dialog {

    private Activity activity;
    public Dialog dialog;
    private List<Product> productList;
    private List<Argument> argumentList;
    private Comment comment;
    private RecyclerViewCommentAdapter recyclerViewCommentAdapter;
    private Integer position;

    private RecyclerView rvSticker;
    private LinearLayout emptyLayout;
    private Button getStickers;

    public DialogStickers(Activity activity, List<Product> productList, List<Argument> argumentList, Comment comment, RecyclerViewCommentAdapter recyclerViewCommentAdapter, Integer position) {
        super(activity);
        List<Product> stickers = new ArrayList<>();
        for (Product sticker : productList) {
            if (sticker != null && sticker.getQuantity() > 0) {
                stickers.add(sticker);
            }
        }
        this.productList = stickers;
        this.argumentList = argumentList;
        this.comment = comment;
        this.recyclerViewCommentAdapter = recyclerViewCommentAdapter;
        this.position = position;
        this.dialog = this;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_stickers);
        setTransparentBackground();

        setLayoutAttributes();

        if (productList == null || productList.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            getStickers.setOnClickListener(view -> activity.startActivity(new Intent(activity, StoreActivity.class)));
        } else {
            emptyLayout.setVisibility(View.GONE);
        }
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        final RecyclerViewGalleryAdapter[] adapter = new RecyclerViewGalleryAdapter[1];
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        rvSticker.addItemDecoration(dividerItemDecoration);
        rvSticker.setLayoutManager(layoutManager);
        if (comment != null) {
            Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(comment.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(comment.getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST).child(comment.getCommentUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        adapter[0] = new RecyclerViewGalleryAdapter(Util.getUser().getProducts(), productList, getContext(), false, true, false, argumentList, dialog, comment, recyclerViewCommentAdapter, position);
                        rvSticker.setAdapter(adapter[0]);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            adapter[0] = new RecyclerViewGalleryAdapter(Util.getUser().getProducts(), productList, getContext(), false, true, false, argumentList, dialog, null, recyclerViewCommentAdapter, position);
            rvSticker.setAdapter(adapter[0]);
        }
    }

    private void setTransparentBackground() {
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setLayoutAttributes() {
        rvSticker = findViewById(R.id.my_stickers);
        emptyLayout = findViewById(R.id.empty_layout);
        getStickers = findViewById(R.id.get_stickers);
    }
}