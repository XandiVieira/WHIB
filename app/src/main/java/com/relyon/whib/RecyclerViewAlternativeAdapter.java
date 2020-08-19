package com.relyon.whib;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Alternative;
import com.relyon.whib.modelo.Survey;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecyclerViewAlternativeAdapter extends RecyclerView.Adapter<RecyclerViewAlternativeAdapter.ViewHolder> {

    private RecyclerView recyclerView;
    private Survey survey;
    private boolean notYet = true;
    private List<Integer> colors = new ArrayList<>();

    public RecyclerViewAlternativeAdapter(Survey survey, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.survey = survey;
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.BLACK);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        colors.add(Color.GRAY);
        colors.add(Color.DKGRAY);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alternative, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Alternative alternative = survey.getAlternatives().get(position);
        float percentage = ((float) alternative.getNumVotes() / (float) survey.getNumVotes()) * 100;
        holder.text.setText(alternative.getText());
        if (!survey.getAlreadyVoted().contains(Util.getUser().getUserUID())) {
            holder.partials.setVisibility(View.GONE);
            holder.bg.setOnClickListener(v -> Util.getmDatabaseRef().child("survey").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (notYet) {
                        notYet = false;
                        survey = dataSnapshot.getValue(Survey.class);
                        if (survey != null) {
                            survey.setNumVotes(survey.getNumVotes() + 1);
                            if (survey.getAlreadyVoted() == null) {
                                survey.setAlreadyVoted(new ArrayList<>());
                            }
                            survey.getAlreadyVoted().add(Util.getUser().getUserUID());
                            survey.getAlternatives().get(position).setNumVotes(survey.getAlternatives().get(position).getNumVotes() + 1);
                            survey.getAlternatives().get(position).getVotedForMe().add(Util.getUser().getUserUID());
                            Util.getmDatabaseRef().child("survey").setValue(survey);
                            updateUI(holder);
                        }
                    }
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRangeChanged(0, survey.getAlternatives().size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            }));
        } else {
            updateUI(holder);
            holder.percentage.setText(percentage + "%");
            if (alternative.getVotedForMe().contains(Util.getUser().getUserUID())) {
                holder.check.setVisibility(View.VISIBLE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.progressBar.setProgress((int) percentage, true);
                holder.progressBar.setProgressTintList(ColorStateList.valueOf(colors.get(position)));
            }
        }
    }

    private void updateUI(ViewHolder holder) {
        holder.partials.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return survey.getAlternatives().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout bg;
        private TextView text;
        private LinearLayout partials;
        private ProgressBar progressBar;
        private TextView percentage;
        private TextView check;

        ViewHolder(View rowView) {
            super(rowView);
            text = rowView.findViewById(R.id.option);
            bg = rowView.findViewById(R.id.layout);
            partials = rowView.findViewById(R.id.partials);
            progressBar = rowView.findViewById(R.id.progressBar);
            percentage = rowView.findViewById(R.id.progressText);
            check = rowView.findViewById(R.id.check);
        }
    }
}