package com.relyon.whib;

import android.app.Activity;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.Sending;
import com.relyon.whib.modelo.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewGalleryAdapter extends RecyclerView.Adapter<RecyclerViewGalleryAdapter.ViewHolder> {

    private List<Product> elements;
    private HashMap<String, Product> myStickers;
    private StorageReference storageReference;
    private Context context;
    private boolean isForDialog;
    private List<Argument> argumentList;
    private Dialog dialog;

    public RecyclerViewGalleryAdapter(List<Product> elements, HashMap<String, Product> myStickers, Context context, Activity activity, boolean isForDialog, List<Argument> argumentList, Dialog dialog) {
        this.elements = elements;
        this.myStickers = myStickers;
        this.context = context;
        this.isForDialog = isForDialog;
        this.argumentList = argumentList;
        this.dialog = dialog;
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
        Product product = elements.get(position);

        if (!isForDialog) {
            holder.title.setText(product.getTitle());
            holder.title.setVisibility(View.VISIBLE);
        } else {
            holder.title.setVisibility(View.GONE);
            holder.image.setOnClickListener(v -> sendSticker(elements.get(position).getItemSKU(), elements.get(position).getProductUID()));
        }

        if (product.isContained(myStickers) != null) {
            storageReference.child("images/" + product.getItemSKU() + ".png").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));
            for (Product mine : myStickers.values()) {
                if (mine.getItemSKU().equals(product.getItemSKU())) {
                    holder.quantity.setText("(" + mine.getQuantity() + ")");
                }
            }
        } else if (!isForDialog) {
            holder.quantity.setText("(0)");
            storageReference.child("images/" + product.getItemSKU() + "_shadow" + ".png").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
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

    private void sendSticker(String sku, String id) {

        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date current_date = cal.getTime();

        Sending sending = new Sending("text", current_date.getTime(), Util.getUser().getUserName(), Util.getUser().getUserUID(), Util.getSubject());
        Argument argument = new Argument(null, sku, Util.getGroup().getGroupUID(), current_date.getTime(), sending);
        Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline")
                .child("commentList").child(Util.getGroup().getCommentUID())
                .child("group").child("argumentList").push().setValue(argument);

        for (Product product : Util.getUser().getProducts().values()) {
            if (product.getProductUID().equals(id)) {
                if (Util.getUser().getProducts() != null) {
                    if (Util.getUser().getProducts().get(product.getProductUID()) != null) {
                        Product product1 = Util.getUser().getProducts().get(product.getProductUID());
                        if (product1 != null) {
                            product1.setQuantity(product1.getQuantity() - 1);
                            Util.getUser().getProducts().get(product1.getProductUID()).setQuantity(product1.getQuantity());
                            Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("products").child(id).child("quantity").setValue(product1.getQuantity());
                        }
                    }
                }
            }
        }

        if (argumentList != null && argumentList.isEmpty() && !Util.getGroup().isReady() && Util.getUser().getUserUID().equals(Util.getComment().getAuthorsUID())) {
            Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline")
                    .child("commentList").child(Util.getGroup().getCommentUID())
                    .child("group").child("ready").setValue(true);
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}