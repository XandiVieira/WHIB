package com.relyon.whib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;

public class RecyclerViewArgumentAdapter extends RecyclerView.Adapter<RecyclerViewArgumentAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Argument> elementos;

    RecyclerViewArgumentAdapter(@NonNull Context context, ArrayList<Argument> elementos) {
        this.context = context;
        this.elementos = elementos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.argument_item, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewArgumentAdapter.ViewHolder holder, final int position) {
        holder.text.setText(elementos.get(position).getText());
        holder.userName.setText(elementos.get(position).getAuthorsName());
        holder.time.setText(elementos.get(position).getTime());
        if(Util.getUser().getUserUID().equals(elementos.get(position).getAuthorsUID())){
            holder.argumentLayout.setBackgroundResource(R.color.lightGrey);
        }
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        TextView time;
        TextView userName;
        LinearLayout argumentLayout;

        ViewHolder(View rowView) {
            super(rowView);
            text = rowView.findViewById(R.id.argument);
            time = rowView.findViewById(R.id.time);
            userName = rowView.findViewById(R.id.userName);
            argumentLayout = rowView.findViewById(R.id.argumentLayout);
        }
    }
}
