package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    private ImageView menu;
    private Subject subjectObj;
    private ArrayList<Object> commentList;
    private RecyclerView rvComments;
    private AppCompatActivity activity;
    private boolean canPost = true;
    // The number of native ads to load and display.
    public int NUMBER_OF_ADS = 0;

    // The AdLoader used to load ads.
    private AdLoader adLoader;

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private RecyclerViewCommentAdapter adapter;

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
        commentList = new ArrayList<>();
        activity = this;

        subjectObj = getIntent().getExtras().getParcelable("subject");
        assert subjectObj != null;
        subject.setText(subjectObj.getTitle());

        new Thread(this::loadNativeAds).start();

        new Thread(() -> Util.mServerDatabaseRef.child(Util.getServer().getServerUID()).child("timeline").child("commentList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    comment.setCommentUID(snap.getKey());
                    commentList.add(comment);
                }
                NUMBER_OF_ADS = commentList.size();
                if (!commentList.isEmpty()) {
                    initRecyclerViewComment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })).start();

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
                } else if (item.getTitle().equals(getString(R.string.about))) {
                    Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            popup.show(); //showing popup menu
        }); //closing the setOnClickListener method

        back.setOnClickListener(v -> backToMainScreen());

        commentBt.setOnClickListener(v -> {
            if (canPost) {
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
        adapter = new RecyclerViewCommentAdapter(getApplicationContext(), commentList, activity);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvComments.getContext(),
                layoutManager.getOrientation());
        rvComments.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider));
        rvComments.setAdapter(adapter);
    }

    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                unifiedNativeAd -> {
                    mNativeAds.add(unifiedNativeAd);
                    if (!adLoader.isLoading()) {
                        insertAdsInMenuItems();
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Log.e("TimelineActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();

        // Load the Native Express ad.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }

    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }
        int size = commentList.size();
        for (int i = 0; i < size; i++) {
            if (mNativeAds.size() >= i && size > (i + 1)) {
                commentList.add((i + 1), mNativeAds.get(i));
                adapter.notifyItemInserted(i + 1);
            }
        }
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
}