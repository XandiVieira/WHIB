package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.relyon.whib.modelo.Popularity;
import com.relyon.whib.modelo.Preferences;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.ServerTempInfo;
import com.relyon.whib.modelo.Subject;
import com.relyon.whib.modelo.Timeline;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.UserTempInfo;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.modelo.Valuation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser fbUser;
    private User user;
    private DatabaseReference mUserDatabaseRef, mServerDatabaseRef, mGroupDatabaseRef, mAdvantagesDatabaseRef;
    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    // Remote Config keys
    private static final String MAIN = "main_subject";
    private static final String SUB2 = "second_subject";
    private static final String SUB3 = "third_subject";
    private static final String SUB4 = "fourth_subject";
    private static final String SUB5 = "fifth_subject";
    private static final String SUB6 = "sixth_subject";

    //Layout elements
    private ArrayList<String> subjectList;
    private ArrayList<Server> serverList;
    private ArrayList<ArrayList> serverGroupList;
    private ProgressBar progressBar;

    private RecyclerView recyclerViewSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.recyclerViewSec = findViewById(R.id.recyclerViewSec);
        this.progressBar = findViewById(R.id.progressBar);
        Button choseSubjectButton = findViewById(R.id.choseSubjectButton);

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

        /*HashMap<String, Object> defaults = new HashMap<>();
        //defaults.put(MAIN, "Sérgio Moro, vilão ou herói?");
        mFirebaseRemoteConfig.setDefaults(defaults);

        final Task<Void> fetch = mFirebaseRemoteConfig.fetch(0);
        fetch.addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirebaseRemoteConfig.activateFetched();
            }
        });*/


        /*-----------------------------------------*/

        /*GroupTempInfo groupTempInfo = new GroupTempInfo(new ArrayList<User>(), false);
        Group group = new Group(UUID.randomUUID().toString(), "91efb909-79a7-49dd-a928-30409f8ddf28", 0, 1,
                groupTempInfo, "text", new ArrayList<Question>(), new ArrayList<String>(),
                new ArrayList<Participation>(), false, null);
        Util.mServerDatabaseRef.child("main").child("510637a5-6f7b-4097-b0d8-dc0b60b5d856").child("timeline").child("commentList").child("-LVLcSxO30s8e20gZ8fh").child("commentGroup").setValue(group);

        /*------------------------------------------*/

        choseSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isExtra()) {
                    goVoteScree();
                } else {
                    openExtraPromotion();
                }
            }
        });
    }

    private void openExtraPromotion() {
        //Dialog dialog = new Dialog();
        Toast.makeText(this, "Assine a versão extra!", Toast.LENGTH_SHORT).show();
        DialogPromoExtra cdd = new DialogPromoExtra(this);
        cdd.show();
    }

    private void goVoteScree() {
        //Intent intent = new Intent();
    }

    private void getSubjects() {
        setSubjects();
        /*//mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
// will use fetch data from the Remote Config service, rather than cached parameter values,
// if cached parameter values are more than cacheExpiration seconds old.
// See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        }
                        setSubjects();
                    }
                });*/
    }

    private void setSubjects() {
        //createServers();
        setServers();
        /*if (!mFirebaseRemoteConfig.getString(MAIN).equals("")) {
            subjectList.add(mFirebaseRemoteConfig.getString(MAIN));
        }
        if (!mFirebaseRemoteConfig.getString(SUB2).equals("")) {
            subjectList.add(mFirebaseRemoteConfig.getString(SUB2));
        }
        if (!mFirebaseRemoteConfig.getString(SUB3).equals("")) {
            subjectList.add(mFirebaseRemoteConfig.getString(SUB3));
        }
        if (!mFirebaseRemoteConfig.getString(SUB4).equals("")) {
            subjectList.add(mFirebaseRemoteConfig.getString(SUB4));
        }
        if (!mFirebaseRemoteConfig.getString(SUB5).equals("")) {
            subjectList.add(mFirebaseRemoteConfig.getString(SUB5));
        }
        if (!mFirebaseRemoteConfig.getString(SUB6).equals("")) {
            subjectList.add(mFirebaseRemoteConfig.getString(SUB6));
        }

        /*final Task<Void> fetch = mFirebaseRemoteConfig.fetch(0);
        fetch.addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirebaseRemoteConfig.activateFetched();
                if (subjectList.size() > 1) {
                    serverSecVet = new ArrayList[subjectList.size() - 1];
                }

                //createServers();

                //Fill servers
                setServers();
            }
        });*/
    }

    private void createServers() {
        ArrayList<Server> serverList = new ArrayList<>();
        subjectList = new ArrayList<>();
        subjectList.add("Imposto sobre Bitcoin");
        subjectList.add("Gestões irresponsáveis em clubes de futebol");
        subjectList.add("Metas de ano novo");
        subjectList.add("Festas de fim de ano e consumismo");
        subjectList.add("Calendário da Premier League");
        subjectList.add("22 mil em chocolate");
        for (int i = 0; i < subjectList.size(); i++) {
            if (!subjectList.get(i).equals("")) {
                Subject subject2 = new Subject(UUID.randomUUID().toString(), subjectList.get(i),
                        getCurrentDate(), setNewPopularity(), true);
                ServerTempInfo serverTempInfo2 = new ServerTempInfo(0, true, serverList.size() + 1);
                String type;
                if (i == 0) {
                    type = "main";
                } else {
                    type = "secondary";
                }
                Timeline tl = new Timeline(null, subject2, null);
                serverList.add(new Server(UUID.randomUUID().toString(), type, serverTempInfo2, subject2, tl));
                mServerDatabaseRef.child(serverList.get(i).getServerUID()).setValue(serverList.get(i));
            }
        }
    }

    private Popularity setNewPopularity() {

        return new Popularity(0, 0, 1);
    }

    private String getCurrentDate() {

        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");

        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        return dateFormat_hora.format(data_atual);
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


        /*mServerDatabaseRef.child("secondary").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serverList = new ArrayList<>();
                for (int i = 0; i < subjectList.size() - 1; i++) {
                    serverSecVet[i] = new ArrayList<>();
                }
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    serverList.add(snap.getValue(Server.class));
                }

                for (int i = 0; i < serverList.size(); i++) {
                    if (serverList.get(i).getSubject().getTitle().equals(mFirebaseRemoteConfig.getString(SUB2))) {
                        serverSecVet[0].add(serverList.get(i));
                    }
                    if (serverList.get(i).getSubject().getTitle().equals(mFirebaseRemoteConfig.getString(SUB3))) {
                        serverSecVet[1].add(serverList.get(i));
                    }
                    if (serverList.get(i).getSubject().getTitle().equals(mFirebaseRemoteConfig.getString(SUB4))) {
                        serverSecVet[2].add(serverList.get(i));
                    }
                    if (serverList.get(i).getSubject().getTitle().equals(mFirebaseRemoteConfig.getString(SUB5))) {
                        serverSecVet[3].add(serverList.get(i));
                    }
                    if (serverList.get(i).getSubject().getTitle().equals(mFirebaseRemoteConfig.getString(SUB6))) {
                        serverSecVet[4].add(serverList.get(i));
                    }
                }
                for (int i = 0; i < serverSecVet.length; i++) {
                    ArrayList<Server> listaServer = serverSecVet[i];
                    if (listaServer.size() > 0) {
                        if (!listaServer.get(0).getSubject().getTitle().equals(mFirebaseRemoteConfig.getString(listaConst.get(i + 1)))) {
                            for (int k = 0; k < listaServer.size(); k++) {
                                if (k == 0) {
                                    listaServer.set(k, new Server(listaServer.get(k).getServerUID(), listaServer.get(k).getType(), listaServer.get(k).getSubject().getTitle(), 1));
                                    Util.getmServerDatabaseRef().child("secondary").child(listaServer.get(k).getServerUID()).setValue(listaServer.get(k));
                                } else {
                                    Util.getmServerDatabaseRef().child("secondary").child(listaServer.get(k).getServerUID()).setValue(null);
                                }
                            }
                        }
                    }
                }
                //initRecyclerViewGroup(serverGroupList.get(i));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    /*private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMain = findViewById(R.id.recyclerViewMain);
        recyclerViewMain.setLayoutManager(layoutManager);
        RecyclerViewServerAdapter adapter = new RecyclerViewServerAdapter(this, serverMainList);
        recyclerViewMain.setAdapter(adapter);
    }*/

    private void initRecyclerViewGroup() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSec = findViewById(R.id.recyclerViewSec);
        recyclerViewSec.setLayoutManager(layoutManager);
        RecyclerViewServerGroupAdapter adapter = new RecyclerViewServerGroupAdapter(this, serverGroupList, subjectList);
        recyclerViewSec.setAdapter(adapter);
    }

    //Initiate Firebase instances
    private void startFirebase() {
        FirebaseApp.initializeApp(this);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mFirebaseDatabase.getReference();
        mUserDatabaseRef = mDatabaseRef.child("user");
        mServerDatabaseRef = mDatabaseRef.child("server");
        mGroupDatabaseRef = mDatabaseRef.child("group");
        mAdvantagesDatabaseRef = mDatabaseRef.child("advantage");
        Util.setmDatabaseRef(mDatabaseRef);
        Util.setmUserDatabaseRef(mUserDatabaseRef);
        Util.setmServerDatabaseRef(mServerDatabaseRef);
        Util.setmGroupDatabaseRef(mGroupDatabaseRef);
        Util.setmAdvantagesDatabaseRef(mAdvantagesDatabaseRef);
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
                setUserTempInfo(), setUserValuation(), null, null, false, true,
                false, null, (float) 0.0, null, 0, null, null,
                false, false, 0, 0, setUserPreferences());

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
        Intent intent = new Intent(this, AdmChoosingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
