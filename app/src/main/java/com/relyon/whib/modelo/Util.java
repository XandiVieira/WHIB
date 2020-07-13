package com.relyon.whib.modelo;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Util {

    public static User user;
    public static FirebaseUser fbUser;
    public static DatabaseReference mDatabaseRef;
    public static DatabaseReference mUserDatabaseRef;
    public static DatabaseReference mServerDatabaseRef;
    public static DatabaseReference mSubjectDatabaseRef;
    public static DatabaseReference mGroupDatabaseRef;
    public static DatabaseReference mAdvantagesDatabaseRef;
    public static DatabaseReference mReportDatabaseRef;
    public static int numberOfServers;
    public static Subject subject;
    public static Server server;
    public static Comment comment;
    public static Group group;
    public static boolean delete;
    public static List<String> subjectList;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String searchString = "";


    public Util() {
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Util.user = user;
    }

    public static FirebaseUser getFbUser() {
        return fbUser;
    }

    public static void setFbUser(FirebaseUser fbUser) {
        Util.fbUser = fbUser;
    }

    public static DatabaseReference getmDatabaseRef() {
        return mDatabaseRef;
    }

    public static void setmDatabaseRef(DatabaseReference mDatabaseRef) {
        Util.mDatabaseRef = mDatabaseRef;
    }

    public static DatabaseReference getmUserDatabaseRef() {
        return mUserDatabaseRef;
    }

    public static void setmUserDatabaseRef(DatabaseReference mUserDatabaseRef) {
        Util.mUserDatabaseRef = mUserDatabaseRef;
    }

    public static DatabaseReference getmServerDatabaseRef() {
        return mServerDatabaseRef;
    }

    public static void setmServerDatabaseRef(DatabaseReference mServerDatabaseRef) {
        Util.mServerDatabaseRef = mServerDatabaseRef;
    }

    public static int getNumberOfServers() {
        return numberOfServers;
    }

    public static void setNumberOfServers(int numberOfServers) {
        Util.numberOfServers = numberOfServers;
    }

    public static Subject getSubject() {
        return subject;
    }

    public static void setSubject(Subject subject) {
        Util.subject = subject;
    }

    public static Server getServer() {
        return server;
    }

    public static void setServer(Server server) {
        Util.server = server;
    }

    public static DatabaseReference getmGroupDatabaseRef() {
        return mGroupDatabaseRef;
    }

    public static void setmGroupDatabaseRef(DatabaseReference mGroupDatabaseRef) {
        Util.mGroupDatabaseRef = mGroupDatabaseRef;
    }

    public static void setComment(Comment comment) {
        Util.comment = comment;
    }

    public static Comment getComment() {
        return comment;
    }

    public static Group getGroup() {
        return group;
    }

    public static void setGroup(Group group) {
        Util.group = group;
    }

    public static DatabaseReference getmAdvantagesDatabaseRef() {
        return mAdvantagesDatabaseRef;
    }

    public static void setmAdvantagesDatabaseRef(DatabaseReference mAdvantagesDatabaseRef) {
        Util.mAdvantagesDatabaseRef = mAdvantagesDatabaseRef;
    }

    public static DatabaseReference getmReportDatabaseRef() {
        return mReportDatabaseRef;
    }

    public static void setmReportDatabaseRef(DatabaseReference mReportDatabaseRef) {
        Util.mReportDatabaseRef = mReportDatabaseRef;
    }

    public static void setDelete(boolean delete) {
        Util.delete = delete;
    }

    public static boolean getDelete() {
        return delete;
    }

    public static boolean isDelete() {
        return delete;
    }

    public static List<String> getSubjectList() {
        return subjectList;
    }

    public static void setSubjectList(List<String> subjectList) {
        Util.subjectList = subjectList;
    }

    public static Popularity setNewPopularity() {

        return new Popularity(0, 0, 1);
    }

    public static DatabaseReference getmSubjectDatabaseRef() {
        return mSubjectDatabaseRef;
    }

    public static void setmSubjectDatabaseRef(DatabaseReference mSubjectDatabaseRef) {
        Util.mSubjectDatabaseRef = mSubjectDatabaseRef;
    }

    public static String getCurrentDate() {

        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");

        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        return dateFormat_hora.format(data_atual);
    }
}