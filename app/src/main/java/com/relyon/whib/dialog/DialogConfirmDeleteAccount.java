package com.relyon.whib.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.relyon.whib.R;
import com.relyon.whib.activity.LoginActivity;
import com.relyon.whib.util.Util;

public class DialogConfirmDeleteAccount extends Dialog implements View.OnClickListener {

    private FragmentActivity fragmentActivity;
    public Dialog dialog;

    public DialogConfirmDeleteAccount(FragmentActivity a) {
        super(a);
        this.fragmentActivity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_account);
        setTransparentBackground();
    }

    private void setTransparentBackground() {
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteBt:
                deleteAccount();
                break;
            case R.id.cancelBt:
                fragmentActivity.closeContextMenu();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
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