package com.relyon.whib;

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
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private Activity activity;
    private User user;
    private LinearLayout contactUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        retrieveUser();

        activity = this;
        contactUs = findViewById(R.id.contact_us);

        LinearLayout aboutUs = findViewById(R.id.about_us);
        LinearLayout tips = findViewById(R.id.tips);
        LinearLayout faq = findViewById(R.id.faq);
        LinearLayout terms = findViewById(R.id.use_terms);
        LinearLayout privacyPolicy = findViewById(R.id.privacy_policy);
        ImageView back = findViewById(R.id.back);
        TextView version = findViewById(R.id.version);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionTxt = pInfo.versionName;
            version.setText(getString(R.string.version, versionTxt));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(getString(R.string.error), Objects.requireNonNull(e.getMessage()));
        }

        aboutUs.setOnClickListener(v -> startActivity(new Intent(this, AboutUsActivity.class)));

        tips.setOnClickListener(v -> startActivity(new Intent(this, TipsActivity.class)));

        faq.setOnClickListener(v -> startActivity(new Intent(this, FaqActivity.class)));

        terms.setOnClickListener(v -> startActivity(new Intent(this, TermsActivity.class)));

        privacyPolicy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyPolicyActivity.class)));

        contactUs.setOnClickListener(v -> startActivity(new Intent(this, ContactActivity.class)));

        back.setOnClickListener(v -> onBackPressed());
    }

    private void retrieveUser() {
        Util.mUserDatabaseRef.child(Util.fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    if (getIntent().hasExtra("showLastWarn") && getIntent().getBooleanExtra("showLastWarn", false) && Util.getUser().isFirstTime()) {
                        DialogFinalWarn warn = new DialogFinalWarn(activity);
                        warn.show();
                    }

                    if (Util.getUser() == null) {
                        contactUs.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent;
        if (Util.getServer() != null) {
            intent = new Intent(this, TimelineActivity.class);
        } else {
            intent = new Intent(this, ProfileActivity.class);
        }
        startActivity(intent);
    }
}