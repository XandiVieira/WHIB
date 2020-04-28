package com.relyon.whib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;

public class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<Object> elementos;
    private AppCompatActivity activity;
    private static final int COMMENT_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    RecyclerViewCommentAdapter(@NonNull Context context, ArrayList<Object> elementos, AppCompatActivity activity) {
        this.context = context;
        this.elementos = elementos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.ad_unified,
                        viewGroup, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case COMMENT_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.comment_item, viewGroup, false);
                return new CommentViewHolder(menuItemLayoutView);
        }
    }

    private void openRatingDialog(float rating, int position) {
        DialogRateComment cdd = new DialogRateComment(activity, rating, (Comment) elementos.get(position), position, elementos);
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.show();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        int viewType = getItemViewType(position);

        if (viewType == NATIVE_EXPRESS_AD_VIEW_TYPE) {
            UnifiedNativeAd nativeAd = (UnifiedNativeAd) elementos.get(position);
            populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder1).getAdView());
        } else {
            CommentViewHolder holder = (CommentViewHolder) holder1;
            final Comment comment = (Comment) elementos.get(position);

            holder.ratingBar.setNumStars(5);
            holder.ratingBar.setRating(comment.getRating());
            holder.ratingTV.setText(String.format("%.2f", comment.getRating()));
            holder.text.setText(comment.getText());
            Glide.with(context)
                    .load(comment.getUserPhotoURL())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.photo);

            if (comment.getAlreadyRatedList().contains(Util.getUser().getUserUID())/* || comment.getAuthorsUID().equals(Util.getUser().getUserUID())*/) {
                holder.ratingBar.setIsIndicator(true);
            } else {
                holder.ratingBar.setIsIndicator(false);
            }
            Util.mUserDatabaseRef.child(comment.getAuthorsUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.isExtra()) {
                        LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
                        stars.getDrawable(2).setColorFilter(Color.parseColor("#AFC2D5"), PorterDuff.Mode.SRC_ATOP);
                        holder.bg.setBackgroundResource(R.drawable.rounded_accent_double);
                    } else if (comment.isAGroup()) {
                        holder.bg.setBackgroundResource(R.drawable.rounded_primary_double);
                    }
                    holder.userName.setText(user.getUserName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (elementos.get(position) instanceof Comment) {
                    openRatingDialog(rating, position);
                    holder.ratingTV.setText(String.valueOf(rating));
                    ratingBar.setRating(rating);
                    Toast.makeText(context, String.valueOf(rating), Toast.LENGTH_SHORT).show();
                }
            });

            holder.text.setOnClickListener(v -> goToGroup(comment));
            holder.ratingTV.setOnClickListener(v -> goToGroup(comment));
            holder.text.setOnLongClickListener(v -> {
                if (comment.getAuthorsUID().equals(Util.getUser().getUserUID())) {
                    return openReportDialog(comment);
                } else {
                    Toast.makeText(context, context.getString(R.string.cant_report_own_comment), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
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
    public int getItemViewType(int position) {
        Object recyclerViewItem = elementos.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return NATIVE_EXPRESS_AD_VIEW_TYPE;
        }
        return COMMENT_ITEM_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return elementos.size();
    }

    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        private UnifiedNativeAdView adView;

        NativeExpressAdViewHolder(View rowView) {
            super(rowView);
            adView = rowView.findViewById(R.id.ad_media);

            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_headline));
            adView.setCallToActionView(adView.findViewById(R.id.ad_headline));
            adView.setIconView(adView.findViewById(R.id.ad_headline));
            adView.setPriceView(adView.findViewById(R.id.ad_headline));
            adView.setStarRatingView(adView.findViewById(R.id.ad_headline));
            adView.setStoreView(adView.findViewById(R.id.ad_headline));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_headline));
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private TextView ratingTV;
        private ImageView photo;
        private RatingBar ratingBar;
        private LinearLayout bg;
        private TextView userName;

        CommentViewHolder(View rowView) {
            super(rowView);
            text = rowView.findViewById(R.id.text);
            ratingTV = rowView.findViewById(R.id.ratingTV);
            photo = rowView.findViewById(R.id.photo);
            ratingBar = rowView.findViewById(R.id.ratingBar);
            bg = rowView.findViewById(R.id.background);
            userName = rowView.findViewById(R.id.userName);
        }
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }
}