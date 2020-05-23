package com.relyon.whib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.relyon.whib.modelo.Server;

import java.util.ArrayList;

public class RecyclerViewServerGroupAdapter extends RecyclerView.Adapter<RecyclerViewServerGroupAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<ArrayList> elements;
    private ArrayList<String> subjects;
    private RecyclerView recyclerView;

    RecyclerViewServerGroupAdapter(@NonNull Context context, ArrayList<ArrayList> elements, ArrayList<String> subjects, RecyclerView recyclerViewServers) {
        this.context = context;
        this.elements = elements;
        this.subjects = subjects;
        this.recyclerView = recyclerViewServers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_group_item, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.subject.setText(subjects.get(position));
        if (holder.subject.getText().toString().equals("")) {
            holder.subject.setText("Indisponível");
        }

        if (elements.size() > 2) {
            holder.nextServer.setVisibility(View.VISIBLE);
        } else {
            holder.nextServer.setVisibility(View.GONE);
        }

        holder.initRecyclerView(elements.get(position));
        /*if (elementos.length > 0 && elementos[position].size() > 0 && !elementos[position].get(0).getSubject().getTitle().equals("")) {
            holder.initRecyclerView(elementos[position]);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }*/
    }

    private void goTimelineScreen(String subject, String status) {
        Toast.makeText(context, subject + " - " + status, Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent();
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView subject;
        RecyclerView recyclerViewInner;
        ImageView nextServer;

        ViewHolder(View rowView) {
            super(rowView);
            subject = rowView.findViewById(R.id.subject);
            recyclerViewInner = rowView.findViewById(R.id.recyclerViewInner);
            nextServer = rowView.findViewById(R.id.nextServer);
        }

        private void initRecyclerView(ArrayList<Server> elementos) {
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewInner = itemView.findViewById(R.id.recyclerViewInner);
            recyclerViewInner.setLayoutManager(layoutManager2);
            RecyclerViewServerAdapter adapter2 = new RecyclerViewServerAdapter(itemView.getContext(), elementos);
            recyclerViewInner.setAdapter(adapter2);
        }
    }
}