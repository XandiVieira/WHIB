package com.relyon.whib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.Report;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private ArrayList<Object> elements;
    private AppCompatActivity activity;
    private static final int COMMENT_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;
    int mPostsPerPage = 10;

    RecyclerViewCommentAdapter(@NonNull Context context, AppCompatActivity activity) {
        this.context = context;
        this.elements = new ArrayList<>();
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.item_ad_unified,
                        viewGroup, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case COMMENT_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_comment, viewGroup, false);
                return new CommentViewHolder(menuItemLayoutView);
        }
    }

    private void openRatingDialog(float rating, int position) {
        Comment comment = (Comment) elements.get(position);
        Util.mUserDatabaseRef.child(comment.getAuthorsUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    DialogRateComment cdd = new DialogRateComment(activity, rating, comment, elements, user.isExtra());
                    if (cdd.getWindow() != null) {
                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        cdd.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        int viewType = getItemViewType(position);
        holder1.setIsRecyclable(false);

        if (viewType == NATIVE_EXPRESS_AD_VIEW_TYPE) {
            UnifiedNativeAd nativeAd = (UnifiedNativeAd) elements.get(position);
            populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder1).getAdView());
        } else {
            CommentViewHolder holder = (CommentViewHolder) holder1;
            final Comment comment = (Comment) elements.get(position);

            GridLayoutManager layoutManager = new GridLayoutManager(context, 5);
            Util.mDatabaseRef.child("product").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Product> finalElements = new ArrayList<>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        finalElements.add(snap.getValue(Product.class));
                    }
                    if (comment.getStickers() != null && comment.getStickers().size() > 0) {
                        RecyclerViewStickerAdapter adapter = new RecyclerViewStickerAdapter(new ArrayList<>(comment.getStickers().values()), context);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                                layoutManager.getOrientation());
                        holder.stickers.addItemDecoration(dividerItemDecoration);
                        holder.stickers.setLayoutManager(layoutManager);
                        holder.stickers.setAdapter(adapter);
                        holder.stickersLayout.setVisibility(View.VISIBLE);
                        holder.stickers.setVisibility(View.VISIBLE);
                    } else {
                        holder.stickersLayout.setVisibility(View.GONE);
                        holder.stickers.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.ratingBar.setNumStars(5);
            holder.ratingBar.setStepSize(0.01f);
            if (((Comment) elements.get(position)).getAlreadyRatedList().contains(Util.getUser().getUserUID()) || ((Comment) elements.get(position)).getAuthorsUID().equals(Util.getUser().getUserUID())) {
                holder.ratingBar.setRating(comment.getRating());
                holder.ratingTV.setText(String.format("%.2f", comment.getRating()));
            } else {
                holder.ratingBar.setRating(0);
                holder.ratingTV.setText(String.format("%.2f", (float) 0));
            }
            holder.text.setText(comment.getText());
            holder.text.setTrimExpandedText(" Ver menos");
            holder.text.setTrimCollapsedText(" Ver mais");
            holder.text.setTrimLines(4);
            holder.text.setColorClickableText(Color.BLUE);

            if (comment.isAGroup()) {
                if (comment.getGroup().isReady()) {
                    holder.entrance.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_exit_active));
                }
                holder.entrance.setVisibility(View.VISIBLE);
                holder.ratingTV.setTextSize(14);
            } else {
                holder.entrance.setVisibility(View.GONE);
            }

            holder.userProfile.setOnClickListener(v -> context.startActivity(new Intent(context, ProfileActivity.class).putExtra("userId", comment.getAuthorsUID())));

            Typeface face = ResourcesCompat.getFont(context, R.font.baloo);
            holder.text.setTypeface(face);

            if (comment.getAlreadyRatedList().contains(Util.getUser().getUserUID()) || comment.getAuthorsUID().equals(Util.getUser().getUserUID()) && !Util.getUser().isAdmin()) {
                holder.ratingBar.setIsIndicator(true);
            } else {
                holder.ratingBar.setIsIndicator(false);
            }
            Util.mUserDatabaseRef.child(comment.getAuthorsUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        holder.userName.setText(user.getUserName());
                        if (user.getPreferences().isShowPhoto()) {
                            Glide.with(context)
                                    .load(comment.getUserPhotoURL())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(holder.photo);
                        }
                        if (user.isExtra()) {
                            LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
                            stars.getDrawable(2).setColorFilter(Color.parseColor("#AFC2D5"), PorterDuff.Mode.SRC_ATOP);
                            stars.getDrawable(1).setColorFilter(Color.parseColor("#2B4162"), PorterDuff.Mode.SRC_ATOP);
                            stars.getDrawable(0).setColorFilter(Color.parseColor("#2B4162"), PorterDuff.Mode.SRC_ATOP);
                            holder.bg.setBackgroundResource(R.drawable.rounded_accent_double);
                            holder.commentLayout.setBackgroundResource(R.drawable.rounded_accent_double);
                        } else if (comment.isAGroup()) {
                            holder.commentLayout.setBackgroundResource(R.drawable.rounded_primary_double);
                            holder.bg.setBackgroundResource(R.drawable.rounded_primary_double);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (elements.get(position) instanceof Comment) {
                    if (!((Comment) elements.get(position)).getAlreadyRatedList().contains(Util.getUser().getUserUID()) && !((Comment) elements.get(position)).getAuthorsUID().equals(Util.getUser().getUserUID())) {
                        openRatingDialog(rating, position);
                        holder.ratingTV.setText(String.valueOf(rating));
                        ratingBar.setRating(rating);
                        Toast.makeText(context, String.valueOf(rating), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, String.valueOf(rating), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.ratingBar.setOnClickListener(v -> {
                if (holder.ratingBar.isIndicator()) {
                    Toast.makeText(context, "Você já avaliou este comentário!", Toast.LENGTH_SHORT).show();
                }
            });

            holder.text.setOnClickListener(v -> goToGroup(comment));
            holder.entrance.setOnClickListener(v -> goToGroup(comment));
            holder.report.setOnClickListener(v -> tryToReport(comment));
            holder.text.setOnLongClickListener(v -> {
                if (elements.get(position) instanceof Comment) {
                    stickersDialog(position);
                }
                return true;
            });
        }
    }

    private void stickersDialog(int position) {
        DialogStickers cdd = new DialogStickers(activity, Util.getUser().getProducts() != null ? new ArrayList<>(Util.getUser().getProducts().values()) : new ArrayList<>(), null, false, (Comment) elements.get(position));
        cdd.show();
    }

    private void tryToReport(Comment comment) {
        Query query = Util.mDatabaseRef.child("report").orderByChild("userSenderUID").equalTo(Util.getUser().getUserUID());
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (comment.getAuthorsUID().equals(Util.getUser().getUserUID())) {
                    Toast.makeText(context, context.getString(R.string.cant_report_own_comment), Toast.LENGTH_SHORT).show();
                } else if (dataSnapshot.hasChildren()) {
                    boolean found = false;
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Report report = snap.getValue(Report.class);
                        if (report != null) {
                            if (report.getCommentUID().equals(comment.getCommentUID())) {
                                found = true;
                                Toast.makeText(context, context.getString(R.string.already_reported_this_comment), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (!found) {
                        query.removeEventListener(this);
                        openReportDialog(comment);
                    }
                } else {
                    query.removeEventListener(this);
                    openReportDialog(comment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(listener);
    }

    private void openReportDialog(Comment comment) {
        DialogReport dialogReport = new DialogReport(activity, comment);
        if (dialogReport.getWindow() != null) {
            dialogReport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogReport.show();
        }
    }

    private void goToGroup(Comment comment) {
        if ((comment.isAGroup() && comment.getGroup() != null)) {
            if (comment.getGroup().isReady() || Util.getUser().getUserUID().equals(comment.getAuthorsUID())) {
                if (!comment.getGroup().getTempInfo().isFull() || Util.getUser().getUserUID().equals(comment.getAuthorsUID()) || Util.getUser().isExtra()) {
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
            } else {
                Toast.makeText(context, "O dono ainda não ativou o grupo, Aguarde!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerViewItem = elements.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return NATIVE_EXPRESS_AD_VIEW_TYPE;
        }
        return COMMENT_ITEM_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        private ReadMoreTextView text;
        private TextView ratingTV;
        private ImageView photo;
        private MaterialRatingBar ratingBar;
        private LinearLayout commentLayout;
        private LinearLayout bg;
        private TextView userName;
        private ImageView entrance;
        private LinearLayout userProfile;
        private ImageView report;
        private RecyclerView stickers;
        private LinearLayout stickersLayout;

        CommentViewHolder(View rowView) {
            super(rowView);
            text = rowView.findViewById(R.id.text);
            ratingTV = rowView.findViewById(R.id.ratingTV);
            photo = rowView.findViewById(R.id.photo);
            ratingBar = rowView.findViewById(R.id.ratingBar);
            bg = rowView.findViewById(R.id.background);
            commentLayout = rowView.findViewById(R.id.commentLayout);
            userName = rowView.findViewById(R.id.userName);
            entrance = rowView.findViewById(R.id.entrance);
            userProfile = rowView.findViewById(R.id.userProfile);
            report = rowView.findViewById(R.id.report);
            stickers = rowView.findViewById(R.id.stickers);
            stickersLayout = rowView.findViewById(R.id.stickersLayout);
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

    void addAll(List<Comment> newComments, boolean isFirst, boolean newComment, boolean reset) {
        if (reset) {
            elements.clear();
            elements.addAll(newComments);
            notifyDataSetChanged();
        } else {
            if (newComment) {
                if (newComments.size() > 0) {
                    elements.add(newComments.get(0));
                    notifyItemRangeInserted(elements.size(), 1);
                }
            } else {
                if (isFirst) {
                    elements.addAll(newComments);
                    notifyItemRangeInserted(0, newComments.size());
                } else {
                    if (newComments.size() > mPostsPerPage) {
                        for (int i = newComments.size() - 1; i > 0; i--) {
                            elements.add(0, newComments.get(i));
                        }
                        notifyItemRangeInserted(0, newComments.size() - 1);
                    } else {
                        for (int i = newComments.size() - 1; i >= 0; i--) {
                            elements.add(0, newComments.get(i));
                        }
                        notifyItemRangeInserted(0, newComments.size());
                    }
                }
            }
        }
    }

    void addAllAds(List<UnifiedNativeAd> newAds) {
        int adsCount = 0;
        int count = 0;
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i) instanceof Comment) {
                count++;
            }
            if (count == 3 && adsCount <= newAds.size()) {
                if (elements.size() >= (i + 1)) {
                    elements.add(i + 1, newAds.get(adsCount));
                    adsCount++;
                    count = 0;
                }
            }
        }
        notifyDataSetChanged();
    }

    String getLastItemId(boolean isFirst) {
        if (elements.size() > 0) {
            if (isFirst) {
                Comment comment = (Comment) elements.get(0);
                return comment.getCommentUID();
            } else {
                Comment comment = (Comment) elements.get(0);
                return comment.getCommentUID();
            }
        } else {
            return null;
        }
    }

    Boolean commentExists(String key) {
        for (Object obj : elements) {
            if (obj instanceof Comment) {
                Comment comment = (Comment) obj;
                if (comment.getCommentUID() == null) {
                    return false;
                }
                if (comment.getCommentUID().equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    float getLastRate() {
        Comment comment = (Comment) elements.get(0);
        return comment.getRating();
    }
}