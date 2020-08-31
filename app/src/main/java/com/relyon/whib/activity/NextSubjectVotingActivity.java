package com.relyon.whib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.adapter.RecyclerViewAlternativeAdapter;
import com.relyon.whib.modelo.Alternative;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Survey;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.util.Util;
import com.relyon.whib.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NextSubjectVotingActivity extends AppCompatActivity {

    private Activity activity;
    private RecyclerViewAlternativeAdapter adapter;
    private Survey survey;
    private Survey newSurvey;
    private List<String> alternatives = new ArrayList<>();

    private RecyclerView rvAlternatives;
    private TextView votedSuccessfullyMessage;
    private EditText newAlternative;
    private ListView subjectsAdded;
    private ArrayAdapter arrayAdapter;
    private TextView date;
    private ImageView back;
    private Button create;
    private Button end;
    private ImageView add;
    private LinearLayout adminLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_subject_voting);

        activity = this;

        setLayoutAttributes();

        verifyUserIsAdmin();

        add.setOnClickListener(v -> addNewAlternative());

        end.setOnClickListener(v -> {
            endSurvey();
        });

        create.setOnClickListener(v -> {
            Util.mDatabaseRef.child(Constants.DATABASE_REF_SURVEY).removeValue();
            Util.mDatabaseRef.child(Constants.DATABASE_REF_SURVEY).setValue(newSurvey);
            finish();
            startActivity(new Intent(this, NextSubjectVotingActivity.class));
        });

        back.setOnClickListener(v -> onBackPressed());

        Util.mDatabaseRef.child(Constants.DATABASE_REF_SURVEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                survey = dataSnapshot.getValue(Survey.class);
                if (survey != null && survey.getAlternatives() != null) {
                    adapter = new RecyclerViewAlternativeAdapter(survey, rvAlternatives);
                    rvAlternatives.setLayoutManager(new LinearLayoutManager(activity));
                    rvAlternatives.setAdapter(adapter);
                    date.setText(Util.formatDate(survey.getEndDate(), "yyyy/MM/dd - HH:mm"));
                }
                if (survey != null && survey.getAlreadyVoted().contains(Util.getUser().getUserUID())) {
                    votedSuccessfullyMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void endSurvey() {
        if (survey != null && new Date().getTime() >= survey.getEndDate()) {
            String subject = null;
            int percentage = 0;
            for (Alternative alt : survey.getAlternatives()) {
                if (alt.getNumVotes() > percentage) {
                    percentage = alt.getNumVotes();
                    subject = alt.getText();
                }
            }
            String finalSubject = subject;
            Util.mSubjectDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isFirst = true;
                    Util.mSubjectDatabaseRef.removeEventListener(this);
                    String lessPopularSubject = "";
                    int popularity = 0;
                    ArrayList<Integer> helperList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Subject subject1 = snapshot.getValue(Subject.class);
                        int servers;
                        int comments = 1;
                        if (subject1 != null && subject1.getServers() != null) {
                            servers = subject1.getServers().values().size();
                            for (Server server : subject1.getServers().values()) {
                                helperList.add(server.getTempInfo().getNumber());
                                if (server.getTimeline() != null && server.getTimeline().getCommentList() != null)
                                    comments += server.getTimeline().getCommentList().size();
                            }
                            if (isFirst) {
                                popularity = comments * servers;
                                lessPopularSubject = subject1.getTitle();
                                isFirst = false;
                            } else if ((comments * servers) < popularity) {
                                popularity = comments * servers;
                                lessPopularSubject = subject1.getTitle();
                            }
                        }
                    }
                    Integer[] serverNumbersAlreadyTaken = new Integer[helperList.size()];
                    helperList.toArray(serverNumbersAlreadyTaken);
                    Arrays.sort(serverNumbersAlreadyTaken);
                    if (finalSubject != null) {
                        Timeline tl = new Timeline(null, finalSubject, null);
                        ServerTempInfo serverTempInfo = new ServerTempInfo(0, true, (serverNumbersAlreadyTaken.length > 0 && serverNumbersAlreadyTaken[0] != null && serverNumbersAlreadyTaken[serverNumbersAlreadyTaken.length - 1] != null) ? findFirstMissingServerNumber(serverNumbersAlreadyTaken) : 0);
                        Server server = new Server(UUID.randomUUID().toString(), serverTempInfo, finalSubject, tl);
                        HashMap<String, Server> map = new HashMap<>();
                        map.put(server.getServerUID(), server);
                        Subject newSubject = new Subject(finalSubject, map, new Date().getTime(), true);
                        Util.mSubjectDatabaseRef.child(lessPopularSubject).removeValue();
                        Util.mSubjectDatabaseRef.child(finalSubject).setValue(newSubject);
                        Util.mDatabaseRef.child(Constants.DATABASE_REF_SURVEY).removeValue();
                        finish();
                        startActivity(new Intent(activity, NextSubjectVotingActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(this, "Data de término ainda não chegou", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewAlternative() {
        if (!newAlternative.getText().toString().isEmpty()) {
            if (newSurvey == null) {
                newSurvey = new Survey(new ArrayList<>());
            }
            newSurvey.getAlternatives().add(new Alternative(UUID.randomUUID().toString(), newAlternative.getText().toString()));
            alternatives.add(newAlternative.getText().toString());
            newAlternative.setText("");
            subjectsAdded.setAdapter(arrayAdapter);
        } else {
            Toast.makeText(this, "O campo não deve estar vazio.", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyUserIsAdmin() {
        if (Util.getUser().isAdmin()) {
            adminLayout.setVisibility(View.VISIBLE);
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alternatives);
            subjectsAdded.setAdapter(arrayAdapter);
        } else {
            adminLayout.setVisibility(View.GONE);
        }
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
        rvAlternatives = findViewById(R.id.alternatives);
        votedSuccessfullyMessage = findViewById(R.id.successMessage);
        create = findViewById(R.id.create);
        end = findViewById(R.id.end);
        add = findViewById(R.id.add);
        newAlternative = findViewById(R.id.newAlternative);
        adminLayout = findViewById(R.id.adminLayout);
        subjectsAdded = findViewById(R.id.subjectsAdded);
        date = findViewById(R.id.date);
    }

    public int findFirstMissingServerNumber(Integer[] serverNumbersAlreadyTaken) {
        for (int i = 0; i < serverNumbersAlreadyTaken.length; i++) {
            int target = serverNumbersAlreadyTaken[i];
            while (target < serverNumbersAlreadyTaken.length && target != serverNumbersAlreadyTaken[target]) {
                int new_target = serverNumbersAlreadyTaken[target];
                serverNumbersAlreadyTaken[target] = target;
                target = new_target;
            }
        }

        for (int i = 0; i < serverNumbersAlreadyTaken.length; i++) {
            if (serverNumbersAlreadyTaken[i] != i) {
                return i;
            }
        }
        return serverNumbersAlreadyTaken.length;
    }
}