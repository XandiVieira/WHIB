package com.relyon.whib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.relyon.whib.modelo.Server;

import java.util.ArrayList;

public class RecyclerViewServerGroupAdapter extends RecyclerView.Adapter<RecyclerViewServerGroupAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<ArrayList> elementos;
    private ArrayList<String> subjects;

    public RecyclerViewServerGroupAdapter(@NonNull Context context, ArrayList<ArrayList> elementos, ArrayList<String> subjects) {
        this.context = context;
        this.elementos = elementos;
        this.subjects = subjects;
    }

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
        holder.initRecyclerView(elementos.get(position));
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
        return elementos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView subject;
        RecyclerView recyclerViewInner;

        ViewHolder(View rowView) {
            super(rowView);

            subject = rowView.findViewById(R.id.subject);
            recyclerViewInner = rowView.findViewById(R.id.recyclerViewInner);
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
