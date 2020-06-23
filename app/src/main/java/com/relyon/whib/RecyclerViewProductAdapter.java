package com.relyon.whib;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.relyon.whib.modelo.Product;

import java.util.List;

public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.ViewHolder> {

    private List<Product> elements;
    private Activity activity;

    public RecyclerViewProductAdapter(List<Product> elements, Activity activity) {
        this.elements = elements;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewProductAdapter.ViewHolder holder, int position) {
        Product product = elements.get(position);

        //holder.image.setImageDrawable(product.getImagePath());
        holder.title.setText(product.getTitle());
        holder.description.setText(product.getDescription());
        holder.price.setText("R$ " + product.getPrice());

        holder.price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title;
        private TextView description;
        private Button price;

        ViewHolder(View rowView) {
            super(rowView);
            image = rowView.findViewById(R.id.image);
            title = rowView.findViewById(R.id.title);
            description = rowView.findViewById(R.id.description);
            price = rowView.findViewById(R.id.price);
        }
    }
}