package com.relyon.whib.activity.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.activity.ProfileActivity;
import com.relyon.whib.adapter.RecyclerViewReportAdapter;
import com.relyon.whib.modelo.Report;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.List;

public class TabReports extends Fragment {

    private User user;
    private ProfileActivity profileActivity;
    private List<Report> reportList;

    private TextView sentReports;
    private TextView receivedReports;
    private TextView empty;
    private RecyclerView reports;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);

        profileActivity = (ProfileActivity) getActivity();

        sentReports = rootView.findViewById(R.id.sent_reports);
        receivedReports = rootView.findViewById(R.id.received_reports);
        reports = rootView.findViewById(R.id.reports);
        empty = rootView.findViewById(R.id.empty);
        LinearLayout reportsLayout = rootView.findViewById(R.id.reportsLayout);

        if (profileActivity != null) {
            user = profileActivity.getUser();
            if (user == null) {
                user = Util.getUser();
            }
            if (user != null) {
                loadReports();
            }
            if (profileActivity.isLoadReports()) {
                reportsLayout.setVisibility(View.VISIBLE);
            } else {
                reportsLayout.setVisibility(View.GONE);
            }
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadReports() {
        final int[] countReceivedReports = {0};
        final int[] countSentReports = {0};

        Query query = Util.mDatabaseRef.child(Constants.DATABASE_REF_REPORT).orderByChild(Constants.DATABASE_REF_USER_RECEIVER_ID).equalTo(user.getUserUID());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportList = new ArrayList<>();
                countReceivedReports[0] = 0;
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Report report = snap.getValue(Report.class);
                    if (report != null) {
                        countReceivedReports[0]++;
                        if (report.isFair() && profileActivity.isLoadReports()) {
                            report.setId(snap.getKey());
                            reportList.add(report);
                        }
                    }
                }
                if (reportList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                }
                receivedReports.setText(String.valueOf(countReceivedReports[0]));
                setReportAdapter();

                Query query = Util.mDatabaseRef.child(Constants.DATABASE_REF_REPORT).orderByChild(Constants.DATABASE_REF_USER_SENDER_ID).equalTo(user.getUserUID());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        countSentReports[0] = 0;
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Report report = snap.getValue(Report.class);
                            if (report != null) {
                                countSentReports[0]++;
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
    }

    private void setReportAdapter() {
        RecyclerViewReportAdapter recyclerViewReportAdapter = new RecyclerViewReportAdapter(reportList);
        reports.setLayoutManager(new LinearLayoutManager(getContext()));
        reports.setAdapter(recyclerViewReportAdapter);
    }
}