package com.relyon.whib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

import static java.util.Objects.requireNonNull;

public class TimelineActivity extends AppCompatActivity {

    private User user;
    private ImageView menuIcon;
    private LinearLayout leaveCommentLayout;
    private TextView emptyList;
    private String subject;
    private RecyclerView rvComments;
    private LinearLayoutManager layoutManager;
    private AppCompatActivity activity;
    private Spinner spinner;
    private int number_of_ads = 0;
    private List<UnifiedNativeAd> nativeAdsList = new ArrayList<>();
    private RecyclerViewCommentAdapter commentAdapter;
    private boolean mIsLoading = false;
    private boolean resetTimeline = false;
    private FancyShowCaseQueue queue = new FancyShowCaseQueue();
    private Query query;
    private DatabaseReference commentListReference;
    private boolean canLoadNewComments = false;

    int menuWidth;
    int menuHeight;
    float menuX;
    float menuY;

    int commentWidth;
    int commentHeight;
    float commentX;
    float commentY;

    int spinnerWidth;
    int spinnerHeight;
    float spinnerX;
    float spinnerY;

    int firstCommentWidth;
    int firstCommentHeight;
    float firstCommentX;
    float firstCommentY;

    public TimelineActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        activity = this;

        commentListReference = Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child(Constants.DATABASE_REF_SERVERS).child(Util.getServer().getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST);
        initRecyclerViewComment();
        retrieveUser();
        verifyServerIsActive();

        final ImageView back = findViewById(R.id.back);
        TextView subject = findViewById(R.id.subject);
        final ImageButton commentBt = findViewById(R.id.commentIcon);
        leaveCommentLayout = findViewById(R.id.leaveCommentLayout);
        menuIcon = findViewById(R.id.menu);
        rvComments = findViewById(R.id.rv_comments);
        emptyList = findViewById(R.id.emptyList);
        spinner = findViewById(R.id.filters);

        this.subject = Util.getServer().getSubject();
        if (this.subject != null) {
            subject.setText(Util.getServer().getSubject());
        }

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.comment_filters, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resetTimeline = true;
                setQuery(position);
                retrieveCommentList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (canLoadNewComments) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        comment.setCommentUID(snapshot.getKey());
                        commentAdapter.addComment(comment);
                    }
                    if (emptyList.getVisibility() == View.VISIBLE) {
                        emptyList.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (!mIsLoading) {
                        setQuery(spinner.getSelectedItemPosition());
                        retrieveCommentList();
                    }
                }
            }
        });

        menuIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(TimelineActivity.this, menuIcon);
            popup.getMenuInflater()
                    .inflate(R.menu.menu_timeline, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (user.isFirstTime()) {
                    Util.mUserDatabaseRef.child(user.getUserUID()).child(Constants.DATABASE_REF_FIRST_TIME).setValue(false);
                }
                if (item.getTitle().equals(getString(R.string.settings))) {
                    Intent intent = new Intent(this, SettingsActivity.class).putExtra(Constants.SHOW_LAST_WARN, user.isFirstTime());
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(getString(R.string.store))) {
                    Intent intent = new Intent(this, StoreActivity.class).putExtra(Constants.SHOW_LAST_WARN, user.isFirstTime());
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(getString(R.string.profile))) {
                    Intent intent = new Intent(this, ProfileActivity.class).putExtra(Constants.SHOW_LAST_WARN, user.isFirstTime());
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(getString(R.string.tips))) {
                    Intent intent = new Intent(this, TipsActivity.class).putExtra(Constants.CAME_FROM_TIMELINE, true).putExtra(Constants.SHOW_LAST_WARN, user.isFirstTime());
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(getString(R.string.about))) {
                    Intent intent = new Intent(this, AboutActivity.class).putExtra(Constants.SHOW_LAST_WARN, user.isFirstTime());
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        back.setOnClickListener(v -> onBackPressed());

        commentBt.setOnClickListener(v -> openCommentBox());

        leaveCommentLayout.setOnClickListener(v -> openCommentBox());
    }

    private void setQuery(int position) {
        if (position == 0) {
            if (commentAdapter.getLastItemId() != null) {
                query = commentListReference.orderByKey().endAt(commentAdapter.getLastItemId()).limitToLast(commentAdapter.mPostsPerPage);
            } else {
                query = commentListReference.orderByKey().limitToLast(commentAdapter.mPostsPerPage);
            }
        } else if (position == 1) {
            query = commentListReference.orderByChild(Constants.DATABASE_REF_RATING)/*.endAt(commentAdapter.getLastRate())*/.limitToLast(commentAdapter.mPostsPerPage + 2);
        } else if (position == 2) {
            query = commentListReference.orderByChild(Constants.DATABASE_REF_A_GROUP).equalTo(true).limitToLast(commentAdapter.mPostsPerPage + 2);
        }
    }

    private void initializeAds() {
        if (!user.isFirstTime() && !user.isExtra()) {
            List<String> testDeviceIds = Collections.singletonList(Constants.TEST_DEVICE_ID);
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
            MobileAds.initialize(this,
                    Constants.ADMOB_APP_ID);
            if (!user.isFirstTime() && !user.isExtra()) {
                new Thread(this::loadNativeAds).start();
            }
        }
    }

    private void initRecyclerViewComment() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        rvComments = findViewById(R.id.rv_comments);
        rvComments.setLayoutManager(layoutManager);
        commentAdapter = new RecyclerViewCommentAdapter(this, activity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvComments.getContext(), layoutManager.getOrientation());
        rvComments.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(requireNonNull(ContextCompat.getDrawable(this, R.drawable.divider)));
        rvComments.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
        query = commentListReference.orderByKey().limitToLast(commentAdapter.mPostsPerPage);
    }

    private void retrieveCommentList() {
        mIsLoading = true;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            Comment comment = snap.getValue(Comment.class);
                            if (comment != null && !commentAdapter.commentExists(snap.getKey())) {
                                comment.setCommentUID(snap.getKey());
                                comments.add(comment);
                            }
                        }
                    }
                    mIsLoading = false;
                } else {
                    if (user.isFirstTime()) {
                        startActivity(new Intent(activity, MainActivity.class).putExtra(Constants.SERVER_EMPTY, true));
                    }
                    mIsLoading = false;
                }

                commentAdapter.addAllComments(comments, resetTimeline);

                resetTimeline = false;
                mIsLoading = false;
                number_of_ads = comments.size() / 3;
                new Thread(() -> loadNativeAds()).start();
                canLoadNewComments = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }

    private void loadNativeAds() {
        if (!user.isFirstTime() && !user.isExtra()) {
            nativeAdsList.clear();
            AdLoader.Builder builder = new AdLoader.Builder(this, Constants.AD_UNIT_ID);
            // The AdLoader used to load ads.
            AdLoader adLoader = builder.forUnifiedNativeAd(
                    unifiedNativeAd -> nativeAdsList.add(unifiedNativeAd)).withAdListener(
                    new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            Log.e(getString(R.string.error), loadAdError.getMessage());
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (nativeAdsList.size() == number_of_ads && !user.isFirstTime()) {
                                insertAdsInTimeline();
                            }
                        }
                    }).build();

            // Load the Native Express ad.
            try {
                Thread.sleep(3000);
                adLoader.loadAds(new AdRequest.Builder().build(), number_of_ads);
            } catch (InterruptedException e) {
                Log.d(getString(R.string.error), requireNonNull(e.getMessage()));
            }
        }
    }

    private void insertAdsInTimeline() {
        commentAdapter.addAllAds(nativeAdsList);
    }

    private void openCommentBox() {
        DialogPostComment cdd = new DialogPostComment(this, subject);
        cdd.show();
    }

    private void callTour() {
        if (user.isFirstTime()) {
            final FancyShowCaseView fancyShowCaseView = new FancyShowCaseView.Builder(this).
                    customView(R.layout.custom_tour_timeline_comment, view -> view.findViewById(R.id.skipTutorial).setOnClickListener(v -> Util.mUserDatabaseRef.child(user.getUserUID()).child(Constants.DATABASE_REF_FIRST_TIME).setValue(false))).focusBorderSize(10)
                    .focusRectAtPosition((int) firstCommentX + (firstCommentWidth / 2), (int) firstCommentY + (firstCommentHeight / 4), firstCommentWidth, firstCommentHeight)
                    .build();

            final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(this).
                    customView(R.layout.custom_tour_timeline_make_comment, view -> {

                    }).focusBorderSize(10)
                    .focusRectAtPosition((int) commentX + (commentWidth / 2), (int) commentY, commentWidth, commentHeight)
                    .build();

            final FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(this).
                    customView(R.layout.custom_tour_timeline_filter, view -> {
                        if (spinner.isShown()) {
                            spinner.performClick();
                        }
                    }).focusBorderSize(10)
                    .focusRectAtPosition((int) spinnerX + (spinnerWidth / 2), (int) spinnerY - (spinnerHeight / 2), spinnerWidth, spinnerHeight)
                    .build();

            final FancyShowCaseView fancyShowCaseView4 = new FancyShowCaseView.Builder(this).
                    customView(R.layout.custom_tour_timeline_menu, view -> {
                    }).focusBorderSize(10)
                    .focusRectAtPosition((int) menuX + (menuWidth / 2), (int) menuY, menuWidth, menuHeight)
                    .build();

            FancyShowCaseView fancyShowCaseView5 = new FancyShowCaseView.Builder(this).
                    customView(R.layout.custom_tour_timeline_menu_opened, view -> {
                        if (menuIcon.isShown()) {
                            menuIcon.performClick();
                        }
                    }).focusBorderSize(10)
                    .focusRectAtPosition(750, (int) (menuY + (menuHeight * 3.25)), 700, 850)
                    .build();

            queue.add(fancyShowCaseView);
            queue.add(fancyShowCaseView2);
            queue.add(fancyShowCaseView3);
            queue.add(fancyShowCaseView4);
            queue.add(fancyShowCaseView5);
            queue.setCompleteListener(() -> {
                Util.mUserDatabaseRef.child(user.getUserUID()).child(Constants.DATABASE_REF_FIRST_TIME).setValue(false);
                DialogFinalWarn warn = new DialogFinalWarn(activity);
                warn.show();
            });
            queue.show();
        }
    }

    //Called after elements dimensions are calculated
    private BroadcastReceiver layoutElementsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (user.isFirstTime()) {
                callTour();
                unregisterReceiver(layoutElementsReceiver);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Util.getServer() != null) {
            decreaseServerNumberOfUsers();
        }
        Util.setServer(null);
        Util.mUserDatabaseRef.child(user.getUserUID()).child(Constants.DATABASE_REF_TEMP_INFO).child(Constants.CURRENT_SERVER).setValue(null);
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        increaseServerNumberOfUsers();
    }

    private void retrieveUser() {
        Util.mUserDatabaseRef.child(Util.fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    initializeAds();
                    calculateElementsDimensions();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void increaseServerNumberOfUsers() {
        Util.getServer().getTempInfo().setQtdUsers(Util.getServer().getTempInfo().getQtdUsers() + 1);
        Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child(Constants.DATABASE_REF_SERVERS).child(Util.getServer().getServerUID()).child(Constants.DATABASE_REF_TEMP_INFO).setValue(Util.getServer().getTempInfo());
    }

    private void calculateElementsDimensions() {
        if (user.isFirstTime()) {
            leaveCommentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    leaveCommentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    commentHeight = leaveCommentLayout.getHeight();
                    commentWidth = leaveCommentLayout.getWidth();
                    int[] location = new int[2];
                    leaveCommentLayout.getLocationOnScreen(location);
                    commentX = location[0];
                    commentY = location[1];

                    spinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            spinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            spinnerHeight = spinner.getHeight();
                            spinnerWidth = spinner.getWidth();
                            int[] location = new int[2];
                            spinner.getLocationOnScreen(location);
                            spinnerX = location[0];
                            spinnerY = location[1];

                            menuIcon.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    menuIcon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    menuHeight = menuIcon.getHeight();
                                    menuWidth = menuIcon.getWidth();
                                    int[] location = new int[2];
                                    menuIcon.getLocationOnScreen(location);
                                    menuX = location[0];
                                    menuY = location[1];

                                    layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition()).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition()).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                            firstCommentHeight = layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition()).getHeight();
                                            firstCommentWidth = layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition()).getWidth();
                                            int[] location = new int[2];
                                            layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition()).getLocationOnScreen(location);
                                            firstCommentX = location[0];
                                            firstCommentY = location[1];
                                            IntentFilter intentFilter = new IntentFilter();
                                            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                                            registerReceiver(layoutElementsReceiver, intentFilter);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void verifyServerIsActive() {
        if (Util.getServer() != null) {
            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child(Constants.DATABASE_REF_SERVERS).child(Util.getServer().getServerUID()).child(Constants.DATABASE_REF_ACTIVATED).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean active = dataSnapshot.getValue(Boolean.class);
                    if (active != null && !active) {
                        Toast.makeText(activity, R.string.full_server, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(activity, MainActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        decreaseServerNumberOfUsers();
    }

    private void decreaseServerNumberOfUsers() {
        if (Util.getServer() != null) {
            Util.getServer().getTempInfo().setQtdUsers(Util.getServer().getTempInfo().getQtdUsers() - 1);
            Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child(Constants.DATABASE_REF_SERVERS).child(Util.getServer().getServerUID()).child(Constants.DATABASE_REF_TEMP_INFO).setValue(Util.getServer().getTempInfo());
        }
    }
}