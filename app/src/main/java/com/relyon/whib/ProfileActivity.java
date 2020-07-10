package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private EditText nick;
    private User user;
    private ImageView photo;
    private TextView userName;
    private boolean loadReports = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView back = findViewById(R.id.back);
        photo = findViewById(R.id.photo);
        ImageView settings = findViewById(R.id.settings);
        userName = findViewById(R.id.userName);
        nick = findViewById(R.id.nick);

        if (getIntent().hasExtra("userId") && !getIntent().getStringExtra("userId").equals(Util.getUser().getUserUID())) {
            Util.mUserDatabaseRef.child(getIntent().getStringExtra("userId")).addListenerForSingleValueEvent(new ValueEventListener() {
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
        } else {
            if (Util.getUser() != null) {
                user = Util.getUser();
                loadReports = true;
                setUserProfile();
            } else {
                //user =
            }
        }

        back.setOnClickListener(v -> {
            if (nick.getText().toString().trim().equals("")) {
                onBackPressed();
            } else if (user.getNickName() == null && !nick.getText().toString().replace("@", "").equals("")) {
                String nickname = nick.getText().toString().replace("@", "");
                user.setNickName(nickname);
                Util.getmUserDatabaseRef().child("nickname").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> nicknames = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            nicknames.add(snap.getValue(String.class));
                        }
                        if (nicknames.contains(nickname)) {
                            Toast.makeText(getApplicationContext(), "Este nome de usuário já está sendo utilizado.", Toast.LENGTH_SHORT).show();
                        } else {
                            Util.getmUserDatabaseRef().child(user.getUserUID()).setValue(user);
                            Util.getmUserDatabaseRef().child("nickname").push().setValue(user.getNickName());
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        settings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SettingsActivity.class)));
    }

    private void setUserProfile() {
        Glide.with(getApplicationContext()).load(user.getPhotoPath()).apply(RequestOptions.circleCropTransform()).into(photo);

        userName.setText(user.getUserName());

        if ((user.getNickName() != null && !user.getNickName().isEmpty()) || !user.getUserUID().equals(Util.getUser().getUserUID())) {
            nick.setEnabled(false);
        } else {
            nick.setEnabled(true);
        }

        SectionPagerProfileAdapter mSectionsPagerAdapter = new SectionPagerProfileAdapter(getSupportFragmentManager(), getApplicationContext());

        // Set up the ViewPager with the sections adapter.
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
            intent = new Intent(getApplicationContext(), TimelineActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
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

    public void setLoadReports(boolean loadReports) {
        this.loadReports = loadReports;
    }
}