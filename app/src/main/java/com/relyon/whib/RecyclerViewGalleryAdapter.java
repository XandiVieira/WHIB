package com.relyon.whib;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.Sending;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewGalleryAdapter extends RecyclerView.Adapter<RecyclerViewGalleryAdapter.ViewHolder> {

    private List<Product> allStickers;
    private HashMap<String, Product> myStickers;
    private StorageReference storageReference;
    private Context context;
    private List<Argument> argumentList;
    private Dialog dialog;
    private boolean isForDialog;
    private boolean isForGallery;
    private Comment comment;
    private RecyclerViewCommentAdapter recyclerViewCommentAdapter;

    public RecyclerViewGalleryAdapter(List<Product> allStickers, HashMap<String, Product> myStickers, Context context, boolean isForGallery, boolean isForDialog, List<Argument> argumentList, Dialog dialog, Comment comment) {
        this.allStickers = allStickers;
        this.myStickers = myStickers;
        this.context = context;
        this.isForDialog = isForDialog;
        this.argumentList = argumentList;
        this.dialog = dialog;
        this.isForGallery = isForGallery;
        this.comment = comment;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public RecyclerViewGalleryAdapter(HashMap<String, Product> myStickers, List<Product> allStickers, Context context, boolean isForGallery, boolean isForDialog, boolean isForComment, List<Argument> argumentList, Dialog dialog, Comment comment, RecyclerViewCommentAdapter recyclerViewCommentAdapter) {
        this.allStickers = allStickers;
        this.myStickers = myStickers;
        this.context = context;
        this.isForDialog = isForDialog;
        this.argumentList = argumentList;
        this.dialog = dialog;
        this.isForGallery = isForGallery;
        this.comment = comment;
        this.recyclerViewCommentAdapter = recyclerViewCommentAdapter;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewGalleryAdapter.ViewHolder holder, int position) {

        if (isForGallery) {
            Product product = allStickers.get(position);
            holder.title.setText(product.getTitle());
            if (product.isContained(myStickers) != null) {
                storageReference.child("images/" + product.getItemSKU() + ".png").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));
                for (Product mine : myStickers.values()) {
                    if (mine.getItemSKU().equals(product.getItemSKU())) {
                        holder.quantity.setText("(" + mine.getQuantity() + ")");
                    }
                }
            } else {
                holder.quantity.setText("(0)");
                storageReference.child("images/" + product.getItemSKU() + "_shadow" + ".png").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));
            }
        } else {
            if (myStickers != null && myStickers.size() > 0) {
                List<Product> products = new ArrayList<>(myStickers.values());
                Product product = products.get(position);
                if (isForDialog && product.getQuantity() > 0) {
                    storageReference.child("images/" + product.getItemSKU() + ".png").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));
                    holder.quantity.setText("(" + product.getQuantity() + ")");
                }
                holder.image.setOnClickListener(v -> sendSticker(allStickers.get(position).getItemSKU(), products.get(position).getProductUID(), position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return allStickers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title;
        private TextView quantity;

        ViewHolder(View rowView) {
            super(rowView);
            image = rowView.findViewById(R.id.image);
            title = rowView.findViewById(R.id.title);
            quantity = rowView.findViewById(R.id.quantity);
        }
    }

    private void sendSticker(String sku, String id, int position) {

        if (isForGallery) {
            return;
        }

        if (isForDialog && comment == null) {
            Date data = new Date();

            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            Date current_date = cal.getTime();

            Sending sending = new Sending("text", current_date.getTime(), Util.getUser().getUserName(), Util.getUser().getUserUID(), Util.getSubject());
            Argument argument = new Argument(null, sku, Util.getGroup().getGroupUID(), current_date.getTime(), sending);
            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline")
                    .child("commentList").child(Util.getGroup().getCommentUID())
                    .child("group").child("argumentList").push().setValue(argument);
            Util.getUser().getProducts().get(id).setQuantity(Util.getUser().getProducts().get(id).getQuantity() - 1);
            Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("products").child(id).child("quantity").setValue(Util.getUser().getProducts().get(id).getQuantity());
        }

        if (Util.getUser().getProducts().get(id) != null) {
            Product userProduct = Util.getUser().getProducts().get(id);
            if (isForDialog && comment != null) {
                Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline").child("commentList").child(comment.getCommentUID()).child("stickers").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline").child("commentList").child(comment.getCommentUID()).child("stickers").child(id).removeEventListener(this);
                        Product commentProduct = dataSnapshot.getValue(Product.class);
                        if (comment.getStickers() == null) {
                            comment.setStickers(new HashMap<>());
                        }
                        int quantity = Util.getUser().getProducts().get(userProduct.getProductUID()).getQuantity() - 1;
                        if (commentProduct != null) {
                            if (comment.getStickers().get(id) != null) {
                                comment.getStickers().get(id).setQuantity(comment.getStickers().get(id).getQuantity() + 1);
                            } else {
                                commentProduct.setQuantity(1);
                                comment.getStickers().put(id, commentProduct);
                            }
                            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline").child("commentList").child(comment.getCommentUID()).child("stickers").child(id).setValue(comment.getStickers().get(id));
                        } else {
                            userProduct.setQuantity(1);
                            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline").child("commentList").child(comment.getCommentUID()).child("stickers").child(id).setValue(userProduct);
                        }
                        Util.getUser().getProducts().get(userProduct.getProductUID()).setQuantity(quantity);
                        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("products").child(userProduct.getProductUID()).child("quantity").setValue(quantity);
                        if (recyclerViewCommentAdapter != null) {
                            recyclerViewCommentAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        if (argumentList != null && comment == null && argumentList.isEmpty() && !Util.getGroup().isReady() && Util.getUser().getUserUID().equals(Util.getComment().getAuthorsUID())) {
            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("timeline")
                    .child("commentList").child(Util.getGroup().getCommentUID())
                    .child("group").child("ready").setValue(true);
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}