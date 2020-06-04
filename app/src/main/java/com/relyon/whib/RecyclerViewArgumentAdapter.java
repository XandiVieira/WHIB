package com.relyon.whib;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.relyon.whib.modelo.Argument;
import com.relyon.whib.modelo.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RecyclerViewArgumentAdapter extends RecyclerView.Adapter<RecyclerViewArgumentAdapter.ViewHolder> {

    private final ArrayList<Argument> elements;
    private Context context;
    SimpleDateFormat dateFormat_date = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
    SimpleDateFormat dateFormat_time = new SimpleDateFormat("HH:mm:ss");

    RecyclerViewArgumentAdapter(Context context, ArrayList<Argument> elements) {
        this.context = context;
        this.elements = elements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.argument_item, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewArgumentAdapter.ViewHolder holder, final int position) {
        Argument argument = elements.get(position);
        if (Util.getUser().getUserUID().equals(argument.getAuthorsUID())) {
            holder.userName.setVisibility(View.GONE);
            holder.argumentLayout.setBackground(context.getResources().getDrawable(R.drawable.square_primary_dark));
            holder.layout.setGravity(Gravity.END);
        } else {
            holder.userName.setVisibility(View.VISIBLE);
            holder.userName.setText(argument.getAuthorsName());
            holder.argumentLayout.setBackground(context.getResources().getDrawable(R.drawable.square_white));
            holder.layout.setGravity(Gravity.START);
            holder.argument.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.time.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
        holder.argument.setText(argument.getText());
        holder.time.setText(dateFormat_time.format(argument.getTime()));
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout;
        private LinearLayout argumentLayout;
        private TextView argument;
        private TextView time;
        private TextView userName;

        ViewHolder(View rowView) {
            super(rowView);
            layout = rowView.findViewById(R.id.layout);
            argumentLayout = rowView.findViewById(R.id.argumentLayout);
            time = rowView.findViewById(R.id.time);
            userName = rowView.findViewById(R.id.userName);
            argument = rowView.findViewById(R.id.argument);
        }
    }
}