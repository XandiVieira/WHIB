package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import static java.util.Objects.requireNonNull;

public class TimelineActivity extends AppCompatActivity {

    private ImageView menu;
    private TextView emptyList;
    private Subject subjectObj;
    private RecyclerView rvComments;
    private AppCompatActivity activity;
    private boolean canPost = true;
    private int NUMBER_OF_ADS = 0;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private RecyclerViewCommentAdapter adapter;
    private boolean mIsLoading = false;
    private boolean isFirst = true;
    private boolean mayPass = false;
    private boolean reset = false;
    private boolean hasPassed = false;

    public TimelineActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        List<String> testDeviceIds = Collections.singletonList("3DF6979E4CCB56C2A91510C1A9BCC253");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(this,
                getString(R.string.admob_app_id));

        final ImageView back = findViewById(R.id.back);
        TextView subject = findViewById(R.id.subject);
        final ImageButton commentBt = findViewById(R.id.commentIcon);
        menu = findViewById(R.id.menu);
        rvComments = findViewById(R.id.rvComments);
        emptyList = findViewById(R.id.emptyList);

        Spinner spinner = findViewById(R.id.filters);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.comment_filters, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (hasPassed) {
                    Query query = null;
                    if (position == 0) {
                        query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByChild("time").endAt(adapter.getLastItemId(false)).limitToLast(adapter.mPostsPerPage + 2);
                    } else if (position == 1) {
                        query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByChild("rating").endAt(adapter.getLastItemId(false)).limitToLast(adapter.mPostsPerPage + 2);
                    } else if (position == 2) {
                        query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByChild("agroup").equalTo(true).limitToLast(adapter.mPostsPerPage + 2);
                    }
                    reset = true;
                    getComments(adapter.getLastItemId(false), query);
                }
                hasPassed = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activity = this;

        if (getIntent() != null && getIntent().getExtras() != null) {
            subjectObj = getIntent().getExtras().getParcelable("subject");
            if (subjectObj != null) {
                subject.setText(subjectObj.getTitle());
            }
        }

        initRecyclerViewComment();
        getComments(null, null);

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

        rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (!mIsLoading) {
                        getComments(adapter.getLastItemId(isFirst), null);
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
                    Toast.makeText(getApplicationContext(), "Em construção.", Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                    startActivity(intent);*/
                    return true;
                } else if (item.getTitle().equals(getString(R.string.profile))) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
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
            if (canPost) {
                mayPass = true;
                openCommentBox();
            } else {
                Toast.makeText(getApplicationContext(), "Você já postou um comentário neste servidor ou já faz parte de um grupo!", Toast.LENGTH_SHORT).show();
            }
        });

        Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("tempInfo").child("activated").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean activated = dataSnapshot.getValue(Boolean.class);
                if (!activated) {
                    backToMainScreen();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void backToMainScreen() {
        Util.getUser().getTempInfo().setCurrentServer(null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initRecyclerViewComment() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        rvComments = findViewById(R.id.rvComments);
        layoutManager.setStackFromEnd(true);
        rvComments.setLayoutManager(layoutManager);
        adapter = new RecyclerViewCommentAdapter(getApplicationContext(), activity);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvComments.getContext(),
                layoutManager.getOrientation());
        rvComments.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(requireNonNull(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider)));
        rvComments.setAdapter(adapter);
        new Thread(this::loadNativeAds).start();
    }

    private void getComments(String nodeId, Query query) {
        mIsLoading = true;
        if (query == null) {
            if (nodeId == null) {
                query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByKey().limitToLast(adapter.mPostsPerPage);
                isFirst = true;
            } else {
                query = Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").orderByKey().endAt(nodeId).limitToLast(adapter.mPostsPerPage + 2);
                isFirst = false;
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
                adapter.addAll(comments, nodeId == null, false, reset);
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
                        if (mNativeAds.size() == NUMBER_OF_ADS) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();

        // Load the Native Express ad.
        try {
            Thread.sleep(3000);
            adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void insertAdsInMenuItems() {
        adapter.addAllAds(mNativeAds);
    }

    private void openCommentBox() {
        DialogPostComment cdd = new DialogPostComment(this, subjectObj);
        cdd.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("tempInfo").child("currentServer").setValue(null);
        Util.getServer().getTempInfo().setQtdUsers(Util.getServer().getTempInfo().getQtdUsers() - 1);
        Util.getmServerDatabaseRef().child(Util.getServer().getServerUID()).child("tempInfo").setValue(Util.getServer().getTempInfo());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.getmServerDatabaseRef().child(Util.getServer().getServerUID()).child("tempInfo").setValue(Util.getServer().getTempInfo());
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
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}