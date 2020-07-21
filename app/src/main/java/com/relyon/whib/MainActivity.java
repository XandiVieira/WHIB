package com.relyon.whib;

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
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Preferences;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.UserTempInfo;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.modelo.Valuation;
import com.relyon.whib.util.ApplicationLifecycle;
import com.relyon.whib.util.SelectSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

import static com.relyon.whib.util.Constants.SKU_WHIB_MONTHLY;
import static com.relyon.whib.util.Constants.SKU_WHIB_SIXMONTH;
import static com.relyon.whib.util.Constants.SKU_WHIB_YEARLY;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, SelectSubscription {

    private FirebaseUser fbUser;
    private User user;
    private DatabaseReference mUserDatabaseRef;
    private DatabaseReference mSubjectDatabaseRef;
    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private BillingProcessor billingProcessor;

    //Layout elements
    private HashMap<String, Server> servers;
    private LinearLayout progressBar;
    private LinearLayout profile;
    private Button choseSubjectButton;

    private RecyclerView recyclerViewServers;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        ApplicationLifecycle lifecycle = (ApplicationLifecycle) getApplication();
        getApplication().registerActivityLifecycleCallbacks(lifecycle);

        billingProcessor = new BillingProcessor(this, getResources().getString(R.string.google_license_key), this);
        billingProcessor.initialize();

        this.recyclerViewServers = findViewById(R.id.recyclerViewSec);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        choseSubjectButton = findViewById(R.id.choseSubjectButton);
        profile = findViewById(R.id.profile);

        //Initiate firebase instances
        startFirebase();

        //getting firebase Facebook info user
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        //verify the user is logged in
        if (fbUser == null) {
            //go to login activity
            goLoginScreen();
        } else if (user == null) {
            //set fbUser to Util class
            Util.setFbUser(fbUser);
            Util.setNumberOfServers(0);
            //retrieve user data from Firebase
            getUserFromFB();
        }

        mFirebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600L).build());

        choseSubjectButton.setOnClickListener(v -> {
            if (user.isExtra() || user.isAdmin()) {
                goVoteScreen();
            } else {
                if (Util.getUser().isExtra()) {
                    startActivity(new Intent(this, NextSubjectVotingActivity.class));
                } else {
                    FragmentTransaction fm = this.getSupportFragmentManager().beginTransaction();
                    DialogChooseSubscription dialogChooseSubscription = DialogChooseSubscription.newInstance(this);
                    dialogChooseSubscription.show(fm, "");
                }
            }
        });
    }

    private void updateToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("token").setValue(token);
        }
    }

    private void goVoteScreen() {
        startActivity(new Intent(this, NextSubjectVotingActivity.class));
    }

    private void getSubjects() {
        setSubjects();
    }

    private void setSubjects() {
        setServers();
    }

    private void setServers() {
        //createServers();
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
                servers = new HashMap<>();
                for (Subject subject : subjects) {
                    for (Server server : subject.getServers().values()) {
                        servers.put(server.getServerUID(), server);
                    }
                }
                Util.setNumberOfServers(servers.values().size());
                initRecyclerViewGroup();
                progressBar.setVisibility(View.GONE);
                choseSubjectButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerViewGroup() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewServers = findViewById(R.id.recyclerViewSec);
        recyclerViewServers.setLayoutManager(layoutManager);
        RecyclerViewServerGroupAdapter adapter = new RecyclerViewServerGroupAdapter(servers);
        recyclerViewServers.setAdapter(adapter);
    }

    //Initiate Firebase instances
    private void startFirebase() {
        FirebaseApp.initializeApp(this);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mFirebaseDatabase.getReference();
        mUserDatabaseRef = mDatabaseRef.child("user");
        DatabaseReference mServerDatabaseRef = mDatabaseRef.child("server");
        mSubjectDatabaseRef = mDatabaseRef.child("subject");
        DatabaseReference mGroupDatabaseRef = mDatabaseRef.child("group");
        DatabaseReference mAdvantagesDatabaseRef = mDatabaseRef.child("advantage");
        DatabaseReference mReportsDatabaseRef = mDatabaseRef.child("report");
        Util.setmDatabaseRef(mDatabaseRef);
        Util.setmUserDatabaseRef(mUserDatabaseRef);
        Util.setmServerDatabaseRef(mServerDatabaseRef);
        Util.setmSubjectDatabaseRef(mSubjectDatabaseRef);
        Util.setmGroupDatabaseRef(mGroupDatabaseRef);
        Util.setmAdvantagesDatabaseRef(mAdvantagesDatabaseRef);
        Util.setmReportDatabaseRef(mReportsDatabaseRef);
    }

    //Get user from Firebase Database
    private void getUserFromFB() {
        mUserDatabaseRef.child(fbUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user == null || user.getUserUID() == null) {
                    //In case it has not found anything, create a new profile for the user
                    createUser();
                }
                //set user for the Util class
                Util.setUser(user);
                if (userIsSubscribed()) {
                    Util.getUser().setExtra(true);
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("extra").setValue(true);
                } else {
                    Util.getUser().setExtra(false);
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("extra").setValue(false);
                }
                updateToken();
                profile.setVisibility(View.VISIBLE);
                if (user != null) {
                    if (user.isFirstTime()) {
                        if (getIntent().hasExtra("serverEmpty")) {
                            Toast tag = Toast.makeText(activity, "Este servidor está vazio, por favor escolha outro para começar.", Toast.LENGTH_LONG);
                            new CountDownTimer(3000, 3500) {
                                public void onTick(long millisUntilFinished) {
                                    tag.show();
                                }

                                public void onFinish() {
                                    tag.show();
                                }

                            }.start();
                        }
                        callTour();
                    }
                    //for push notification
                    if (getIntent().hasExtra("serverId") && getIntent().hasExtra("commentId")) {
                        String commentId = getIntent().getStringExtra("commentId");
                        String serverId = getIntent().getStringExtra("serverId");
                        Util.mSubjectDatabaseRef.orderByChild("server").equalTo(serverId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean pushWorked = false;
                                Subject subject = snapshot.getValue(Subject.class);
                                if (subject != null) {
                                    Util.setSubject(subject.getTitle());
                                    if (subject.getServers() != null) {
                                        Util.setServer(subject.getServers().get(serverId));
                                        if (subject.getServers().get(serverId) != null && subject.getServers().get(serverId).getTimeline() != null) {
                                            if (subject.getServers().get(serverId).getTimeline().getCommentList() != null) {
                                                for (Comment comment : subject.getServers().get(serverId).getTimeline().getCommentList().values()) {
                                                    if (comment.getCommentUID().equals(commentId)) {
                                                        Util.setComment(comment);
                                                        if (comment.getGroup() != null) {
                                                            Util.setGroup(comment.getGroup());
                                                            pushWorked = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (pushWorked) {
                                    startActivity(new Intent(activity, TimelineActivity.class).putExtra("serverId", serverId).putExtra("commentId", commentId).putExtra("comment", Util.getComment()));
                                } else {
                                    startActivity(new Intent(activity, ProfileActivity.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //create a new profile for the user
    private void createUser() {
        String photoPath = null;
        if (fbUser.getPhotoUrl() != null) {
            photoPath = fbUser.getPhotoUrl().toString();
        }

        user = new User(fbUser.getUid(), FirebaseInstanceId.getInstance().getToken(), fbUser.getDisplayName(), photoPath, setUserValuation(), null, false, true,
                false, null, 0, null, null,
                false, false, 0, 0, setUserPreferences(), null, false);

        Util.setUser(user);
        mUserDatabaseRef.child(fbUser.getUid()).setValue(user);
    }

    private void callTour() {
        FancyShowCaseQueue queue = new FancyShowCaseQueue();
        if (user.isFirstTime()) {
            FancyShowCaseView fancyShowCaseView = new FancyShowCaseView.Builder(this).customView(R.layout.custom_tour_servers, view -> view.findViewById(R.id.skip_tutorial).setOnClickListener(v -> {
                Util.getUser().setFirstTime(false);
                Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("firstTime").setValue(false);
            })).focusOn(recyclerViewServers)
                    .focusBorderSize(10)
                    .focusCircleAtPosition(550, 800, 500)
                    .build();
            queue.add(fancyShowCaseView);
            queue.show();
        }
    }

    //Methods for completing user setting
//Attributes no set here - Complaints - History -  Following - groupList - Doubts - Items

    private Preferences setUserPreferences() {
        return new Preferences(true, true, true, true);
    }

    private Valuation setUserValuation() {
        return new Valuation(0, 0, 0, 0, 0, 0);
    }

    private UserTempInfo setUserTempInfo() {
        return new UserTempInfo(null, null, true, false);
    }

    //Go to login activity
    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //logout the user from application
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    public void profile(View view) {
        Intent intent;
        if (Util.getUser().isAdmin()) {
            intent = new Intent(this, AdmChoosingActivity.class);
        } else {
            intent = new Intent(this, ProfileActivity.class);
        }
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Retrieving the subjects
        getSubjects();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("extra").setValue(true);
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

    private void subscribe(String sku) {
        billingProcessor.subscribe(this, sku);
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

    @Override
    public void onChoose(String sku) {
        subscribe(sku);
    }

    public boolean userIsSubscribed() {
        boolean purchaseResult = billingProcessor.loadOwnedPurchasesFromGoogle();
        if (purchaseResult) {
            List<String> ids = new ArrayList<>();
            ids.add(SKU_WHIB_MONTHLY);
            ids.add(SKU_WHIB_SIXMONTH);
            ids.add(SKU_WHIB_YEARLY);
            for (String id : ids) {
                TransactionDetails subscriptionTransactionDetails = billingProcessor.getSubscriptionTransactionDetails(id);
                return subscriptionTransactionDetails != null;
            }
        }
        return false;
    }
}