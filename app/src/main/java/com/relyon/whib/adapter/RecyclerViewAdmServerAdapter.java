package com.relyon.whib.adapter;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class RecyclerViewAdmServerAdapter extends RecyclerView.Adapter<RecyclerViewAdmServerAdapter.ViewHolder> {

    private final ArrayList<Server> admServerList;
    private int comments = 0;
    private int servers = 0;
    private Context context;

    public RecyclerViewAdmServerAdapter(ArrayList<Server> admServerList, Context context) {
        this.admServerList = admServerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adm_server, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Server server = admServerList.get(position);

        holder.subjectTitle.setText(server.getSubject());

        holder.deleteButton.setOnClickListener(v -> Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).removeValue());

        holder.disableSwitch.setChecked(server.getTempInfo().isActivated());

        if (holder.disableSwitch.isChecked()) {
            holder.disableSwitch.setText("Ativo");
        } else {
            holder.disableSwitch.setText("Desativo");
        }

        holder.subjectTitle.setOnClickListener(v -> callDialog(position));

        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).removeEventListener(this);
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Server server1 = snap.getValue(Server.class);
                    if (server1 != null && server1.getTimeline() != null && server1.getTimeline().getCommentList() != null && server1.getTimeline().getCommentList().size() > 0) {
                        comments += server1.getTimeline().getCommentList().values().size();
                    }
                    servers++;
                }
                int popularity = servers * comments;
                holder.tvPopularity.setText(popularity + " ");
                servers = 0;
                comments = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.disableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.disableSwitch.setText("Ativo");
            } else {
                holder.disableSwitch.setText("Desativo");
            }
            server.getTempInfo().setActivated(holder.disableSwitch.isChecked());
            Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server.getSubject()).removeEventListener(this);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Server server1 = snapshot.getValue(Server.class);
                        if (server1 != null && server1.getSubject().equals(server.getSubject())) {
                            Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(server1.getSubject()).child(server1.getServerUID()).child(Constants.DATABASE_REF_TEMP_INFO).child(Constants.DATABASE_REF_ACTIVATED).setValue(server.getTempInfo().isActivated());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
    }

    private void callDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(context);
        input.setText(admServerList.get(position).getSubject());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Integer> helperList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subject subject1 = snapshot.getValue(Subject.class);
                    if (subject1 != null && subject1.getServers() != null) {
                        for (Server server : subject1.getServers().values()) {
                            helperList.add(server.getTempInfo().getNumber());
                        }
                    }
                }
                Integer[] takenServerNumbers = new Integer[helperList.size()];
                helperList.toArray(takenServerNumbers);
                Arrays.sort(takenServerNumbers);
                HashMap<String, Server> map = new HashMap<>();
                Subject newSubject = new Subject(input.getText().toString(), map, new Date().getTime(), true);
                map.put(input.getText().toString(), new Server(UUID.randomUUID().toString(), newSubject.getTitle(), (takenServerNumbers.length > 0 && takenServerNumbers[0] != null && takenServerNumbers[takenServerNumbers.length - 1] != null) ? findFirstAvailableServerNumber(takenServerNumbers) : 0));
                newSubject.setServers(map);
                Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).push().setValue(newSubject);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public int findFirstAvailableServerNumber(Integer[] takenServerNumbers) {
        for (int i = 0; i < takenServerNumbers.length; i++) {
            int target = takenServerNumbers[i];
            while (target < takenServerNumbers.length && target != takenServerNumbers[target]) {
                int new_target = takenServerNumbers[target];
                takenServerNumbers[target] = target;
                target = new_target;
            }
        }

        for (int i = 0; i < takenServerNumbers.length; i++) {
            if (takenServerNumbers[i] != i) {
                return i;
            }
        }
        return takenServerNumbers.length;
    }

    @Override
    public int getItemCount() {
        return admServerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView subjectTitle;
        private TextView tvPopularity;
        private SwitchCompat disableSwitch;
        private ImageButton deleteButton;

        ViewHolder(View rowView) {
            super(rowView);
            this.subjectTitle = rowView.findViewById(R.id.subject_title);
            this.tvPopularity = rowView.findViewById(R.id.popularity);
            this.disableSwitch = rowView.findViewById(R.id.disable_server);
            this.deleteButton = rowView.findViewById(R.id.delete);
        }
    }
}