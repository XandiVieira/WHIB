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
import com.relyon.whib.modelo.Subject;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewSubjectAdapter extends RecyclerView.Adapter<RecyclerViewSubjectAdapter.ViewHolder> {

    private List<Subject> subjects;

    public RecyclerViewSubjectAdapter(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Subject subject = subjects.get(position);

        holder.subject.setText(subject.getTitle());

        if (subject.getServers().size() > 1) {
            holder.nextServer.setVisibility(View.VISIBLE);
        } else {
            holder.nextServer.setVisibility(View.GONE);
        }

        holder.initServersRecyclerView(new ArrayList<>(subject.getServers().values()));
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView subject;
        private RecyclerView rvServer;
        private ImageView nextServer;

        ViewHolder(View rowView) {
            super(rowView);
            subject = rowView.findViewById(R.id.subject);
            rvServer = rowView.findViewById(R.id.server_recycler_view);
            nextServer = rowView.findViewById(R.id.next_server);
        }

        private void initServersRecyclerView(ArrayList<Server> elements) {
            LinearLayoutManager layoutManagerForServerAdapter = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvServer = itemView.findViewById(R.id.server_recycler_view);
            rvServer.setLayoutManager(layoutManagerForServerAdapter);
            RecyclerViewServerAdapter recyclerViewServerAdapter = new RecyclerViewServerAdapter(itemView.getContext(), elements);
            rvServer.setAdapter(recyclerViewServerAdapter);
        }
    }
}