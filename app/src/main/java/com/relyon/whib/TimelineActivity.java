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
import android.widget.ProgressBar;
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
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

import static java.util.Objects.requireNonNull;

public class TimelineActivity extends AppCompatActivity {

    private ImageView menu;
    private LinearLayout leaveCommentLayout;
    private TextView emptyList;
    private Subject subjectObj;
    private RecyclerView rvComments;
    private LinearLayoutManager layoutManager;
    private AppCompatActivity activity;
    private Spinner spinner;
    private int NUMBER_OF_ADS = 0;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private RecyclerViewCommentAdapter adapter;
    private boolean mIsLoading = false;
    private boolean isFirst = true;
    private boolean mayPass = false;
    private boolean reset = false;
    private boolean hasPassed = false;
    private int filter = 0;
    private FancyShowCaseQueue queue = new FancyShowCaseQueue();
    private Query query;
    private ProgressBar progressBar;

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

        if (!Util.getUser().isFirstTime()) {
            List<String> testDeviceIds = Collections.singletonList("3DF6979E4CCB56C2A91510C1A9BCC253");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();

            MobileAds.setRequestConfiguration(configuration);
            MobileAds.initialize(this,
                    getString(R.string.admob_app_id));
        }

        final ImageView back = findViewById(R.id.back);
        TextView subject = findViewById(R.id.subject);
        final ImageButton commentBt = findViewById(R.id.commentIcon);
        leaveCommentLayout = findViewById(R.id.leaveCommentLayout);
        menu = findViewById(R.id.menu);
        rvComments = findViewById(R.id.rvComments);
        emptyList = findViewById(R.id.emptyList);
        spinner = findViewById(R.id.filters);
        progressBar = findViewById(R.id.progressBar);

        activity = this;

        subjectObj = Util.getServer().getSubject();
        if (subjectObj != null) {
            subject.setText(Util.getServer().getSubject().getTitle());
        }

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.comment_filters, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (hasPassed) {
                    if (position == 0) {
                        filter = 0;
                        query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByKey().limitToLast(adapter.mPostsPerPage);
                    } else if (position == 1) {
                        filter = 1;
                        query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByChild("rating").endAt(adapter.getLastRate()).limitToLast(adapter.mPostsPerPage + 2);
                    } else if (position == 2) {
                        filter = 2;
                        query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByChild("agroup").equalTo(true).limitToLast(adapter.mPostsPerPage + 2);
                    }
                    isFirst = true;
                    reset = true;
                    getComments();
                }
                hasPassed = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initRecyclerViewComment();
        getComments();

        rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (!mIsLoading) {
                        getComments();
                    }
                }
            }
        });

        menu.setOnClickListener(v -> {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(TimelineActivity.this, menu);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.menu_timeline, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals(getString(R.string.settings))) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(getString(R.string.store))) {
                    Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(getString(R.string.profile))) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(getString(R.string.tips))) {
                    Intent intent = new Intent(getApplicationContext(), TipsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(getString(R.string.tips))) {
                    Intent intent = new Intent(getApplicationContext(), TipsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(getString(R.string.about))) {
                    Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            popup.show(); //showing popup menu
        }); //closing the setOnClickListener method

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        commentBt.setOnClickListener(v -> {
            openCommentBox();
        });

        leaveCommentLayout.setOnClickListener(v -> openCommentBox());
    }

    private void backToMainScreen() {
        Util.getUser().getTempInfo().setCurrentServer(null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initRecyclerViewComment() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvComments = findViewById(R.id.rvComments);
        layoutManager.setStackFromEnd(true);
        rvComments.setLayoutManager(layoutManager);
        adapter = new RecyclerViewCommentAdapter(getApplicationContext(), activity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvComments.getContext(),
                layoutManager.getOrientation());
        rvComments.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(requireNonNull(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider)));
        rvComments.setAdapter(adapter);
        query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByKey().limitToLast(adapter.mPostsPerPage);

        if (!Util.getUser().isFirstTime()) {
            new Thread(this::loadNativeAds).start();
        }
    }

    private void getComments() {
        mIsLoading = true;
        if (filter == 2) {
            query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByChild("agroup").equalTo(true).limitToLast(adapter.mPostsPerPage + 2);
        } else if (filter == 1) {
            if (isFirst) {
                query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByChild("rating").limitToLast(adapter.mPostsPerPage + 2);
            } else {
                query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByChild("rating").endAt(adapter.getLastRate()).limitToLast(adapter.mPostsPerPage + 2);
            }
        } else {
            if (isFirst) {
                query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByKey().limitToLast(adapter.mPostsPerPage + 2);
            } else {
                query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByKey().endAt(adapter.getLastItemId(false)).limitToLast(adapter.mPostsPerPage + 2);
            }
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            Comment comment = snap.getValue(Comment.class);
                            requireNonNull(comment).setCommentUID(snap.getKey());
                            if (!adapter.commentExists(snap.getKey()) || reset) {
                                comments.add(comment);
                            }
                        }
                    }
                }
                adapter.addAll(comments, adapter.getLastItemId(false) == null, false, reset);
                progressBar.setVisibility(View.GONE);
                reset = false;
                if (comments.size() > 0) {
                    emptyList.setVisibility(View.GONE);
                }
                mIsLoading = false;
                NUMBER_OF_ADS = comments.size() / 3;
                isFirst = false;
                new Thread(() -> loadNativeAds()).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }

    private void loadNativeAds() {
        if (!Util.getUser().isFirstTime()) {
            mNativeAds.clear();
            AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ad_unit_id));
            // The AdLoader used to load ads.
            AdLoader adLoader = builder.forUnifiedNativeAd(
                    unifiedNativeAd -> {
                        mNativeAds.add(unifiedNativeAd);
                    }).withAdListener(
                    new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            Log.e("TimelineActivity", "The previous native ad failed to load. Attempting to"
                                    + " load another.");
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (mNativeAds.size() == NUMBER_OF_ADS && !Util.getUser().isFirstTime()) {
                                insertAdsInMenuItems();
                            }
                        }
                    }).build();

            // Load the Native Express ad.
            try {
                Thread.sleep(3000);
                adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
            } catch (InterruptedException e) {
                Log.d("Catch", e.getMessage());
            }
        }
    }

    private void insertAdsInMenuItems() {
        adapter.addAllAds(mNativeAds);
    }

    private void openCommentBox() {
        boolean canPost = true;
        if (canPost) {
            mayPass = true;
            DialogPostComment cdd = new DialogPostComment(this, subjectObj, menu);
            cdd.show();
        } else {
            Toast.makeText(getApplicationContext(), "Você já postou um comentário neste servidor ou já faz parte de um grupo!", Toast.LENGTH_SHORT).show();
        }
    }

    private void callTour() {
        if (Util.getUser().isFirstTime()) {
            final FancyShowCaseView fancyShowCaseView = new FancyShowCaseView.Builder(this).
                    customView(R.layout.custom_tour_timeline_comment, view -> {
                        view.findViewById(R.id.skipTutorial).setOnClickListener(v -> Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("firstTime").setValue(false));
                    }).focusBorderSize(10)
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
                        if (menu.isShown()) {
                            menu.performClick();
                        }
                    }).focusBorderSize(10)
                    .focusRectAtPosition(750, (int) (menuY + (menuHeight * 3.25)), 700, 850)
                    .build();

            queue.add(fancyShowCaseView);
            queue.add(fancyShowCaseView2);
            queue.add(fancyShowCaseView3);
            queue.add(fancyShowCaseView4);
            queue.add(fancyShowCaseView5);
            queue.setCompleteListener(() -> Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("firstTime").setValue(false));
            queue.show();
        }
    }

    private BroadcastReceiver menuReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Util.getUser().isFirstTime()) {
                callTour();
                unregisterReceiver(menuReceiver);
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("tempInfo").child("currentServer").setValue(null);
        if (Util.getServer() != null) {
            Util.getServer().getTempInfo().setQtdUsers(Util.getServer().getTempInfo().getQtdUsers() - 1);
            Util.getmServerDatabaseRef().child(Util.getServer().getServerUID()).child("tempInfo").setValue(Util.getServer().getTempInfo());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.getmServerDatabaseRef().child(Util.getServer().getServerUID()).child("tempInfo").setValue(Util.getServer().getTempInfo());

        if (Util.getUser().isFirstTime()) {
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

                            menu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    menu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    menuHeight = menu.getHeight();
                                    menuWidth = menu.getWidth();
                                    int[] location = new int[2];
                                    menu.getLocationOnScreen(location);
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
                                            registerReceiver(menuReceiver, intentFilter);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });

            Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (mayPass) {
                        List<Comment> comments = new ArrayList<>();
                        comments.add(dataSnapshot.getValue(Comment.class));
                        comments.get(0).setCommentUID(dataSnapshot.getKey());
                        requireNonNull(comments.get(0)).setCommentUID(dataSnapshot.getKey());
                        adapter.addAll(comments, true, true, false);
                        emptyList.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        if (Util.getServer() != null) {
            Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("activated").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean active = dataSnapshot.getValue(Boolean.class);
                    if (active != null && !active) {
                        Toast.makeText(getApplicationContext(), "Servidor Lotado", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Util.getServer().getTempInfo().setQtdUsers(Util.getServer().getTempInfo().getQtdUsers() + 1);
        Util.getmServerDatabaseRef().child(Util.getServer().getServerUID()).child("tempInfo").setValue(Util.getServer().getTempInfo());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Util.setServer(null);
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}