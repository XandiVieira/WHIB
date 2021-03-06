package com.relyon.whib.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.activity.GroupActivity;
import com.relyon.whib.activity.ProfileActivity;
import com.relyon.whib.dialog.DialogRateComment;
import com.relyon.whib.dialog.DialogReport;
import com.relyon.whib.dialog.DialogStickers;
import com.relyon.whib.dialog.DialogWarnGroupFull;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.Report;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private ArrayList<Object> elements;
    private AppCompatActivity activity;
    private static final int COMMENT_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;
    public int mPostsPerPage = 10;
    private boolean calledFromTabHistory = false;
    private boolean calledFromGroup = false;

    public RecyclerViewCommentAdapter(@NonNull Context context, AppCompatActivity activity) {
        this.context = context;
        this.elements = new ArrayList<>();
        this.activity = activity;
    }

    public RecyclerViewCommentAdapter(@NonNull Context context, AppCompatActivity activity, boolean calledFromTabHistory, boolean calledFromGroup) {
        this.context = context;
        this.elements = new ArrayList<>();
        this.activity = activity;
        this.calledFromTabHistory = calledFromTabHistory;
        this.calledFromGroup = calledFromGroup;
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
            default:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_comment, viewGroup, false);
                return new CommentViewHolder(menuItemLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        int viewType = getItemViewType(position);

        if (viewType == NATIVE_EXPRESS_AD_VIEW_TYPE) {
            UnifiedNativeAd nativeAd = (UnifiedNativeAd) elements.get(position);
            populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder1).getAdView());
        } else {
            CommentViewHolder holder = (CommentViewHolder) holder1;
            final Comment comment = (Comment) elements.get(position);

            retrieveCommentStickers(comment, holder);
            handleRating(comment, holder, position);
            handleText(comment, holder);
            handleUserData(comment, holder);

            if (!calledFromTabHistory) {
                holder.userProfile.setOnClickListener(v -> context.startActivity(new Intent(context, ProfileActivity.class).putExtra(Constants.USER_ID, comment.getAuthorsUID()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));

                RatingBar.OnRatingBarChangeListener ratingBarChangeListener = (ratingBar, rating, fromTouch) -> {
                    if (fromTouch) {
                        if (!comment.getAlreadyRatedList().contains(Util.getUser().getUserUID()) && !comment.getAuthorsUID().equals(Util.getUser().getUserUID())) {
                            openRatingDialog(rating, holder.getAdapterPosition());
                            holder.ratingTV.setText(String.valueOf(rating));
                            ratingBar.setRating(rating);
                        }
                        Toast.makeText(context, String.valueOf(rating), Toast.LENGTH_SHORT).show();
                    }
                };
                holder.ratingBar.setOnRatingBarChangeListener(ratingBarChangeListener);
                holder.ratingBar.setOnClickListener(v -> {
                    if (holder.ratingBar.isIndicator()) {
                        Toast.makeText(context, R.string.already_valuated_this_comment, Toast.LENGTH_SHORT).show();
                    }
                });
                holder.text.setOnLongClickListener(v -> {
                    showStickersDialog(holder.getAdapterPosition());
                    return true;
                });
            } else {
                holder.ratingBar.setIsIndicator(true);
            }

            holder.entrance.setOnClickListener(v -> goToGroup(comment));
            holder.report.setOnClickListener(v -> tryToReport(comment));
        }
    }

    private void handleUserData(Comment comment, CommentViewHolder holder) {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(comment.getAuthorsUID()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    } else {
                        holder.photo.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_no_pic));
                    }
                    if (calledFromGroup || Util.getUser().getUserUID().equals(comment.getAuthorsUID())) {
                        holder.report.setVisibility(View.INVISIBLE);
                    } else {
                        holder.report.setVisibility(View.VISIBLE);
                    }
                    handleGroupIndication(comment, holder, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleGroupIndication(Comment comment, CommentViewHolder holder, User user) {
        if (comment.isAGroup() && !calledFromGroup) {
            holder.entrance.setVisibility(View.VISIBLE);
            if (comment.getGroup().isReady()) {
                holder.entrance.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_exit_active, null));
            }
            holder.ratingTV.setTextSize(14);
        } else {
            holder.entrance.setVisibility(View.GONE);
        }
        if (user.isExtra()) {
            setStarsColor(holder.ratingBar, "#AFC2D5", "#2B4162");
            holder.bg.setBackgroundResource(R.drawable.rounded_accent_double);
            holder.commentLayout.setBackgroundResource(R.drawable.rounded_accent_double);
        } else if (comment.isAGroup()) {
            setStarsColor(holder.ratingBar, "#FFBD4A", "#FF9800");
            holder.commentLayout.setBackgroundResource(R.drawable.rounded_primary_double);
            holder.bg.setBackgroundResource(R.drawable.rounded_primary_double);
        } else {
            setStarsColor(holder.ratingBar, "#FFBD4A", "#FF9800");
            holder.commentLayout.setBackgroundResource(R.drawable.rounded_white);
            holder.bg.setBackgroundResource(R.drawable.rounded_white);
        }
    }

    private void setStarsColor(MaterialRatingBar ratingBar, String color1, String color2) {
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor(color1), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor(color2), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.parseColor(color2), PorterDuff.Mode.SRC_ATOP);
    }

    private void handleText(Comment comment, CommentViewHolder holder) {
        String commentText = comment.getText();
        if (commentText.length() > 270) {
            addReadMore(commentText, holder.text);
        } else {
            holder.text.setText(commentText);
            Typeface face = ResourcesCompat.getFont(context, R.font.baloo);
            holder.text.setTypeface(face);
        }
    }

    private void addReadMore(final String text, final TextView textView) {
        SpannableString ss = new SpannableString(text.substring(0, 270) + context.getString(R.string.see_more));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                addReadLess(text, textView);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.BLUE);
            }
        };
        Typeface face = ResourcesCompat.getFont(context, R.font.baloo);
        textView.setTypeface(face);
        ss.setSpan(clickableSpan, ss.length() - 10, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void addReadLess(final String text, final TextView textView) {
        SpannableString ss = new SpannableString(text + context.getString(R.string.see_less));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                addReadMore(text, textView);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.BLUE);
            }
        };
        ss.setSpan(clickableSpan, ss.length() - 10, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void handleRating(Comment comment, CommentViewHolder holder, int position) {
        holder.ratingBar.setNumStars(5);
        holder.ratingBar.setStepSize(0.01f);
        if (((Comment) elements.get(position)).getAlreadyRatedList().contains(Util.getUser().getUserUID()) || ((Comment) elements.get(position)).getAuthorsUID().equals(Util.getUser().getUserUID())) {
            holder.ratingBar.setRating(comment.getRating());
            holder.ratingTV.setText(String.format("%.2f", comment.getRating()));
        } else {
            holder.ratingBar.setRating(0);
            holder.ratingTV.setText(String.format("%.2f", (float) 0));
        }
        if (comment.getAlreadyRatedList().contains(Util.getUser().getUserUID()) || comment.getAuthorsUID().equals(Util.getUser().getUserUID()) && !Util.getUser().isAdmin()) {
            holder.ratingBar.setIsIndicator(true);
        } else {
            holder.ratingBar.setIsIndicator(false);
        }
    }

    private void retrieveCommentStickers(Comment comment, CommentViewHolder holder) {
        GridLayoutManager layoutManager = new GridLayoutManager(context, 5);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(comment.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(comment.getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST).child(comment.getCommentUID()).child(Constants.DATABASE_REF_STICKERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Product> stickers = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    stickers.add(snap.getValue(Product.class));
                }
                if (stickers.size() > 0) {
                    RecyclerViewStickerAdapter adapter = new RecyclerViewStickerAdapter(stickers, context, comment);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                            layoutManager.getOrientation());
                    holder.stickers.addItemDecoration(dividerItemDecoration);
                    holder.stickers.setLayoutManager(layoutManager);
                    holder.stickers.setAdapter(adapter);
                    holder.stickers.setVisibility(View.VISIBLE);
                    holder.stickersLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.stickers.setVisibility(View.GONE);
                    holder.stickersLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openRatingDialog(float rating, int position) {
        Comment comment = (Comment) elements.get(position);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(comment.getAuthorsUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(comment.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(comment.getServerUID()).child(Constants.DATABASE_REF_TEMP_INFO).child(Constants.DATABASE_REF_QTD_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DialogRateComment cdd = new DialogRateComment(activity, rating, comment, elements, user.isExtra());
                            if (cdd.getWindow() != null) {
                                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                cdd.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showStickersDialog(int position) {
        Comment comment = (Comment) elements.get(position);
        if (comment != null && !comment.getAuthorsUID().equals(Util.getUser().getUserUID())) {
            List<Product> products = new ArrayList<>();
            if (Util.getUser().getProducts() != null) {
                for (Product product : Util.getUser().getProducts().values()) {
                    if (product != null && product.getQuantity() > 0) {
                        products.add(product);
                    }
                }
            }
            DialogStickers cdd = new DialogStickers(activity, products, null, comment, this, position, true);
            cdd.show();
        } else {
            Toast.makeText(context, R.string.can_not_send_stickers_to_yourself, Toast.LENGTH_SHORT).show();
        }
    }

    private void tryToReport(Comment comment) {
        Query query = Util.mDatabaseRef.child(Constants.DATABASE_REF_REPORT).orderByChild(Constants.DATABASE_REF_USER_SENDER_ID).equalTo(Util.getUser().getUserUID());
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
                    if (Util.getServer() == null) {
                        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(comment.getSubject()).child(comment.getServerUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Util.setServer(snapshot.getValue(Server.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if (calledFromTabHistory) {
                        Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(comment.getSubject()).child(Constants.DATABASE_REF_SERVERS).child(comment.getServerUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Server server = snapshot.getValue(Server.class);
                                if (server != null) {
                                    Util.setServer(server);
                                    Util.setSubject(server.getSubject());
                                    if (comment.getGroup().getUserListUID() != null && !comment.getGroup().getUserListUID().contains(Util.getUser().getUserUID())) {
                                        comment.getGroup().getUserListUID().add(Util.getUser().getUserUID());
                                    }
                                    context.startActivity(new Intent(context, GroupActivity.class).putExtra(Constants.SERVER_ID, comment.getServerUID()).putExtra(Constants.COMMENT_ID, comment.getCommentUID()).putExtra(Constants.CAME_FROM_PROFILE, true).putExtra(Constants.COMMENT_NUMBER, comment.getGroup().getServerNumber()).putExtra(Constants.GROUP_NUMBER, comment.getGroup().getNumber()).putExtra(Constants.SUBJECT, comment.getSubject()).putExtra(Constants.COMMENT_ID, comment.getCommentUID()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    activity.finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        if (comment.getGroup().getUserListUID() != null && !comment.getGroup().getUserListUID().contains(Util.getUser().getUserUID())) {
                            comment.getGroup().getUserListUID().add(Util.getUser().getUserUID());
                        }
                        context.startActivity(new Intent(context, GroupActivity.class).putExtra(Constants.SERVER_ID, Util.getServer().getServerUID()).putExtra(Constants.COMMENT_ID, comment.getCommentUID()).putExtra(Constants.COMMENT_NUMBER, comment.getGroup().getServerNumber()).putExtra(Constants.GROUP_NUMBER, comment.getGroup().getNumber()).putExtra(Constants.SUBJECT, comment.getSubject()).putExtra(Constants.COMMENT_ID, comment.getCommentUID()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        activity.finish();
                    }
                } else {
                    DialogWarnGroupFull warnGroupFull = new DialogWarnGroupFull(activity);
                    warnGroupFull.show();
                }
            } else {
                Toast.makeText(context, R.string.owner_has_not_activated_group_yet, Toast.LENGTH_LONG).show();
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

    private void populateNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

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

        adView.setNativeAd(nativeAd);
    }

    public void addComment(Comment newComment) {
        elements.add(newComment);
        notifyDataSetChanged();
    }

    public void addAllComments(List<Comment> newComments, boolean resetTimeline) {
        if (resetTimeline) {
            resetTimeline();
            elements.addAll(newComments);
            notifyDataSetChanged();
        } else {
            elements.addAll(0, newComments);
            notifyItemRangeInserted(0, newComments.size());
        }
    }

    public void addAllAds(List<UnifiedNativeAd> newAds) {
        int commentsBetweenAds = 0;
        int adsAdded = 0;

        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i) instanceof Comment) {
                commentsBetweenAds++;
            }
            if (commentsBetweenAds == Constants.COMMENTS_BY_ADD) {
                if (adsAdded < newAds.size()) {
                    if (elements.size() >= (i + 1)) {
                        elements.add((i + 1), newAds.get(adsAdded));
                        adsAdded++;
                        notifyItemInserted(i + 1);
                        commentsBetweenAds = 0;
                    }
                }
            }
        }
    }

    public String getLastItemId() {
        if (elements.size() > 0) {
            Comment comment = (Comment) elements.get(0);
            return comment.getCommentUID();
        } else {
            return null;
        }
    }

    public Boolean commentExists(String key) {
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

    public float getLastRate() {
        if (elements != null && elements.size() > 0) {
            Comment comment = (Comment) elements.get(0);
            return comment.getRating();
        } else {
            return 0;
        }
    }

    public void refreshToShowSticker(int position) {
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void resetTimeline() {
        elements.clear();
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
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
            userName = rowView.findViewById(R.id.user_name);
            entrance = rowView.findViewById(R.id.entrance);
            userProfile = rowView.findViewById(R.id.userProfile);
            report = rowView.findViewById(R.id.report);
            stickers = rowView.findViewById(R.id.stickers);
            stickersLayout = rowView.findViewById(R.id.stickersLayout);
        }
    }

    private static class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder {

        private UnifiedNativeAdView adView;

        UnifiedNativeAdView getAdView() {
            return adView;
        }

        UnifiedNativeAdViewHolder(View view) {
            super(view);
            adView = view.findViewById(R.id.ad_view);

            // The MediaView will display a video asset if one is present in the ad, and the
            // first image asset otherwise.
            adView.setMediaView(adView.findViewById(R.id.ad_media));

            // Register the view used for each individual asset.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        }
    }
}