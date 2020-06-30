package com.relyon.whib;

import android.app.Activity;
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
import com.relyon.whib.modelo.Product;

import java.util.HashMap;
import java.util.List;

public class RecyclerViewGalleryAdapter extends RecyclerView.Adapter<RecyclerViewGalleryAdapter.ViewHolder> {

    private List<Product> elements;
    private HashMap<String, Product> myStickers;
    private StorageReference storageReference;
    private Context context;
    private Activity activity;

    public RecyclerViewGalleryAdapter(List<Product> elements, HashMap<String, Product> myStickers, Context context, Activity activity) {
        this.elements = elements;
        this.myStickers = myStickers;
        this.context = context;
        this.activity = activity;

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

        holder.title.setText(product.getTitle());

        if (product.isContained(myStickers) != null) {
            storageReference.child("images/" + product.getTitle()).getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));
            for (Product mine : myStickers.values()) {
                if (mine.getTitle().equals(product.getTitle())) {
                    holder.quantity.setText("(" + mine.getQuantity() + ")");
                }
            }
        } else {
            holder.quantity.setText("(0)");
            storageReference.child("images/" + product.getTitle() + "_shadow").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));
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
}