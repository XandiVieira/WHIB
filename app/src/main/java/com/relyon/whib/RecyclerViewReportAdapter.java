package com.relyon.whib;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.relyon.whib.modelo.Report;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.Constants;

import java.util.List;

public class RecyclerViewReportAdapter extends RecyclerView.Adapter<RecyclerViewReportAdapter.ViewHolder> {

    private List<Report> elements;

    RecyclerViewReportAdapter(List<Report> elements) {
        this.elements = elements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = elements.get(position);

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

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView reason;
        TextView text;
        TextView explanation;
        TextView sender;
        TextView receiver;
        LinearLayout receiverAndSender;
        LinearLayout explanationLayout;
        LinearLayout decision;
        Button fair;
        Button unfair;

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