package com.relyon.whib.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.dialog.DialogFinalWarn;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private Activity activity;
    private User user;

    private LinearLayout contactUs;
    private LinearLayout aboutUs;
    private LinearLayout tips;
    private LinearLayout faq;
    private LinearLayout terms;
    private LinearLayout privacyPolicy;
    private ImageView back;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        activity = this;

        retrieveUser();

        setLayoutAttributes();

        setAppVersion();

        aboutUs.setOnClickListener(v -> startActivity(new Intent(this, AboutUsActivity.class)));
        tips.setOnClickListener(v -> startActivity(new Intent(this, TipsActivity.class)));
        faq.setOnClickListener(v -> startActivity(new Intent(this, FaqActivity.class)));
        terms.setOnClickListener(v -> startActivity(new Intent(this, TermsActivity.class)));
        privacyPolicy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyPolicyActivity.class)));
        contactUs.setOnClickListener(v -> startActivity(new Intent(this, ContactActivity.class)));

        back.setOnClickListener(v -> onBackPressed());
    }

    private void setLayoutAttributes() {
        contactUs = findViewById(R.id.contact_us);
        aboutUs = findViewById(R.id.about_us);
        tips = findViewById(R.id.tips);
        faq = findViewById(R.id.faq);
        terms = findViewById(R.id.use_terms);
        privacyPolicy = findViewById(R.id.privacy_policy);
        back = findViewById(R.id.back);
        version = findViewById(R.id.version);
    }

    private void retrieveUser() {
        Util.mUserDatabaseRef.child(Util.fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    if (getIntent().hasExtra(Constants.SHOW_LAST_WARN) && getIntent().getBooleanExtra(Constants.SHOW_LAST_WARN, false) && Util.getUser().isFirstTime()) {
                        new DialogFinalWarn(activity).show();
                    }
                } else {
                    contactUs.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAppVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionTxt = pInfo.versionName;
            version.setText(getString(R.string.version, versionTxt));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(getString(R.string.error), Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent;
        if (getIntent().hasExtra(Constants.CAME_FROM_PROFILE) && getIntent().getBooleanExtra(Constants.CAME_FROM_PROFILE, false)) {
            intent = new Intent(this, ProfileActivity.class);
        } else if (Util.getServer() != null) {
            intent = new Intent(this, TimelineActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }
}