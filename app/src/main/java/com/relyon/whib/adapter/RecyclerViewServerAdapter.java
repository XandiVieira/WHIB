package com.relyon.whib.adapter;

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
import com.relyon.whib.R;
import com.relyon.whib.activity.TimelineActivity;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class RecyclerViewServerAdapter extends RecyclerView.Adapter<RecyclerViewServerAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Server> servers;

    RecyclerViewServerAdapter(@NonNull Context context, ArrayList<Server> servers) {
        this.context = context;
        this.servers = servers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Server server = servers.get(position);
        int numberOfComments = 0;
        if (server.getTimeline() != null && server.getTimeline().getCommentList() != null) {
            numberOfComments = server.getTimeline().getCommentList().size();
        }

        setServerFullness(numberOfComments, holder, position);

        int finalNumberOfComments = numberOfComments;
        holder.full1.setOnClickListener(v -> callServer(position, holder, finalNumberOfComments, server));
        holder.full2.setOnClickListener(v -> callServer(position, holder, finalNumberOfComments, server));
        holder.full3.setOnClickListener(v -> callServer(position, holder, finalNumberOfComments, server));
        holder.full4.setOnClickListener(v -> callServer(position, holder, finalNumberOfComments, server));
        holder.serverNumber.setOnClickListener(v -> callServer(position, holder, finalNumberOfComments, server));
        holder.itemView.setOnClickListener(v -> callServer(position, holder, finalNumberOfComments, server));
    }

    private void setServerFullness(int numberOfComments, ViewHolder holder, int position) {
        if (numberOfComments > 25) {
            holder.full2.setVisibility(View.VISIBLE);
        } else {
            holder.full2.setVisibility(View.GONE);
        }
        holder.serverStatus.setTextColor(Color.rgb(0, 188, 0));

        if (numberOfComments > 50) {
            holder.full3.setVisibility(View.VISIBLE);
            holder.full1.setBackgroundResource(R.drawable.rounded_yellow);
            holder.full2.setBackgroundResource(R.drawable.rounded_yellow);
            holder.full3.setBackgroundResource(R.drawable.rounded_yellow);
            holder.full4.setBackgroundResource(R.drawable.rounded_yellow);
            holder.serverStatus.setTextColor(Color.rgb(230, 230, 0));
        } else {
            holder.full3.setVisibility(View.GONE);
        }

        if (numberOfComments > 75) {
            holder.full4.setVisibility(View.VISIBLE);
            holder.full1.setBackgroundResource(R.drawable.rounded_accent);
            holder.full2.setBackgroundResource(R.drawable.rounded_accent);
            holder.full3.setBackgroundResource(R.drawable.rounded_accent);
            holder.full4.setBackgroundResource(R.drawable.rounded_accent);
            holder.serverStatus.setTextColor(Color.rgb(255, 189, 74));
        } else {
            holder.full4.setVisibility(View.GONE);
        }

        if (numberOfComments == 100) {
            holder.full4.setVisibility(View.VISIBLE);
            holder.full1.setBackgroundResource(R.drawable.rounded_red);
            holder.full2.setBackgroundResource(R.drawable.rounded_red);
            holder.full3.setBackgroundResource(R.drawable.rounded_red);
            holder.full4.setBackgroundResource(R.drawable.rounded_red);
            holder.serverStatus.setTextColor(Color.rgb(188, 0, 0));
            servers.get(position).getTempInfo().setActivated(false);
        } else {
            holder.full4.setVisibility(View.GONE);
        }

        holder.serverNumber.setText("Servidor #" + (servers.get(position).getTempInfo().getNumber() + 1));

        if (numberOfComments < 100) {
            holder.serverStatus.setText("Disponível");
        } else {
            holder.serverStatus.setText("Lotado");
        }
    }

    private void callServer(int position, ViewHolder holder, int numberOfComments, Server server) {
        if (numberOfComments > 95 && numberOfComments < 100) {
            isThereAvailableServers(numberOfComments);
        } else if (numberOfComments >= 100) {
            Toast.makeText(context, context.getString(R.string.full_server), Toast.LENGTH_SHORT).show();
        }
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(server.getServerUID()).child(Constants.DATABASE_REF_TEMP_INFO).setValue(server.getTempInfo());
        goToServer(server, holder, position);
    }

    private void isThereAvailableServers(int numberOfComments) {
        boolean allServersAreFull = true;
        for (int i = 0; i < servers.size(); i++) {
            if (numberOfComments < 95) {
                allServersAreFull = false;
            }
        }
        if (allServersAreFull) {
            createNewServer();
        }
    }

    private void goToServer(Server server, ViewHolder holder, int position) {
        if (server.getTempInfo().isActivated()) {
            goTimelineScreen(holder.serverNumber.getText().toString(),
                    holder.serverStatus.getText().toString(), position);
        } else {
            Toast.makeText(context, R.string.unavailable_server, Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewServer() {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).removeEventListener(this);
                ArrayList<Integer> helperList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subject subject1 = snapshot.getValue(Subject.class);
                    if (subject1 != null && subject1.getServers() != null) {
                        for (Server server : subject1.getServers().values()) {
                            helperList.add(server.getTempInfo().getNumber());
                        }
                    }
                }
                String subject2 = servers.get(0).getSubject();
                Integer[] number = new Integer[helperList.size()];
                helperList.toArray(number);
                Arrays.sort(number);
                ServerTempInfo serverTempInfo2 = new ServerTempInfo(0, true, findFirstMissing(number));
                Timeline tl = new Timeline(null, subject2, null);
                Server server = new Server(UUID.randomUUID().toString(), serverTempInfo2, subject2, tl);
                Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(server.getServerUID()).setValue(server);
                Util.setNumberOfServers(Util.getNumberOfServers() + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goTimelineScreen(String serverNumber, String status, int position) {
        Toast.makeText(context, serverNumber + " - " + status, Toast.LENGTH_SHORT).show();
        Util.setServer(servers.get(position));
        User user = Util.getUser();
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(Util.getUser().getUserUID()).setValue(user);
        Intent intent = new Intent(context, TimelineActivity.class);
        intent.putExtra("subject", servers.get(position).getSubject()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return servers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView serverNumber;
        private TextView serverStatus;
        private LinearLayout full1;
        private LinearLayout full2;
        private LinearLayout full3;
        private LinearLayout full4;

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