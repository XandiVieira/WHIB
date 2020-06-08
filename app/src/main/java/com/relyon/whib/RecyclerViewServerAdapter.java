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

import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
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

        holder.serverNumber.setText("Servidor #" + elements.get(position).getTempInfo().getNumber());

        if (elements.get(position).getTempInfo().getQtdUsers() < 100) {
            holder.serverStatus.setText("Disponível");
        } else {
            holder.serverStatus.setText("Lotado");
        }

        holder.itemView.setOnClickListener(v -> {
            Server server = elements.get(position);
            if (server.getTempInfo().getQtdUsers() < 95) {
                goToServer(server, holder, position);
            } else if (server.getTempInfo().getQtdUsers() >= 95 && server.getTempInfo().getQtdUsers() < 100) {
                boolean todosLotados = true;
                for (int i = 0; i < elements.size(); i++) {
                    if (elements.get(i).getTempInfo().getQtdUsers() < 95) {
                        todosLotados = false;
                    }
                }
                if (todosLotados) {
                    createNewServer();
                }
                goToServer(server, holder, position);
            } else if (server.getTempInfo().getQtdUsers() >= 100) {
                Toast.makeText(context, "Servidor Lotado!", Toast.LENGTH_SHORT).show();
            }
            Util.getmServerDatabaseRef().child(server.getServerUID()).child("tempInfo").setValue(server.getTempInfo());
        });
    }

    private void goToServer(Server server, ViewHolder holder, int position) {
        if (server.getTempInfo().isActivated()) {
            server.getTempInfo().setQtdUsers(server.getTempInfo().getQtdUsers() + 1);
            goTimelineScreen(holder.serverNumber.getText().toString(),
                    holder.serverStatus.getText().toString(), position);
        } else {
            Toast.makeText(context, "Servidor indisponível!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewServer() {
        Subject subject2 = elements.get(0).getSubject();
        ServerTempInfo serverTempInfo2 = new ServerTempInfo(0, true, Util.getNumberOfServers() + 1);
        Timeline tl = new Timeline(null, subject2, null);
        Server server = new Server(UUID.randomUUID().toString(), serverTempInfo2, subject2, tl);
        Util.mServerDatabaseRef.child(server.getServerUID()).setValue(server);
        Util.setNumberOfServers(Util.getNumberOfServers() + 1);
    }

    private void goTimelineScreen(String serverNumber, String status, int position) {
        Toast.makeText(context, serverNumber + " - " + status, Toast.LENGTH_SHORT).show();
        Util.getUser().getTempInfo().setCurrentServer(elements.get(position));
        Util.setServer(elements.get(position));
        User user = Util.getUser();
        user.getTempInfo().setCurrentServer(elements.get(position));
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).setValue(user);
        Intent intent = new Intent(context, TimelineActivity.class);
        intent.putExtra("subject", elements.get(position).getSubject());
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
}
