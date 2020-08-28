package com.relyon.whib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.relyon.whib.R;
import com.relyon.whib.modelo.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewServerGroupAdapter extends RecyclerView.Adapter<RecyclerViewServerGroupAdapter.ViewHolder> {

    private HashMap<String, Server> servers;
    private List<String> subjects = new ArrayList<>();

    public RecyclerViewServerGroupAdapter(HashMap<String, Server> servers) {
        this.servers = servers;
        for (Server server : servers.values()) {
            if (!subjects.contains(server.getSubject())) {
                subjects.add(server.getSubject());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server_group, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.subject.setText(subjects.get(position));
        if (holder.subject.getText().toString().equals("")) {
            holder.subject.setText("Indisponível");
        }

        ArrayList<Server> serversOfGroup = new ArrayList<>();

        for (Server server : servers.values()) {
            if (server.getSubject().equals(subjects.get(position))) {
                serversOfGroup.add(server);
            }
        }

        if (serversOfGroup.size() > 1) {
            holder.nextServer.setVisibility(View.VISIBLE);
        } else {
            holder.nextServer.setVisibility(View.GONE);
        }

        holder.initRecyclerView(serversOfGroup);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
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

        private void initRecyclerView(ArrayList<Server> elements) {
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewInner = itemView.findViewById(R.id.recyclerViewInner);
            recyclerViewInner.setLayoutManager(layoutManager2);
            RecyclerViewServerAdapter adapter2 = new RecyclerViewServerAdapter(itemView.getContext(), elements);
            recyclerViewInner.setAdapter(adapter2);
        }
    }
}