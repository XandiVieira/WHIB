package com.relyon.whib.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.relyon.whib.TimelineActivity;
import com.relyon.whib.modelo.Util;

public class ApplicationLifecycle extends Application implements Application.ActivityLifecycleCallbacks {

    private void setOnline(boolean online) {
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid != null) {
            Util.mUserDatabaseRef.child(uid).child("tempInfo").child("on").setValue(true);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        setOnline(true);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        setOnline(false);

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (Util.getUser() != null && Util.getServer() != null) {
            if (activity instanceof TimelineActivity) {
                Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("tempInfo").child("currentServer").setValue(null);
                Util.getServer().getTempInfo().setQtdUsers(Util.getServer().getTempInfo().getQtdUsers() - 1);
                Util.mSubjectDatabaseRef.child(Util.getServer().getSubject()).child("servers").child(Util.getServer().getServerUID()).child("tempInfo").setValue(Util.getServer().getTempInfo());
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}