package com.relyon.whib.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.relyon.whib.R;
import com.relyon.whib.activity.LoginActivity;
import com.relyon.whib.util.Util;

public class DialogConfirmDeleteAccount extends Dialog {

    private TextView cancel;
    private TextView delete;

    public DialogConfirmDeleteAccount(FragmentActivity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_account);
        setTransparentBackground();

        setLayoutAttributes();

        cancel.setOnClickListener(view -> dismiss());
        delete.setOnClickListener(view -> deleteAccount());
    }

    private void setLayoutAttributes() {
        cancel = findViewById(R.id.cancel);
        delete = findViewById(R.id.delete);
    }

    private void setTransparentBackground() {
        if (getWindow() != null && getWindow() != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
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
        Toast.makeText(getContext(), R.string.account_successfully_deleted, Toast.LENGTH_LONG).show();
    }
}