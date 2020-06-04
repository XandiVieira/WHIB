package com.relyon.whib;

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
import com.relyon.whib.modelo.Survey;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NextSubjectVoting extends AppCompatActivity {

    private ImageView back;
    private RecyclerView alternativesRV;
    private TextView successMessage;
    private RecyclerViewAlternativeAdapter adapter;
    private Survey survey;

    private Button create;
    private ImageView add;
    private EditText newAlternative;
    private LinearLayout adminLayout;
    private ListView subjectsAdded;
    private List<String> alternatives = new ArrayList<>();
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_subject_voting);

        back = findViewById(R.id.back);
        alternativesRV = findViewById(R.id.alternatives);
        successMessage = findViewById(R.id.successMessage);
        create = findViewById(R.id.create);
        add = findViewById(R.id.add);
        newAlternative = findViewById(R.id.newAlternative);
        adminLayout = findViewById(R.id.adminLayout);
        subjectsAdded = findViewById(R.id.subjectsAdded);

        if (Util.getUser().isAdmin()) {
            adminLayout.setVisibility(View.VISIBLE);
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, alternatives);
            subjectsAdded.setAdapter(arrayAdapter);
        } else {
            adminLayout.setVisibility(View.GONE);
        }

        add.setOnClickListener(v -> {
            if (!newAlternative.getText().toString().isEmpty()) {
                if (survey == null) {
                    survey = new Survey(new ArrayList<>());
                }
                survey.getAlternatives().add(new Alternative(UUID.randomUUID().toString(), newAlternative.getText().toString()));
                alternatives.add(newAlternative.getText().toString());
                newAlternative.setText("");
                subjectsAdded.setAdapter(arrayAdapter);
            } else {
                Toast.makeText(getApplicationContext(), "O campo não deve estar vazio.", Toast.LENGTH_SHORT).show();
            }
        });

        create.setOnClickListener(v -> {
            Util.getmDatabaseRef().child("survey").setValue(survey);
        });

        back.setOnClickListener(v -> onBackPressed());

        Util.getmDatabaseRef().child("survey").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                survey = dataSnapshot.getValue(Survey.class);
                if (survey != null && survey.getAlternatives() != null) {
                    adapter = new RecyclerViewAlternativeAdapter(getApplicationContext(), survey, alternativesRV);
                    alternativesRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    alternativesRV.setAdapter(adapter);
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
}