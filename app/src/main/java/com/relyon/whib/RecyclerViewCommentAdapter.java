package com.relyon.whib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;

public class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerViewCommentAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Comment> elementos;
    private AppCompatActivity activity;

    RecyclerViewCommentAdapter(@NonNull Context context, ArrayList<Comment> elementos, AppCompatActivity activity) {
        this.context = context;
        this.elementos = elementos;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(rowView);
    }

    private void openRatingDialog(float rating, int position) {
        DialogRateComment cdd = new DialogRateComment(activity, rating, elementos.get(position), position, elementos);
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.show();
    }

    @Override
    public void onBindViewHolder(final RecyclerViewCommentAdapter.ViewHolder holder, final int position) {

        final Comment comment = elementos.get(position);

        holder.ratingBar.setNumStars(5);
        holder.ratingBar.setRating(elementos.get(position).getRating());
        holder.ratingTV.setText(String.format("%.2f", elementos.get(position).getRating()));
        holder.text.setText(comment.getText());
        Glide.with(context)
                .load(comment.getUserPhotoURL())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.photo);

        if (elementos.get(position).getAlreadyRatedList().contains(Util.getUser().getUserUID())/* || elementos.get(position).getAuthorsUID().equals(Util.getUser().getUserUID())*/) {
            holder.ratingBar.setIsIndicator(true);
        } else {
            holder.ratingBar.setIsIndicator(false);
        }
        Util.mUserDatabaseRef.child(elementos.get(position).getAuthorsUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.isExtra()) {
                    LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
                    stars.getDrawable(2).setColorFilter(Color.parseColor("#AFC2D5"), PorterDuff.Mode.SRC_ATOP);
                    holder.bg.setBackgroundResource(R.drawable.rounded_accent_double);
                } else if (elementos.get(position).isAGroup()) {
                    holder.bg.setBackgroundResource(R.drawable.rounded_primary_double);
                }
                holder.userName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                openRatingDialog(rating, position);
                holder.ratingTV.setText(String.valueOf(rating));
                ratingBar.setRating(rating);
                Toast.makeText(context, String.valueOf(rating), Toast.LENGTH_SHORT).show();
            }
        });

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGroup(elementos.get(position));
            }
        });
        holder.ratingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGroup(elementos.get(position));
            }
        });
        holder.text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!comment.getAuthorsUID().equals(Util.getUser().getUserUID())){
                    return openReportDialog(elementos.get(position));
                }else {
                    Toast.makeText(context, context.getString(R.string.cant_report_own_comment), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

    private boolean openReportDialog(Comment comment) {
        DialogReport dialogReport = new DialogReport(activity, comment);
        dialogReport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReport.show();
        return true;
    }

    private void goToGroup(Comment comment) {
        if (comment.isAGroup() && comment.getGroup() != null && comment.getGroup().isReady()) {
            if (!comment.getGroup().getTempInfo().isFull()) {
                Toast.makeText(context, "Entrou no grupo!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, GroupActivity.class);
                if (comment.getGroup().getUserListUID() != null && !comment.getGroup().getUserListUID().contains(Util.getUser().getUserUID())) {
                    comment.getGroup().getUserListUID().add(Util.getUser().getUserUID());
                }
                Util.getUser().getTempInfo().setCurrentGroup(comment.getGroup());
                Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("tempInfo").child("currentGroup").setValue(Util.getUser().getTempInfo().getCurrentGroup());
                Util.setComment(comment);
                Util.setGroup(comment.getGroup());
                context.startActivity(intent);
                activity.finish();
            } else {
                WarnGroupFull warnGroupFull = new WarnGroupFull(activity);
                warnGroupFull.show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private TextView ratingTV;
        private ImageView photo;
        private RatingBar ratingBar;
        private LinearLayout bg;
        private TextView userName;

        ViewHolder(View rowView) {
            super(rowView);
            text = rowView.findViewById(R.id.text);
            ratingTV = rowView.findViewById(R.id.ratingTV);
            photo = rowView.findViewById(R.id.photo);
            ratingBar = rowView.findViewById(R.id.ratingBar);
            bg = rowView.findViewById(R.id.background);
            userName = rowView.findViewById(R.id.userName);
        }
    }
}
