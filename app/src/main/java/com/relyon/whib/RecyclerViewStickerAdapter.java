package com.relyon.whib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.List;

public class RecyclerViewStickerAdapter extends RecyclerView.Adapter<RecyclerViewStickerAdapter.ViewHolder> {

    private List<Product> ownStickers;
    private StorageReference storageReference;
    private Context context;
    private Comment comment;

    public RecyclerViewStickerAdapter(List<Product> ownStickers, Context context, Comment comment) {
        this.context = context;
        this.ownStickers = ownStickers;
        storageReference = FirebaseStorage.getInstance().getReference();
        this.comment = comment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker_for_comment, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewStickerAdapter.ViewHolder holder, int position) {
        Product product = ownStickers.get(position);
        storageReference.child("images/" + product.getItemSKU() + ".png").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));
        holder.image.setMaxHeight(50);
        holder.image.setMaxWidth(50);
        holder.quantity.setText("(" + product.getQuantity() + ")");

        if (comment != null && comment.isAGroup()){
            Util.mUserDatabaseRef.child(comment.getAuthorsUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.isExtra()){
                        holder.bg.setBackground(context.getResources().getDrawable(R.drawable.rounded_accent));
                    }else {
                        holder.bg.setBackground(context.getResources().getDrawable(R.drawable.rounded_primary));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ownStickers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView quantity;
        private LinearLayout bg;

        ViewHolder(View rowView) {
            super(rowView);
            image = rowView.findViewById(R.id.image);
            quantity = rowView.findViewById(R.id.quantity);
            bg = rowView.findViewById(R.id.background);
        }
    }
}