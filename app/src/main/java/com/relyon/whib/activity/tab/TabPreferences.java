package com.relyon.whib.activity.tab;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.relyon.whib.R;
import com.relyon.whib.activity.LoginActivity;
import com.relyon.whib.dialog.DialogConfirmDeleteAcc;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.Util;

public class TabPreferences extends Fragment {

    private CheckBox checkSound, checkVibration, checkShowPhoto, checkNotifications;
    private TextView version;
    private Button deleteAccount;
    private Button logout;

    @Override
    public void onAttach(@NonNull final Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);

        setLayoutAttributes(rootView);

        setAppVersion(container);

        checkNotifications.setChecked(Util.getUser().getPreferences().isNotification());
        checkShowPhoto.setChecked(Util.getUser().getPreferences().isShowPhoto());
        checkSound.setChecked(Util.getUser().getPreferences().isSound());
        checkVibration.setChecked(Util.getUser().getPreferences().isVibration());

        checkNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_PREFERENCES).child(Constants.DATABASE_REF_NOTIFICATION).setValue(checkNotifications.isChecked()));

        checkShowPhoto.setOnCheckedChangeListener((buttonView, isChecked) -> Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_PREFERENCES).child(Constants.DATABASE_REF_SHOW_PHOTO).setValue(checkShowPhoto.isChecked()));

        checkSound.setOnCheckedChangeListener((buttonView, isChecked) -> Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_PREFERENCES).child(Constants.DATABASE_REF_SOUND).setValue(checkSound.isChecked()));

        checkVibration.setOnCheckedChangeListener((buttonView, isChecked) -> Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_PREFERENCES).child(Constants.DATABASE_REF_VIBRATION).setValue(checkVibration.isChecked()));

        logout.setOnClickListener(v -> logout());

        deleteAccount.setOnClickListener(v -> {
            DialogConfirmDeleteAcc cdd = new DialogConfirmDeleteAcc(getActivity());
            cdd.show();
            if (Util.getDelete()) {
                deleteAccount();
            }
        });

        return rootView;
    }

    private void setAppVersion(ViewGroup container) {
        PackageInfo pInfo = null;
        try {
            pInfo = container.getContext().getPackageManager().getPackageInfo(container.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(getString(R.string.error), e.getMessage());
        }
        version.setText("Version " + pInfo.versionName);
    }

    private void setLayoutAttributes(View rootView) {
        checkSound = rootView.findViewById(R.id.checkSound);
        checkVibration = rootView.findViewById(R.id.checkVibration);
        checkShowPhoto = rootView.findViewById(R.id.checkShowPhoto);
        checkNotifications = rootView.findViewById(R.id.checkNotifications);
        version = rootView.findViewById(R.id.version);
        deleteAccount = rootView.findViewById(R.id.delete_acc);
        logout = rootView.findViewById(R.id.logout);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void goLoginScreen() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    private void deleteAccount() {
        logout();
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).setValue(null);
    }
}