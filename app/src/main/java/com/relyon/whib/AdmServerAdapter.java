package com.relyon.whib;

import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class AdmServerAdapter extends BaseAdapter {

    private final ArrayList<Server> admServerList;
    private final AppCompatActivity act;
    private int comments = 0;
    private int servers = 0;

    AdmServerAdapter(ArrayList<Server> admServerList, AppCompatActivity act) {
        this.admServerList = admServerList;
        this.act = act;
    }

    @Override
    public int getCount() {
        return admServerList.size();
    }

    @Override
    public Object getItem(int position) {
        return admServerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = act.getLayoutInflater().inflate(R.layout.item_adm_server, parent, false);
        final Server server = admServerList.get(position);
        servers = 0;
        comments = 0;
        TextView subjectTitle = view.findViewById(R.id.subjectTitle);
        final Switch disableSwitch = view.findViewById(R.id.disableServer);
        ImageButton deleteButton = view.findViewById(R.id.delete);
        TextView popularity = view.findViewById(R.id.popularity);
        subjectTitle.setText(server.getSubject());

        deleteButton.setOnClickListener(v -> {
            Util.mSubjectDatabaseRef.child(server.getSubject()).removeValue();
        });

        disableSwitch.setChecked(server.getTempInfo().isActivated());

        if (disableSwitch.isChecked()) {
            disableSwitch.setText("Ativo");
        } else {
            disableSwitch.setText("Desativo");
        }

        subjectTitle.setOnClickListener(v -> callDialog(view, position));

        Util.mSubjectDatabaseRef.child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                servers = 0;
                Util.mSubjectDatabaseRef.child(server.getSubject()).child(Constants.DATABASE_REF_SERVERS).removeEventListener(this);
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Server server1 = snap.getValue(Server.class);
                    if (server1 != null && server1.getTimeline() != null && server1.getTimeline().getCommentList() != null && server1.getTimeline().getCommentList().size() > 0) {
                        comments = comments + (server1.getTimeline().getCommentList().values().size());
                    }
                    servers++;
                }
                int pop = servers * comments;
                popularity.setText(pop + " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        disableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                disableSwitch.setText("Ativo");
            } else {
                disableSwitch.setText("Desativo");
            }
            server.getTempInfo().setActivated(disableSwitch.isChecked());
            Util.mSubjectDatabaseRef.child(server.getSubject()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Util.mSubjectDatabaseRef.child(server.getSubject()).removeEventListener(this);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Server server1 = snapshot.getValue(Server.class);
                        if (server1 != null && server1.getSubject().equals(server.getSubject())) {
                            Util.mSubjectDatabaseRef.child(server1.getSubject()).child(server1.getServerUID()).child(Constants.DATABASE_REF_TEMP_INFO).child(Constants.DATABASE_REF_ACTIVATED).setValue(server.getTempInfo().isActivated());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        return view;
    }

    private void callDialog(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(view.getContext());
        input.setText(admServerList.get(position).getSubject());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> Util.mSubjectDatabaseRef.addValueEventListener(new ValueEventListener() {
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
                Integer[] number = new Integer[helperList.size()];
                helperList.toArray(number);
                Arrays.sort(number);
                HashMap<String, Server> map = new HashMap<>();
                Subject newSubject = new Subject(input.getText().toString(), map, new Date().getTime(), true);
                map.put(input.getText().toString(), new Server(UUID.randomUUID().toString(), newSubject.getTitle(),(number.length > 0 && number[0] != null && number[number.length - 1] != null) ? findFirstMissing(number) : 0));
                newSubject.setServers(map);
                Util.mSubjectDatabaseRef.push().setValue(newSubject);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
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