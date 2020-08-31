package com.relyon.whib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.relyon.whib.R;
import com.relyon.whib.modelo.Complaint;
import com.relyon.whib.util.Util;
import com.relyon.whib.util.Constants;

import java.util.Date;
import java.util.List;

public class RecyclerViewComplaintAdapter extends RecyclerView.Adapter<RecyclerViewComplaintAdapter.ViewHolder> {

    private final List<Complaint> elements;
    private Context context;

    public RecyclerViewComplaintAdapter(Context context, List<Complaint> elements) {
        this.context = context;
        this.elements = elements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewComplaintAdapter.ViewHolder holder, final int position) {
        Complaint complaint = elements.get(position);

        holder.complaint.setText(complaint.getQuestion());
        holder.complaintDate.setText(Util.formatDate(complaint.getDateQuestion(), "dd/MM//yy HH:mm:ss"));

        if (Util.getUser().isAdmin()) {
            holder.adminLayout.setVisibility(View.VISIBLE);
            holder.userLayout.setVisibility(View.GONE);

            holder.send.setOnClickListener(v -> {
                if (!holder.editText.getText().toString().isEmpty() && holder.editText.getText().toString().length() > 5) {
                    complaint.setAnswered(true);
                    complaint.setDateAnswer(new Date().getTime());
                    complaint.setAnswer(holder.editText.getText().toString());
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_COMPLAINT).child(complaint.getComplaintId()).setValue(complaint);
                } else {
                    Toast.makeText(context, "Resposta muito curta", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.adminLayout.setVisibility(View.GONE);
            holder.userLayout.setVisibility(View.VISIBLE);
            holder.complaint.setText(complaint.getQuestion());
            holder.complaintDate.setText(Util.formatDate(complaint.getDateQuestion(), "dd/MM//yy HH:mm:ss"));
            if (complaint.isAnswered()) {
                holder.answer.setText(complaint.getAnswer());
                holder.answerDate.setText(Util.formatDate(complaint.getDateAnswer(), "dd/MM//yy HH:mm:ss"));
            } else {
                holder.answer.setText(context.getResources().getString(R.string.answer_soon));
                holder.answerDate.setText(context.getResources().getString(R.string.soon));
            }
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout userLayout;
        private LinearLayout adminLayout;
        private TextView complaint;
        private TextView answer;
        private TextView answerDate;
        private TextView complaintDate;
        private EditText editText;
        private Button send;

        ViewHolder(View rowView) {
            super(rowView);
            userLayout = rowView.findViewById(R.id.userLayout);
            adminLayout = rowView.findViewById(R.id.adminLayout);
            answer = rowView.findViewById(R.id.answer);
            answerDate = rowView.findViewById(R.id.answerDate);
            complaintDate = rowView.findViewById(R.id.complaintDate);
            complaint = rowView.findViewById(R.id.myComplaint);
            editText = rowView.findViewById(R.id.answerEditText);
            send = rowView.findViewById(R.id.send);
        }
    }
}