package com.relyon.whib.activity;

import android.app.Activity;
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
import android.widget.ListView;
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
import com.relyon.whib.R;
import com.relyon.whib.adapter.RecyclerViewCommentAdapter;
import com.relyon.whib.dialog.DialogFinalWarn;
import com.relyon.whib.dialog.DialogPostComment;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

import static java.util.Objects.requireNonNull;

public class TimelineActivity extends AppCompatActivity {

    private AppCompatActivity activity;
    private User user;
    private List<UnifiedNativeAd> nativeAdsList;
    private FancyShowCaseQueue queue = new FancyShowCaseQueue();
    private Query commentQuery;
    private DatabaseReference commentListReference;
    private boolean mIsLoading = false;
    private boolean resetTimeline = false;
    private boolean canLoadNewComments = false;
    private int number_of_ads = 0;
    private PopupMenu popupMenu;

    private RecyclerViewCommentAdapter commentAdapter;
    private ImageView menuIcon;
    private LinearLayout leaveCommentLayout;
    private TextView emptyList;
    private String subject;
    private RecyclerView rvComments;
    private LinearLayoutManager layoutManager;
    private Spinner spinnerFilter;
    private ImageView back;
    private TextView tvSubject;
    private ImageButton commentBt;

    private int menuWidth;
    private int menuHeight;
    private float menuX;
    private float menuY;

    private int expandedMenuWidth;
    private int expandedMenuHeight;

    private int commentWidth;
    private int commentHeight;
    private float commentX;
    private float commentY;

    private int spinnerWidth;
    private int spinnerHeight;
    private float spinnerX;
    private float spinnerY;

    private int firstCommentWidth;
    private int firstCommentHeight;
    private float firstCommentX;
    private float firstCommentY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        activity = this;

        commentListReference = Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(Util.getServer().getSubject()).child(Constants.DATABASE_REF_SERVERS).child(Util.getServer().getServerUID()).child(Constants.DATABASE_REF_TIMELINE).child(Constants.DATABASE_REF_COMMENT_LIST);

        setLayoutAttributes();
        initPopUpMenu();
        initRecyclerViewComment();
        retrieveUser();
        verifyServerIsActive();

        this.subject = Util.getServer().getSubject();
        if (this.subject != null) {
            tvSubject.setText(Util.getServer().getSubject());
        }

        setupSpinnerFilter();

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spinnerSelection, long id) {
                resetTimeline = true;
                setCommentQuery(spinnerSelection);
                retrieveCommentList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        commentQuery.addChildEventListener(new ChildEventListener() {
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
                        setCommentQuery(spinnerFilter.getSelectedItemPosition());
                        retrieveCommentList();
                    }
                }
            }
        });

        menuIcon.setOnClickListener(v -> {
            popupMenu.setOnMenuItemClickListener(item -> {
                if (user.isFirstTime()) {
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(user.getUserUID()).child(Constants.DATABASE_REF_FIRST_TIME).setValue(false);
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
            popupMenu.show();
        });

        back.setOnClickListener(v -> onBackPressed());
        commentBt.setOnClickListener(v -> openCommentBox(activity));
        leaveCommentLayout.setOnClickListener(v -> openCommentBox(activity));
    }

    private void initPopUpMenu() {
        popupMenu = new PopupMenu(TimelineActivity.this, menuIcon);
        popupMenu.getMenuInflater().inflate(R.menu.menu_timeline, popupMenu.getMenu());
    }

    private void preparePopupMenuDimensionsForWelcomeTour() {
        initPopUpMenu();
        popupMenu.show();
        ListView listView = getPopupMenuListView(popupMenu);
        if (listView != null) {
            listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    expandedMenuWidth = listView.getWidth();
                    expandedMenuHeight = listView.getHeight();
                    popupMenu.dismiss();
                    callTour();
                }
            });
        }
    }

    private ListView getPopupMenuListView(PopupMenu popupMenu) {
        Method getMenuListViewMethod = null;
        try {
            getMenuListViewMethod = PopupMenu.class.getDeclaredMethod("getMenuListView");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        ListView listView = null;
        if (getMenuListViewMethod != null) {
            getMenuListViewMethod.setAccessible(true);
            try {
                listView = (ListView) getMenuListViewMethod.invoke(popupMenu);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return listView;
    }

    private void setLayoutAttributes() {
        rvComments = findViewById(R.id.rv_comments);
        back = findViewById(R.id.back);
        tvSubject = findViewById(R.id.subject);
        commentBt = findViewById(R.id.commentIcon);
        leaveCommentLayout = findViewById(R.id.leaveCommentLayout);
        menuIcon = findViewById(R.id.menu);
        rvComments = findViewById(R.id.rv_comments);
        emptyList = findViewById(R.id.emptyList);
        spinnerFilter = findViewById(R.id.filters);
    }

    private void setupSpinnerFilter() {
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.comment_filters, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);
    }

    private void setCommentQuery(int spinnerSelection) {
        resetTimeline();
        if (spinnerSelection == 0) {
            if (commentAdapter.getLastItemId() != null) {
                commentQuery = commentListReference.orderByKey().endAt(commentAdapter.getLastItemId()).limitToLast(commentAdapter.mPostsPerPage);
            } else {
                commentQuery = commentListReference.orderByKey().limitToLast(commentAdapter.mPostsPerPage);
            }
        } else if (spinnerSelection == 1) {
            if (resetTimeline) {
                commentQuery = commentListReference.orderByChild(Constants.DATABASE_REF_RATING).startAt(commentAdapter.getLastRate()).limitToLast(commentAdapter.mPostsPerPage + 2);
            } else {
                if (commentAdapter.getLastRate() == 0) {
                    commentQuery = commentListReference.orderByKey().endAt(commentAdapter.getLastItemId()).limitToLast(commentAdapter.mPostsPerPage);
                } else {
                    commentQuery = commentListReference.orderByChild(Constants.DATABASE_REF_RATING).endAt(commentAdapter.getLastRate()).limitToLast(commentAdapter.mPostsPerPage + 2);
                }
            }
        } else if (spinnerSelection == 2) {
            commentQuery = commentListReference.orderByChild(Constants.DATABASE_REF_A_GROUP).equalTo(true).limitToLast(commentAdapter.mPostsPerPage + 2);
        }
    }

    private void resetTimeline() {
        if (resetTimeline) {
            commentAdapter.resetTimeline();
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
        }
    }

    private void initRecyclerViewComment() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        rvComments.setLayoutManager(layoutManager);
        commentAdapter = new RecyclerViewCommentAdapter(this, activity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvComments.getContext(), layoutManager.getOrientation());
        rvComments.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(requireNonNull(ContextCompat.getDrawable(this, R.drawable.divider)));
        rvComments.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
        commentQuery = commentListReference.orderByKey().limitToLast(commentAdapter.mPostsPerPage);
    }

    private void retrieveCommentList() {
        mIsLoading = true;

        commentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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
                } else {
                    if (user.isFirstTime()) {
                        startActivity(new Intent(activity, MainActivity.class).putExtra(Constants.SERVER_EMPTY, true));
                    }
                }

                commentAdapter.addAllComments(comments, resetTimeline);
                mIsLoading = false;
                resetTimeline = false;
                number_of_ads = comments.size() / Constants.COMMENTS_BY_ADD;
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
        nativeAdsList = new ArrayList<>();
        if (!user.isFirstTime() && !user.isExtra()) {
            nativeAdsList.clear();
            AdLoader.Builder builder = new AdLoader.Builder(this, Constants.AD_UNIT_ID);
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

    private void openCommentBox(Activity activity) {
        commentListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentListReference.removeEventListener(this);
                if (snapshot.getChildrenCount() < 100) {
                    DialogPostComment cdd = new DialogPostComment(activity, subject);
                    cdd.show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.server_full_choose_another, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void callTour() {
        if (user.isFirstTime()) {
            final FancyShowCaseView fancyShowCaseView = new FancyShowCaseView.Builder(this).
                    customView(R.layout.custom_tour_timeline_first_comment, view -> view.findViewById(R.id.skipTutorial).setOnClickListener(v -> Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(user.getUserUID()).child(Constants.DATABASE_REF_FIRST_TIME).setValue(false))).focusBorderSize(10)
                    .focusRectAtPosition((int) firstCommentX + (firstCommentWidth / 2), (int) firstCommentY + (firstCommentHeight / 4), firstCommentWidth, firstCommentHeight)
                    .build();

            final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(this).
                    customView(R.layout.custom_tour_timeline_make_comment, view -> {

                    }).focusBorderSize(10)
                    .focusRectAtPosition((int) commentX + (commentWidth / 2), (int) commentY, commentWidth, commentHeight)
                    .build();

            final FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(this).
                    customView(R.layout.custom_tour_timeline_filter, view -> {
                        if (spinnerFilter.isShown()) {
                            spinnerFilter.performClick();
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
                    }).focusBorderSize(10).build();

            queue.add(fancyShowCaseView);
            queue.add(fancyShowCaseView2);
            queue.add(fancyShowCaseView3);
            queue.add(fancyShowCaseView4);
            queue.add(fancyShowCaseView5);
            queue.setCompleteListener(() -> {
                Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(user.getUserUID()).child(Constants.DATABASE_REF_FIRST_TIME).setValue(false);
                DialogFinalWarn warn = new DialogFinalWarn(activity);
                warn.show();
            });
            queue.show();
        }
    }

    //Called after elements dimensions are calculated
    private BroadcastReceiver layoutElementsDimensionsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (user.isFirstTime()) {
                preparePopupMenuDimensionsForWelcomeTour();
                unregisterReceiver(layoutElementsDimensionsReceiver);
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
        Util.setServer(null);
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(user.getUserUID()).child(Constants.DATABASE_REF_TEMP_INFO).child(Constants.CURRENT_SERVER).setValue(null);
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void retrieveUser() {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(Util.fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    spinnerFilter.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            spinnerFilter.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            spinnerHeight = spinnerFilter.getHeight();
                            spinnerWidth = spinnerFilter.getWidth();
                            int[] location = new int[2];
                            spinnerFilter.getLocationOnScreen(location);
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
                                            registerReceiver(layoutElementsDimensionsReceiver, intentFilter);
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
            Util.mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT).child(Util.getServer().getSubject()).child(Constants.DATABASE_REF_SERVERS).child(Util.getServer().getServerUID()).child(Constants.DATABASE_REF_ACTIVATED).addValueEventListener(new ValueEventListener() {
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
}