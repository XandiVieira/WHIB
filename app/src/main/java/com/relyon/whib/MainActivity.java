package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.relyon.whib.modelo.Preferences;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.UserTempInfo;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.modelo.Valuation;

import java.util.ArrayList;
import java.util.UUID;

import static com.relyon.whib.modelo.Util.getCurrentDate;
import static com.relyon.whib.modelo.Util.setNewPopularity;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser fbUser;
    private User user;
    private DatabaseReference mUserDatabaseRef;
    private DatabaseReference mServerDatabaseRef;
    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    //Layout elements
    private ArrayList<String> subjectList;
    private ArrayList<Server> serverList;
    private ArrayList<ArrayList> serverGroupList;
    private ProgressBar progressBar;
    private LinearLayout profile;

    private RecyclerView recyclerViewServers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.recyclerViewServers = findViewById(R.id.recyclerViewSec);
        this.progressBar = findViewById(R.id.progressBar);
        Button choseSubjectButton = findViewById(R.id.choseSubjectButton);
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
        //Retrieving the subjects
        getSubjects();

        choseSubjectButton.setOnClickListener(v -> {
            if (user.isExtra()) {
                goVoteScreen();
            } else {
                openExtraPromotion();
            }
            Toast.makeText(getApplicationContext(), "Em construção.", Toast.LENGTH_SHORT).show();
        });
    }

    private void openExtraPromotion() {
        //Dialog dialog = new Dialog();
        Toast.makeText(this, "Assine a versão extra!", Toast.LENGTH_SHORT).show();
        DialogPromoExtra cdd = new DialogPromoExtra(this);
        cdd.show();
    }

    private void goVoteScreen() {
        startActivity(new Intent(getApplicationContext(), NextSubjectVoting.class));
    }

    private void getSubjects() {
        setSubjects();
    }

    private void setSubjects() {
        //createServers();
        setServers();
    }

    private void createServers() {
        ArrayList<Server> serverList = new ArrayList<>();
        subjectList = new ArrayList<>();
        subjectList.add("Terceira Guerra Mundial");
        subjectList.add("Gestões irresponsáveis em clubes de futebol");
        subjectList.add("Erros do ENEM");
        subjectList.add("Nazismo na política brasileira");
        subjectList.add("Vestibulares e ingressos em Universidades");
        subjectList.add("Começo de temporada no futebol brasileiro");
        for (int i = 0; i < subjectList.size(); i++) {
            if (!subjectList.get(i).equals("")) {
                Subject subject2 = new Subject(UUID.randomUUID().toString(), subjectList.get(i),
                        getCurrentDate(), setNewPopularity(), true);
                ServerTempInfo serverTempInfo2 = new ServerTempInfo(0, true, serverList.size() + 1);
                Timeline tl = new Timeline(null, subject2, null);
                serverList.add(new Server(UUID.randomUUID().toString(), serverTempInfo2, subject2, tl));
                mServerDatabaseRef.child(serverList.get(i).getServerUID()).setValue(serverList.get(i));
            }
        }
    }

    private void setServers() {
        mServerDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serverList = new ArrayList<>();
                subjectList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Server server = snap.getValue(Server.class);
                    if (server != null) {
                        serverList.add(server);
                        if (!subjectList.contains(server.getSubject().getTitle())) {
                            subjectList.add(server.getSubject().getTitle());
                        }
                    }
                }
                serverGroupList = new ArrayList<>();
                for (int j = 0; j < subjectList.size(); j++) {
                    serverGroupList.add(new ArrayList());
                }
                for (int i = 0; i < serverList.size(); i++) {
                    for (int j = 0; j < subjectList.size(); j++) {
                        if (serverList.get(i).getSubject().getTitle().equals(subjectList.get(j))) {
                            serverGroupList.get(j).add(serverList.get(i));
                            Util.setNumberOfServers(Util.getNumberOfServers() + 1);
                        }
                    }
                }
                initRecyclerViewGroup();
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
        RecyclerViewServerGroupAdapter adapter = new RecyclerViewServerGroupAdapter(this, serverGroupList, subjectList, recyclerViewServers);
        recyclerViewServers.setAdapter(adapter);
    }

    //Initiate Firebase instances
    private void startFirebase() {
        FirebaseApp.initializeApp(this);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mFirebaseDatabase.getReference();
        mUserDatabaseRef = mDatabaseRef.child("user");
        mServerDatabaseRef = mDatabaseRef.child("server");
        DatabaseReference mGroupDatabaseRef = mDatabaseRef.child("group");
        DatabaseReference mAdvantagesDatabaseRef = mDatabaseRef.child("advantage");
        DatabaseReference mReportsDatabaseRef = mDatabaseRef.child("report");
        Util.setmDatabaseRef(mDatabaseRef);
        Util.setmUserDatabaseRef(mUserDatabaseRef);
        Util.setmServerDatabaseRef(mServerDatabaseRef);
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
                if (user == null) {
                    //In case it has not found anything, create a new profile for the user
                    createUser();
                }
                //set user for the Util class
                Util.setUser(user);
                profile.setVisibility(View.VISIBLE);
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

        user = new User(fbUser.getUid(), fbUser.getDisplayName(), photoPath,
                setUserTempInfo(), setUserValuation(), null, false, true,
                false, null, null, 0, null, null,
                false, false, 0, 0, setUserPreferences(), null, false);

        Util.setUser(user);
        mUserDatabaseRef.child(fbUser.getUid()).setValue(user);

        //Initiate the welcome tour
        callTour();
    }

    private void callTour() {
        user.setFirstTime(false);
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
    }
}