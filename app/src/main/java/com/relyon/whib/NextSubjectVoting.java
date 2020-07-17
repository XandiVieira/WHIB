package com.relyon.whib;

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
import com.relyon.whib.modelo.Alternative;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Survey;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.modelo.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NextSubjectVoting extends AppCompatActivity {

    private RecyclerView alternativesRV;
    private TextView successMessage;
    private RecyclerViewAlternativeAdapter adapter;
    private Survey survey;
    private Survey newSurvey;

    private EditText newAlternative;
    private ListView subjectsAdded;
    private List<String> alternatives = new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_subject_voting);

        ImageView back = findViewById(R.id.back);
        alternativesRV = findViewById(R.id.alternatives);
        successMessage = findViewById(R.id.successMessage);
        Button create = findViewById(R.id.create);
        Button end = findViewById(R.id.end);
        ImageView add = findViewById(R.id.add);
        newAlternative = findViewById(R.id.newAlternative);
        LinearLayout adminLayout = findViewById(R.id.adminLayout);
        subjectsAdded = findViewById(R.id.subjectsAdded);
        date = findViewById(R.id.date);

        if (Util.getUser().isAdmin()) {
            adminLayout.setVisibility(View.VISIBLE);
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, alternatives);
            subjectsAdded.setAdapter(arrayAdapter);
        } else {
            adminLayout.setVisibility(View.GONE);
        }

        add.setOnClickListener(v -> {
            if (!newAlternative.getText().toString().isEmpty()) {
                if (newSurvey == null) {
                    newSurvey = new Survey(new ArrayList<>());
                }
                newSurvey.getAlternatives().add(new Alternative(UUID.randomUUID().toString(), newAlternative.getText().toString()));
                alternatives.add(newAlternative.getText().toString());
                newAlternative.setText("");
                subjectsAdded.setAdapter(arrayAdapter);
            } else {
                Toast.makeText(getApplicationContext(), "O campo não deve estar vazio.", Toast.LENGTH_SHORT).show();
            }
        });

        end.setOnClickListener(v -> {
            if (survey != null/* && new Date().getTime() >= survey.getEndDate()*/) {
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
                        Integer[] number = new Integer[helperList.size()];
                        helperList.toArray(number);
                        Arrays.sort(number);
                        if (finalSubject != null) {
                            Timeline tl = new Timeline(null, finalSubject, null);
                            ServerTempInfo serverTempInfo = new ServerTempInfo(0, true, (number.length > 0 && number[0] != null && number[number.length - 1] != null) ? findFirstMissing(number) : 0);
                            Server server = new Server(UUID.randomUUID().toString(), serverTempInfo, finalSubject, tl);
                            HashMap<String, Server> map = new HashMap<>();
                            map.put(server.getServerUID(), server);
                            Subject newSubject = new Subject(finalSubject, map, new Date().getTime(), true);
                            Util.mSubjectDatabaseRef.child(lessPopularSubject).removeValue();
                            Util.mSubjectDatabaseRef.child(finalSubject).setValue(newSubject);
                            Util.mDatabaseRef.child("survey").removeValue();
                            finish();
                            startActivity(new Intent(getApplicationContext(), NextSubjectVoting.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Data de término ainda não chegou", Toast.LENGTH_SHORT).show();
            }
        });

        create.setOnClickListener(v -> {
            Util.mDatabaseRef.child("survey").removeValue();
            Util.mDatabaseRef.child("survey").setValue(newSurvey);
            finish();
            startActivity(new Intent(getApplicationContext(), NextSubjectVoting.class));
        });

        back.setOnClickListener(v -> onBackPressed());

        Util.mDatabaseRef.child("survey").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                survey = dataSnapshot.getValue(Survey.class);
                if (survey != null && survey.getAlternatives() != null) {
                    adapter = new RecyclerViewAlternativeAdapter(getApplicationContext(), survey, alternativesRV);
                    alternativesRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    alternativesRV.setAdapter(adapter);
                    date.setText(dateFormat.format(survey.getEndDate()));
                }
                if (survey != null && survey.getAlreadyVoted().contains(Util.getUser().getUserUID())) {
                    successMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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