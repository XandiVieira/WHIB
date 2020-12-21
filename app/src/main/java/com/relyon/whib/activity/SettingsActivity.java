package com.relyon.whib.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.relyon.whib.R;
import com.relyon.whib.dialog.DialogConfirmDeleteAccount;
import com.relyon.whib.dialog.DialogFinalWarn;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private ImageView back;
    private CheckBox checkSound, checkVibration, checkShowPhoto, checkNotifications;
    private TextView version;
    private Button deleteAccount;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setLayoutAttributes();

        if (getIntent().hasExtra(Constants.SHOW_LAST_WARN) && getIntent().getBooleanExtra(Constants.SHOW_LAST_WARN, false)) {
            DialogFinalWarn warn = new DialogFinalWarn(this);
            warn.show();
        }

        back.setOnClickListener(v -> onBackPressed());
        setAppVersion();

        checkNotifications.setChecked(Util.getUser().getPreferences().isNotification());
        checkShowPhoto.setChecked(Util.getUser().getPreferences().isShowPhoto());
        checkSound.setChecked(Util.getUser().getPreferences().isSound());
        checkVibration.setChecked(Util.getUser().getPreferences().isVibration());

        checkNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setUserPreferenceProperty(Constants.DATABASE_REF_NOTIFICATION, isChecked);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/" + Constants.NEW_SUBJECT);
        });
        checkShowPhoto.setOnCheckedChangeListener((buttonView, isChecked) -> setUserPreferenceProperty(Constants.DATABASE_REF_SHOW_PHOTO, isChecked));
        checkSound.setOnCheckedChangeListener((buttonView, isChecked) -> setUserPreferenceProperty(Constants.DATABASE_REF_SOUND, isChecked));
        checkVibration.setOnCheckedChangeListener((buttonView, isChecked) -> setUserPreferenceProperty(Constants.DATABASE_REF_VIBRATION, isChecked));

        logout.setOnClickListener(v -> logout());

        deleteAccount.setOnClickListener(v -> {
            DialogConfirmDeleteAccount cdd = new DialogConfirmDeleteAccount(this);
            cdd.show();
        });
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
        checkSound = findViewById(R.id.checkSound);
        checkVibration = findViewById(R.id.checkVibration);
        checkShowPhoto = findViewById(R.id.checkShowPhoto);
        checkNotifications = findViewById(R.id.checkNotifications);
        version = findViewById(R.id.version);
        deleteAccount = findViewById(R.id.delete_acc);
        logout = findViewById(R.id.logout);
    }

    private void setAppVersion() {
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionTxt = pInfo.versionName;
            version.setText(getString(R.string.version, versionTxt));
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(getString(R.string.error), Objects.requireNonNull(e.getMessage()));
        }
    }

    private void setUserPreferenceProperty(String propertyReference, boolean value) {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_USER).child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_PREFERENCES).child(propertyReference).setValue(value);
        switch (propertyReference) {
            case Constants.DATABASE_REF_NOTIFICATION:
                Util.getUser().getPreferences().setNotification(value);
                break;
            case Constants.DATABASE_REF_SHOW_PHOTO:
                Util.getUser().getPreferences().setShowPhoto(value);
                break;
            case Constants.DATABASE_REF_VIBRATION:
                Util.getUser().getPreferences().setVibration(value);
                break;
            case Constants.DATABASE_REF_SOUND:
                Util.getUser().getPreferences().setSound(value);
                break;
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Util.restartClass();
        goLoginScreen();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (getIntent().hasExtra(Constants.CAME_FROM_PROFILE) && getIntent().getBooleanExtra(Constants.CAME_FROM_PROFILE, false)) {
            intent = new Intent(this, ProfileActivity.class);
            if (Util.getUser() != null && Util.getUser().getUserUID() != null) {
                intent.putExtra(Constants.USER_ID, Util.getUser().getUserUID());
            }
        } else if (Util.getServer() != null) {
            intent = new Intent(this, TimelineActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }
}