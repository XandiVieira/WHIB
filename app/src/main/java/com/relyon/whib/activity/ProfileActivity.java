package com.relyon.whib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.adapter.SectionPagerProfileAdapter;
import com.relyon.whib.dialog.DialogFinalWarn;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private Activity activity;
    private User user;
    private boolean loadReports = false;

    private ImageView menu;
    private ImageView photo;
    private ImageView back;
    private LinearLayout settingsLayout;
    private EditText nick;
    private TextView userName;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        activity = this;

        setLayoutAttributes();

        if (getIntent().hasExtra(Constants.SHOW_LAST_WARN) && getIntent().getBooleanExtra(Constants.SHOW_LAST_WARN, false) && Util.getUser().isFirstTime()) {
            new DialogFinalWarn(this).show();
        }

        if (getIntent().hasExtra(Constants.USER_ID) && !getIntent().getStringExtra(Constants.USER_ID).equals(Util.getUser().getUserUID())) {
            Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(getIntent().getStringExtra(Constants.USER_ID)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (!user.getUserUID().equals(Util.getUser().getUserUID())) {
                            settingsLayout.setVisibility(View.INVISIBLE);
                        }
                        loadReports = false;
                        setUserProfile();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (Util.getUser() != null) {
            user = Util.getUser();
            loadReports = true;
            setUserProfile();
        }

        back.setOnClickListener(v -> onBackPressed());

        settingsLayout.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class).putExtra(Constants.CAME_FROM_PROFILE, true)));

        menu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ProfileActivity.this, menu);
            popup.getMenuInflater()
                    .inflate(R.menu.menu_timeline, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (Util.getUser().isFirstTime()) {
                    Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_FIRST_TIME).setValue(false);
                }
                Intent intent = null;
                if (item.getTitle().equals(getString(R.string.settings))) {
                    intent = new Intent(this, SettingsActivity.class).putExtra(Constants.CAME_FROM_PROFILE, true);
                } else if (item.getTitle().equals(getString(R.string.store))) {
                    intent = new Intent(this, StoreActivity.class).putExtra(Constants.CAME_FROM_PROFILE, true);
                } else if (item.getTitle().equals(getString(R.string.tips))) {
                    intent = new Intent(this, TipsActivity.class).putExtra(Constants.CAME_FROM_TIMELINE, false).putExtra(Constants.CAME_FROM_PROFILE, true);
                } else if (item.getTitle().equals(getString(R.string.about))) {
                    intent = new Intent(this, AboutActivity.class).putExtra(Constants.CAME_FROM_PROFILE, true);
                }
                if (intent != null) {
                    startActivity(intent);
                }
                return true;
            });
            popup.show();
        });
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
        photo = findViewById(R.id.photo);
        settingsLayout = findViewById(R.id.settings_layout);
        userName = findViewById(R.id.user_name);
        nick = findViewById(R.id.nick);
        menu = findViewById(R.id.menu);
        mViewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tabs);
    }

    private void setUserProfile() {
        if (user.getPreferences().isShowPhoto()) {
            Glide.with(this).load(user.getPhotoPath()).apply(RequestOptions.circleCropTransform()).into(photo);
        } else {
            photo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_no_pic));
        }

        userName.setText(user.getUserName());
        nick.setText(user.getNickName() != null && !user.getNickName().trim().isEmpty() ? "@" + user.getNickName() : "");

        if ((user.getNickName() != null && !user.getNickName().isEmpty()) || !user.getUserUID().equals(Util.getUser().getUserUID())) {
            nick.setEnabled(false);
        } else {
            nick.setEnabled(true);
        }
        setupPagerProfileAdapter();
    }

    private void setupPagerProfileAdapter() {
        SectionPagerProfileAdapter mSectionsPagerAdapter = new SectionPagerProfileAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if (user != null && (user.getNickName() == null || user.getNickName().trim().equals("")) && !nick.getText().toString().replace("@", "").equals("")) {
            String nickname = nick.getText().toString().replace("@", "");
            Util.getmDatabaseRef().child(Constants.DATABASE_REF_NICKNAME).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Util.getmDatabaseRef().child(Constants.DATABASE_REF_NICKNAME).removeEventListener(this);
                    List<String> nicknames = new ArrayList<>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        nicknames.add(snap.getValue(String.class));
                    }
                    if (nicknames.contains(nickname)) {
                        Toast.makeText(activity, R.string.username_was_already_taken, Toast.LENGTH_SHORT).show();
                    } else {
                        user.setNickName(nickname);
                        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(user.getUserUID()).setValue(user);
                        Util.mDatabaseRef.child(Constants.DATABASE_REF_NICKNAME).push().setValue(user.getNickName());
                        onBackPressed();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            if (Util.getServer() != null) {
                intent = new Intent(this, TimelineActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLoadReports() {
        return loadReports;
    }
}