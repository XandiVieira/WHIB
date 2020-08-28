package com.relyon.whib.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.relyon.whib.R;
import com.relyon.whib.adapter.ScreenSlidePagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class LoginActivity extends AppCompatActivity implements Runnable {

    private Activity activity;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ArrayList<Techniques> techniques = new ArrayList<>();
    private Random random = new Random();

    private LoginButton loginButton;
    private ProgressBar progressBar;
    private ImageView logo;
    private ViewPager pager;
    private TextView appVersion;
    private TextView terms;
    private TextView privacyPolicy;

    // The number of pages to show in this pager.
    private static final int NUM_PAGES = 4;


    @Override
    public void run() {
        if (pager.getCurrentItem() == 3) {
            pager.setCurrentItem(0);
        } else {
            pager.setCurrentItem(pager.getCurrentItem() + 1);
        }
        new Handler().postDelayed(this, 4000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;

        setLayoutAttributes();

        getAppVersion();

        terms.setOnClickListener(v -> startActivity(new Intent(this, TermsActivity.class)));

        privacyPolicy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyPolicyActivity.class)));

        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(activity, R.string.error_login, Toast.LENGTH_SHORT).show();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                goMainScreen();
            }
        };

        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), NUM_PAGES);
        pager.setAdapter(pagerAdapter);

        new Handler().postDelayed(this, 4000);
        techniques.addAll(Arrays.asList(Techniques.values()));
        callAnimation(random.nextInt(techniques.size()));
    }

    private void setLayoutAttributes() {
        progressBar = findViewById(R.id.progress_bar);
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        logo = findViewById(R.id.logo);
        terms = findViewById(R.id.terms);
        privacyPolicy = findViewById(R.id.privacy_policy);
        appVersion = findViewById(R.id.version);
        pager = findViewById(R.id.pager);
    }

    private void getAppVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            appVersion.setText(getString(R.string.version, version));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(getString(R.string.error), Objects.requireNonNull(e.getMessage()));
        }
    }

    private void callAnimation(final int i) {
        YoYo.with(techniques.get(i)).duration(7000).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                callAnimation(random.nextInt(techniques.size()));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).repeat(0).playOn(logo);
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(activity, R.string.firebase_error_login, Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        });
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }
}