package com.relyon.whib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;

public class RecyclerViewArgumentAdapter extends RecyclerView.Adapter<RecyclerViewArgumentAdapter.ViewHolder> {

    private final ArrayList<Argument> elementos;
    private Context context;

    RecyclerViewArgumentAdapter(Context context, ArrayList<Argument> elementos) {
        this.context = context;
        this.elementos = elementos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.argument_sent_item, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewArgumentAdapter.ViewHolder holder, final int position) {
        if (Util.getUser().getUserUID().equals(elementos.get(position).getAuthorsUID())) {
            holder.userName.setVisibility(View.GONE);
            holder.argumentLayout.setBackground(context.getResources().getDrawable(R.drawable.square_primary_dark));
            holder.layout.setGravity(Gravity.END);
        } else {
            holder.userName.setVisibility(View.VISIBLE);
            holder.userName.setText(elementos.get(position).getAuthorsName());
            holder.argumentLayout.setBackground(context.getResources().getDrawable(R.drawable.square_white));
            holder.layout.setGravity(Gravity.START);
            holder.text.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.time.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
        holder.text.setText(elementos.get(position).getText());
        holder.time.setText(elementos.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout;
        private LinearLayout argumentLayout;
        private TextView text;
        private TextView time;
        private TextView userName;

        ViewHolder(View rowView) {
            super(rowView);
            layout = rowView.findViewById(R.id.layout);
            argumentLayout = rowView.findViewById(R.id.argumentLayout);
            text = rowView.findViewById(R.id.argument);
            time = rowView.findViewById(R.id.time);
            userName = rowView.findViewById(R.id.userName);
        }
    }
}