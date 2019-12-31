package com.relyon.whib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.relyon.whib.modelo.Util;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TabPreferences extends Fragment {

    private FrameLayout mFramePerfil;

    private TabPreferences tabPreferences;
    private TabWhibExtra tabWhibExtra;

    private Context mContext;
    public FirebaseUser user;
    private CheckBox checkSound, checkVibration, checkShowPhoto, checkNotifications;
    private Button delete_acc, logout;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);

        tabPreferences = new TabPreferences();
        tabWhibExtra = new TabWhibExtra();

        checkSound = rootView.findViewById(R.id.checkSound);
        checkVibration = rootView.findViewById(R.id.checkVibration);
        checkShowPhoto = rootView.findViewById(R.id.checkShowPhoto);
        checkNotifications = rootView.findViewById(R.id.checkNotifications);
        delete_acc = rootView.findViewById(R.id.delete_acc);
        logout = rootView.findViewById(R.id.logout);

        if(Util.getUser().getPreferences().isNotification()){
            checkNotifications.setChecked(true);
        }else{
            checkNotifications.setChecked(false);
        }

        if(Util.getUser().getPreferences().isShowPhoto()){
            checkShowPhoto.setChecked(true);
        }else{
            checkShowPhoto.setChecked(false);
        }

        if(Util.getUser().getPreferences().isSound()){
            checkSound.setChecked(true);
        }else{
            checkSound.setChecked(false);
        }

        if(Util.getUser().getPreferences().isVibration()){
            checkVibration.setChecked(true);
        }else{
            checkVibration.setChecked(false);
        }

        checkNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkNotifications.isChecked()){
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("preferences").child("notification").setValue(true);
                }
            }
        });

        checkShowPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkShowPhoto.isChecked()){
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("preferences").child("showPhoto").setValue(true);
                }
            }
        });

        checkSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkSound.isChecked()){
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("preferences").child("sound").setValue(true);
                }
            }
        });

        checkVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkVibration.isChecked()){
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("preferences").child("vibration").setValue(true);
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        delete_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogConfirmDeleteAcc cdd = new DialogConfirmDeleteAcc(getActivity());
                cdd.show();
                if(Util.getDelete()){
                    deleteAcc();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    //Go to login activity
    private void goLoginScreen() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //logout the user from application
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    public void deleteAcc(){
        logout();
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).setValue(null);
    }
}
