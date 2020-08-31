package com.relyon.whib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView settings;
    private EditText nick;
    private TextView userName;

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
            Util.mUserDatabaseRef.child(getIntent().getStringExtra(Constants.USER_ID)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
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

        back.setOnClickListener(v -> {
            if (nick.getText().toString().trim().equals("")) {
                onBackPressed();
            } else if (user.getNickName() == null && !nick.getText().toString().replace("@", "").equals("")) {
                String nickname = nick.getText().toString().replace("@", "");
                user.setNickName(nickname);
                Util.getmDatabaseRef().child(Constants.DATABASE_REF_NICKNAME).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> nicknames = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            nicknames.add(snap.getValue(String.class));
                        }
                        if (nicknames.contains(nickname)) {
                            Toast.makeText(activity, "Este nome de usuário já está sendo utilizado.", Toast.LENGTH_SHORT).show();
                        } else {
                            Util.getmDatabaseRef().child(Constants.DATABASE_REF_NICKNAME).removeEventListener(this);
                            Util.getmUserDatabaseRef().child(user.getUserUID()).setValue(user);
                            Util.getmDatabaseRef().child(Constants.DATABASE_REF_NICKNAME).push().setValue(user.getNickName());
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        settings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class).putExtra(Constants.CAME_FROM_PROFILE, true)));

        menu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ProfileActivity.this, menu);
            popup.getMenuInflater()
                    .inflate(R.menu.menu_timeline, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (Util.getUser().isFirstTime()) {
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_FIRST_TIME).setValue(false);
                }
                Intent intent = new Intent();
                if (item.getTitle().equals(getString(R.string.settings))) {
                    intent = new Intent(this, SettingsActivity.class).putExtra(Constants.CAME_FROM_PROFILE, true);
                } else if (item.getTitle().equals(getString(R.string.store))) {
                    intent = new Intent(this, StoreActivity.class).putExtra(Constants.CAME_FROM_PROFILE, true);
                } else if (item.getTitle().equals(getString(R.string.profile))) {
                    intent = new Intent(this, ProfileActivity.class).putExtra(Constants.CAME_FROM_PROFILE, true);
                } else if (item.getTitle().equals(getString(R.string.tips))) {
                    intent = new Intent(this, TipsActivity.class).putExtra(Constants.CAME_FROM_TIMELINE, false).putExtra(Constants.CAME_FROM_PROFILE, true);
                } else if (item.getTitle().equals(getString(R.string.about))) {
                    intent = new Intent(this, AboutActivity.class).putExtra(Constants.CAME_FROM_PROFILE, true);
                }
                startActivity(intent);
                return true;
            });
            popup.show();
        });
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
        photo = findViewById(R.id.photo);
        settings = findViewById(R.id.settings);
        userName = findViewById(R.id.user_name);
        nick = findViewById(R.id.nick);
        menu = findViewById(R.id.menu);
    }

    private void setUserProfile() {
        if (user.getPreferences().isShowPhoto()) {
            Glide.with(this).load(user.getPhotoPath()).apply(RequestOptions.circleCropTransform()).into(photo);
        }

        userName.setText(user.getUserName());

        if ((user.getNickName() != null && !user.getNickName().isEmpty()) || !user.getUserUID().equals(Util.getUser().getUserUID())) {
            nick.setEnabled(false);
        } else {
            nick.setEnabled(true);
        }

        SectionPagerProfileAdapter mSectionsPagerAdapter = new SectionPagerProfileAdapter(getSupportFragmentManager(), this);

        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent;
        if (Util.getServer() != null) {
            intent = new Intent(this, TimelineActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
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