package com.relyon.whib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.modelo.Punishment;
import com.relyon.whib.modelo.Report;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecyclerViewReportAdapter extends RecyclerView.Adapter<RecyclerViewReportAdapter.ViewHolder> {

    private List<Report> reports;

    public RecyclerViewReportAdapter(List<Report> reports) {
        this.reports = reports;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reports.get(position);

        if (Util.getUser().isAdmin() || report.isFair()) {
            holder.reason.setText(report.getReason());
            holder.text.setText(report.getText());
            if (report.getExplanation().isEmpty()) {
                holder.explanationLayout.setVisibility(View.GONE);
            } else {
                holder.explanationLayout.setVisibility(View.VISIBLE);
                holder.explanation.setText(report.getExplanation());
            }
            holder.sender.setText(report.getUserSenderUID());
            holder.receiver.setText(report.getUserReceiverUID());
            if (Util.getUser().isAdmin()) {
                holder.receiverAndSender.setVisibility(View.VISIBLE);
                holder.decision.setVisibility(View.VISIBLE);
                report.setReviewed(true);
                holder.fair.setOnClickListener(v -> {
                    report.setFair(true);
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_REPORT).child(report.getId()).setValue(report);
                    getUserReportsQuantity(report);
                    notifyDataSetChanged();
                });
                holder.unfair.setOnClickListener(v -> {
                    report.setFair(false);
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_REPORT).child(report.getId()).setValue(report);
                    notifyDataSetChanged();
                });
            } else {
                holder.receiverAndSender.setVisibility(View.GONE);
                holder.decision.setVisibility(View.GONE);
            }
        }
    }

    private void getUserReportsQuantity(Report report) {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(report.getUserReceiverUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(report.getUserReceiverUID()).removeEventListener(this);
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Punishment punishment;
                    if (user.getPunishment() != null) {
                        punishment = user.getPunishment();
                        punishment.setReportsAfterLastBlock(punishment.getReportsAfterLastBlock() + 1);
                        if (punishment.getReportsAfterLastBlock() >= Constants.REPORTS_TO_BE_BLOCKED) {
                            punishment.setBlocked(true);
                            blockUser(user.getUserUID(), punishment);
                        }
                    } else {
                        punishment = new Punishment(1, false, 0L);
                    }
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(user.getUserUID()).child(Constants.DATABASE_REF_PUNISHMENT).setValue(punishment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void blockUser(String userReceiverUID, Punishment punishment) {
        Long aWeekInTheFuture = addAWeek();
        punishment.setEndDate(aWeekInTheFuture);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(userReceiverUID).child(Constants.DATABASE_REF_PUNISHMENT).setValue(punishment);
    }

    private Long addAWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        return calendar.getTime().getTime();
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView reason;
        private TextView text;
        private TextView explanation;
        private TextView sender;
        private TextView receiver;
        private LinearLayout receiverAndSender;
        private LinearLayout explanationLayout;
        private LinearLayout decision;
        private Button fair;
        private Button unfair;

        ViewHolder(View rowView) {
            super(rowView);
            this.reason = rowView.findViewById(R.id.reason);
            this.text = rowView.findViewById(R.id.text);
            this.explanation = rowView.findViewById(R.id.explanation);
            this.sender = rowView.findViewById(R.id.sender);
            this.receiver = rowView.findViewById(R.id.receiver);
            this.receiverAndSender = rowView.findViewById(R.id.receiverAndSender);
            this.explanationLayout = rowView.findViewById(R.id.explanationLayout);
            this.decision = rowView.findViewById(R.id.decision);
            this.fair = rowView.findViewById(R.id.fair);
            this.unfair = rowView.findViewById(R.id.unfair);
        }
    }
}