package com.relyon.whib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;

public class RecyclerViewArgumentAdapter extends RecyclerView.Adapter<RecyclerViewArgumentAdapter.ViewHolder> {

    private final ArrayList<Argument> elementos;
    private View rowView;
    private ViewGroup parent;
    private Context context;

    RecyclerViewArgumentAdapter(Context context, ArrayList<Argument> elementos) {
        this.elementos = elementos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.argument_sent_item, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewArgumentAdapter.ViewHolder holder, final int position) {
        holder.text.setText(elementos.get(position).getText());
        if (Util.getUser().getUserUID().equals(elementos.get(position).getAuthorsUID())) {
            rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.argument_sent_item, parent, false);
        } else {
            rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.argument_received_item, parent, false);
            holder.userName.setText(elementos.get(position).getAuthorsName());
        }
        holder.time.setText(elementos.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private TextView time;
        private TextView userName;

        ViewHolder(View rowView) {
            super(rowView);
            text = rowView.findViewById(R.id.argument);
            time = rowView.findViewById(R.id.time);
            userName = rowView.findViewById(R.id.userName);
        }
    }
}