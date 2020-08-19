package com.relyon.whib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class RecyclerViewServerAdapter extends RecyclerView.Adapter<RecyclerViewServerAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Server> elements;

    RecyclerViewServerAdapter(@NonNull Context context, ArrayList<Server> elements) {
        this.context = context;
        this.elements = elements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (elements.get(position).getTempInfo().getQtdUsers() > 25) {
            holder.full2.setVisibility(View.VISIBLE);
        } else {
            holder.full2.setVisibility(View.GONE);
        }
        holder.serverStatus.setTextColor(Color.rgb(0, 188, 0));

        if (elements.get(position).getTempInfo().getQtdUsers() > 50) {
            holder.full3.setVisibility(View.VISIBLE);
            holder.full1.setBackgroundResource(R.drawable.rounded_yellow);
            holder.full2.setBackgroundResource(R.drawable.rounded_yellow);
            holder.full3.setBackgroundResource(R.drawable.rounded_yellow);
            holder.full4.setBackgroundResource(R.drawable.rounded_yellow);
            holder.serverStatus.setTextColor(Color.rgb(230, 230, 0));
        } else {
            holder.full3.setVisibility(View.GONE);
        }

        if (elements.get(position).getTempInfo().getQtdUsers() > 75) {
            holder.full4.setVisibility(View.VISIBLE);
            holder.full1.setBackgroundResource(R.drawable.rounded_accent);
            holder.full2.setBackgroundResource(R.drawable.rounded_accent);
            holder.full3.setBackgroundResource(R.drawable.rounded_accent);
            holder.full4.setBackgroundResource(R.drawable.rounded_accent);
            holder.serverStatus.setTextColor(Color.rgb(255, 189, 74));
        } else {
            holder.full4.setVisibility(View.GONE);
        }

        if (elements.get(position).getTempInfo().getQtdUsers() == 100) {
            holder.full4.setVisibility(View.VISIBLE);
            holder.full1.setBackgroundResource(R.drawable.rounded_red);
            holder.full2.setBackgroundResource(R.drawable.rounded_red);
            holder.full3.setBackgroundResource(R.drawable.rounded_red);
            holder.full4.setBackgroundResource(R.drawable.rounded_red);
            holder.serverStatus.setTextColor(Color.rgb(188, 0, 0));
            elements.get(position).getTempInfo().setActivated(false);
        } else {
            holder.full4.setVisibility(View.GONE);
        }

        holder.serverNumber.setText("Servidor #" + (elements.get(position).getTempInfo().getNumber() + 1));

        if (elements.get(position).getTempInfo().getQtdUsers() < 100) {
            holder.serverStatus.setText("Disponível");
        } else {
            holder.serverStatus.setText("Lotado");
        }

        holder.full1.setOnClickListener(v -> beforeGroup(position, holder));
        holder.full2.setOnClickListener(v -> beforeGroup(position, holder));
        holder.full3.setOnClickListener(v -> beforeGroup(position, holder));
        holder.full4.setOnClickListener(v -> beforeGroup(position, holder));

        holder.serverNumber.setOnClickListener(v -> beforeGroup(position, holder));

        holder.itemView.setOnClickListener(v -> beforeGroup(position, holder));
    }

    private void beforeGroup(int position, ViewHolder holder) {
        Server server = elements.get(position);
        if (server.getTempInfo().getQtdUsers() < 95) {
            goToServer(server, holder, position);
        } else if (server.getTempInfo().getQtdUsers() >= 95 && server.getTempInfo().getQtdUsers() < 100) {
            boolean allFull = true;
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).getTempInfo().getQtdUsers() < 95) {
                    allFull = false;
                }
            }
            if (allFull) {
                createNewServer();
            }
            goToServer(server, holder, position);
        } else if (server.getTempInfo().getQtdUsers() >= 100) {
            Toast.makeText(context, "Servidor Lotado!", Toast.LENGTH_SHORT).show();
        }
        Util.mSubjectDatabaseRef.child(server.getSubject()).child("servers").child(server.getServerUID()).child("tempInfo").setValue(server.getTempInfo());
    }

    private void goToServer(Server server, ViewHolder holder, int position) {
        if (server.getTempInfo().isActivated()) {
            goTimelineScreen(holder.serverNumber.getText().toString(),
                    holder.serverStatus.getText().toString(), position);
        } else {
            Toast.makeText(context, "Servidor indisponível!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewServer() {
        Util.mSubjectDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Util.mSubjectDatabaseRef.removeEventListener(this);
                ArrayList<Integer> helperList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subject subject1 = snapshot.getValue(Subject.class);
                    if (subject1 != null && subject1.getServers() != null) {
                        for (Server server : subject1.getServers().values()) {
                            helperList.add(server.getTempInfo().getNumber());
                        }
                    }
                }
                String subject2 = elements.get(0).getSubject();
                Integer[] number = new Integer[helperList.size()];
                helperList.toArray(number);
                Arrays.sort(number);
                ServerTempInfo serverTempInfo2 = new ServerTempInfo(0, true, findFirstMissing(number));
                Timeline tl = new Timeline(null, subject2, null);
                Server server = new Server(UUID.randomUUID().toString(), serverTempInfo2, subject2, tl);
                Util.mSubjectDatabaseRef.child(server.getSubject()).child("servers").child(server.getServerUID()).setValue(server);
                Util.setNumberOfServers(Util.getNumberOfServers() + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goTimelineScreen(String serverNumber, String status, int position) {
        Toast.makeText(context, serverNumber + " - " + status, Toast.LENGTH_SHORT).show();
        Util.setServer(elements.get(position));
        User user = Util.getUser();
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).setValue(user);
        Intent intent = new Intent(context, TimelineActivity.class);
        intent.putExtra("subject", elements.get(position).getSubject()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView serverNumber;
        TextView serverStatus;
        LinearLayout full1;
        LinearLayout full2;
        LinearLayout full3;
        LinearLayout full4;

        ViewHolder(View rowView) {
            super(rowView);
            this.serverNumber = rowView.findViewById(R.id.serverNumber);
            this.serverStatus = rowView.findViewById(R.id.serverStatus);
            this.full1 = rowView.findViewById(R.id.full1);
            this.full2 = rowView.findViewById(R.id.full2);
            this.full3 = rowView.findViewById(R.id.full3);
            this.full4 = rowView.findViewById(R.id.full4);
        }
    }

    public int findFirstMissing(Integer[] numbers) {
        for (int i = 0; i < numbers.length; i++) {
            int target = numbers[i];
            while (target < numbers.length && target != numbers[target]) {
                int new_target = numbers[target];
                numbers[target] = target;
                target = new_target;
            }
        }

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] != i) {
                return i;
            }
        }
        return numbers.length;
    }
}