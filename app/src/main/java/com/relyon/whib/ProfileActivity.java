package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Report;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ProfileActivity extends AppCompatActivity {

    private EditText nick;
    private User user;
    private ImageView photo;
    private TextView userName;
    private MaterialRatingBar ratingBar;
    private TextView rating;
    private TextView goodValuation;
    private TextView mediumValuation;
    private TextView badValuation;
    private TextView sentReports;
    private TextView receivedReports;
    private RecyclerView reports;
    private List<Report> reportList = new ArrayList<>();
    private TextView empty;
    private LinearLayout reportsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView back = findViewById(R.id.back);
        photo = findViewById(R.id.photo);
        ImageView settings = findViewById(R.id.settings);
        userName = findViewById(R.id.userName);
        nick = findViewById(R.id.nick);
        ratingBar = findViewById(R.id.my_stars);
        rating = findViewById(R.id.my_rating);
        goodValuation = findViewById(R.id.good_valuation);
        mediumValuation = findViewById(R.id.medium_valuation);
        badValuation = findViewById(R.id.bad_valuation);
        sentReports = findViewById(R.id.sent_reports);
        receivedReports = findViewById(R.id.received_reports);
        reports = findViewById(R.id.reports);
        empty = findViewById(R.id.empty);
        reportsLayout = findViewById(R.id.reportsLayout);

        if (getIntent().hasExtra("userId") && !getIntent().getStringExtra("userId").equals(Util.getUser().getUserUID())) {
            Util.mUserDatabaseRef.child(getIntent().getStringExtra("userId")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        setUserProfile(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            if (Util.getUser() != null) {
                user = Util.getUser();
                setUserProfile(true);
            } else {
                //user =
            }
        }

        back.setOnClickListener(v -> {
            if (user.getNickName() == null && !nick.getText().toString().equals("")) {
                user.setNickName(nick.getText().toString());
                Util.getmUserDatabaseRef().child(user.getUserUID()).setValue(user);
            }
            onBackPressed();
        });

        settings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SettingsActivity.class)));
    }

    private void setUserProfile(boolean loadReports) {
        final int[] countReceivedReports = {0};
        final int[] countSentReports = {0};
        if (loadReports) {
            reportsLayout.setVisibility(View.VISIBLE);
        } else {
            reportsLayout.setVisibility(View.GONE);
        }
        Query query = Util.mDatabaseRef.child("report").orderByChild("userReceiverUID").equalTo(user.getUserUID());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Report report = snap.getValue(Report.class);
                    if (report != null) {
                        countReceivedReports[0]++;
                        if (report.isFair() && loadReports) {
                            report.setId(snap.getKey());
                            reportList.add(report);
                        }
                    }
                }
                if (reportList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                }
                receivedReports.setText(String.valueOf(countReceivedReports[0]));
                RecyclerViewReportAdapter recyclerViewReportAdapter = new RecyclerViewReportAdapter(reportList);
                reports.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                reports.setAdapter(recyclerViewReportAdapter);

                Query query = Util.mDatabaseRef.child("report").orderByChild("userSenderUID").equalTo(user.getUserUID());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Report report = snap.getValue(Report.class);
                            if (report != null) {
                                countReceivedReports[0]++;
                            }
                        }
                        sentReports.setText(String.valueOf(countSentReports[0]));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Glide.with(getApplicationContext()).load(user.getPhotoPath()).apply(RequestOptions.circleCropTransform()).into(photo);

        userName.setText(user.getUserName());
        ratingBar.setStepSize(0.01f);
        if (user.getValuation().getSumOfRatings() != 0 && user.getValuation().getNumberOfRatings() != 0) {
            ratingBar.setRating(user.getValuation().getSumOfRatings() / user.getValuation().getNumberOfRatings());
            rating.setText(String.format("%.2f", user.getValuation().getSumOfRatings() / user.getValuation().getNumberOfRatings()));
        } else {
            ratingBar.setRating(0);
            rating.setText(String.format("%.2f", 0));
        }
        ratingBar.setIsIndicator(true);
        goodValuation.setText(user.getValuation().getGoodPercentage() + "%");
        mediumValuation.setText(user.getValuation().getMediumPercentage() + "%");
        badValuation.setText(user.getValuation().getBadPercentage() + "%");

        if ((user.getNickName() != null && !user.getNickName().isEmpty()) || !user.getUserUID().equals(Util.getUser().getUserUID())) {
            nick.setEnabled(false);
        } else {
            nick.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}