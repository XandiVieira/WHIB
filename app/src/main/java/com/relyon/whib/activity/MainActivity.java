package com.relyon.whib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseState;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.relyon.whib.R;
import com.relyon.whib.activity.adm.AdmChoosingProfileActivity;
import com.relyon.whib.adapter.RecyclerViewServerGroupAdapter;
import com.relyon.whib.dialog.DialogChooseSubscription;
import com.relyon.whib.dialog.DialogCongratsSubscription;
import com.relyon.whib.modelo.Preferences;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.modelo.Valuation;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.SelectSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

import static com.relyon.whib.util.Constants.DATABASE_REF_FIRST_TIME;
import static com.relyon.whib.util.Constants.SKU_WHIB_MONTHLY;
import static com.relyon.whib.util.Constants.SKU_WHIB_SIXMONTH;
import static com.relyon.whib.util.Constants.SKU_WHIB_YEARLY;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, SelectSubscription {

    private Activity activity;
    private FirebaseUser firebaseUser;
    private User user;
    private DatabaseReference mUserDatabaseRef;
    private DatabaseReference mSubjectDatabaseRef;
    private BillingProcessor billingProcessor;
    private String firebaseInstanceId;
    private HashMap<String, Server> serversMap;

    private LinearLayout progressBar;
    private LinearLayout profileIcon;
    private LinearLayout logoutLayout;
    private Button chooseSubjectButton;
    private RecyclerView recyclerViewServerSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        billingProcessor = new BillingProcessor(this, getResources().getString(R.string.google_license_key), this);
        billingProcessor.initialize();

        setLayoutAttributes();

        startFirebaseInstances();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> firebaseInstanceId = instanceIdResult.getToken());

        if (firebaseUser == null) {
            goLoginScreen();
        } else if (user == null) {
            Util.setFbUser(firebaseUser);
            Util.setNumberOfServers(0);
            getUserDataFromFirebase();
        } else {
            verifyUserSubscriptionStatus();
        }

        retrieveSubjects();

        mFirebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600L).build());

        profileIcon.setOnClickListener(view -> goToProfile());
        logoutLayout.setOnClickListener(view -> logout());

        chooseSubjectButton.setOnClickListener(v -> {
            if (user.isExtra() || user.isAdmin()) {
                startActivity(new Intent(this, NextSubjectVotingActivity.class));
            } else {
                FragmentTransaction fm = this.getSupportFragmentManager().beginTransaction();
                DialogChooseSubscription dialogChooseSubscription = DialogChooseSubscription.newInstance(this);
                dialogChooseSubscription.show(fm, "");
            }
        });
    }

    private void setLayoutAttributes() {
        recyclerViewServerSection = findViewById(R.id.recycler_view_section);
        progressBar = findViewById(R.id.progress_bar);
        chooseSubjectButton = findViewById(R.id.choose_subject_button);
        profileIcon = findViewById(R.id.profile_layout);
        logoutLayout = findViewById(R.id.logout_layout);
    }

    private void updateToken() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.TOKEN).setValue(firebaseInstanceId);
        }
    }

    private void retrieveSubjects() {
        mSubjectDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Subject> subjects = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Subject subject = snap.getValue(Subject.class);
                    if (subject != null) {
                        subjects.add(subject);
                    }
                }
                setSubjects(subjects);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setSubjects(List<Subject> subjects) {
        serversMap = new HashMap<>();
        for (Subject subject : subjects) {
            for (Server server : subject.getServers().values()) {
                serversMap.put(server.getServerUID(), server);
            }
        }
        Util.setNumberOfServers(serversMap.values().size());
        initRecyclerViewGroup();
        progressBar.setVisibility(View.GONE);
        chooseSubjectButton.setVisibility(View.VISIBLE);
    }

    private void initRecyclerViewGroup() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewServerSection = findViewById(R.id.recycler_view_section);
        recyclerViewServerSection.setLayoutManager(layoutManager);
        RecyclerViewServerGroupAdapter adapter = new RecyclerViewServerGroupAdapter(serversMap);
        recyclerViewServerSection.setAdapter(adapter);
    }

    private void startFirebaseInstances() {
        FirebaseApp.initializeApp(this);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mFirebaseDatabase.getReference();
        mUserDatabaseRef = mDatabaseRef.child(Constants.DATABASE_REF_USER);
        mSubjectDatabaseRef = mDatabaseRef.child(Constants.DATABASE_REF_SUBJECT);
        DatabaseReference mGroupDatabaseRef = mDatabaseRef.child(Constants.DATABASE_REF_GROUP);
        DatabaseReference mAdvantagesDatabaseRef = mDatabaseRef.child(Constants.DATABASE_REF_ADVANTAGE);
        DatabaseReference mReportsDatabaseRef = mDatabaseRef.child(Constants.DATABASE_REF_REPORT);
        Util.setmDatabaseRef(mDatabaseRef);
        Util.setmUserDatabaseRef(mUserDatabaseRef);
        Util.setmSubjectDatabaseRef(mSubjectDatabaseRef);
        Util.setmGroupDatabaseRef(mGroupDatabaseRef);
        Util.setmAdvantagesDatabaseRef(mAdvantagesDatabaseRef);
        Util.setmReportDatabaseRef(mReportsDatabaseRef);
    }

    private void getUserDataFromFirebase() {
        mUserDatabaseRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user == null || user.getUserUID() == null) {
                    createUser();
                }

                Util.setUser(user);
                updateToken();
                callWelcomeTour();
                verifyUserSubscriptionStatus();
                profileIcon.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createUser() {
        String userPhotoPath = null;
        if (firebaseUser.getPhotoUrl() != null) {
            userPhotoPath = firebaseUser.getPhotoUrl().toString();
        }

        user = new User(firebaseUser.getUid(), firebaseInstanceId, firebaseUser.getDisplayName(), userPhotoPath, setUserValuation(), null, false, true,
                false, null, 0, null, null,
                false, false, 0, 0, setUserPreferences(), null, false);

        Util.setUser(user);
        mUserDatabaseRef.child(firebaseUser.getUid()).setValue(user);
    }

    private void callWelcomeTour() {
        if (user != null) {
            if (user.isFirstTime()) {
                if (getIntent().hasExtra(Constants.SERVER_EMPTY)) {
                    Toast tag = Toast.makeText(activity, getString(R.string.server_empty_plase_select_another), Toast.LENGTH_LONG);
                    new CountDownTimer(3000, 3500) {
                        public void onTick(long millisUntilFinished) {
                            tag.show();
                        }

                        public void onFinish() {
                            tag.show();
                        }
                    }.start();
                }
                FancyShowCaseQueue queue = new FancyShowCaseQueue();
                if (user.isFirstTime()) {
                    FancyShowCaseView fancyShowCaseView = new FancyShowCaseView.Builder(this).customView(R.layout.custom_tour_servers, view -> view.findViewById(R.id.skip_tutorial).setOnClickListener(v -> {
                        Util.getUser().setFirstTime(false);
                        mUserDatabaseRef.child(Util.getUser().getUserUID()).child(DATABASE_REF_FIRST_TIME).setValue(false);
                    })).focusOn(recyclerViewServerSection)
                            .focusBorderSize(10)
                            .focusCircleAtPosition(550, 800, 500)
                            .build();
                    queue.add(fancyShowCaseView);
                    queue.show();
                }
            }
        }
    }

    private Preferences setUserPreferences() {
        return new Preferences(true, true, true, true);
    }

    private Valuation setUserValuation() {
        return new Valuation(0, 0, 0, 0, 0, 0);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    public void goToProfile() {
        Intent intent;
        if (Util.getUser().isAdmin()) {
            intent = new Intent(this, AdmChoosingProfileActivity.class);
        } else {
            intent = new Intent(this, ProfileActivity.class);
        }
        startActivity(intent);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_EXTRA).setValue(true);
        Util.getUser().setExtra(true);
        DialogCongratsSubscription dialogCongratsSubscription = new DialogCongratsSubscription(this);
        dialogCongratsSubscription.show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    public void onChoose(String sku) {
        subscribe(sku);
    }

    private void subscribe(String sku) {
        billingProcessor.subscribe(this, sku);
    }

    public void verifyUserSubscriptionStatus() {
        boolean purchaseResult = billingProcessor.loadOwnedPurchasesFromGoogle();
        List<String> subscriptionsSKU = new ArrayList<>();
        subscriptionsSKU.add(SKU_WHIB_MONTHLY);
        subscriptionsSKU.add(SKU_WHIB_SIXMONTH);
        subscriptionsSKU.add(SKU_WHIB_YEARLY);
        if (purchaseResult) {
            for (int i = 0; i < subscriptionsSKU.size(); i++) {
                if (updateUserSubscription(subscriptionsSKU.get(i))) {
                    break;
                }
            }
        }
    }

    private boolean updateUserSubscription(String subscriptionSKU) {
        TransactionDetails subscriptionTransactionDetails =
                billingProcessor.getSubscriptionTransactionDetails(subscriptionSKU);

        if (subscriptionTransactionDetails != null && subscriptionTransactionDetails.purchaseInfo.purchaseData.purchaseState == PurchaseState.PurchasedSuccessfully) {
            Util.getUser().setExtra(true);
            mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_EXTRA).setValue(true);
            return true;
        } else {
            Util.getUser().setExtra(false);
            mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_EXTRA).setValue(false);
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }
}