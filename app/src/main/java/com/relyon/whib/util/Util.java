package com.relyon.whib.util;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Group;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    public static User user;
    public static FirebaseUser fbUser;
    public static DatabaseReference mDatabaseRef;
    public static DatabaseReference mUserDatabaseRef;
    public static DatabaseReference mSubjectDatabaseRef;
    public static DatabaseReference mGroupDatabaseRef;
    public static DatabaseReference mAdvantagesDatabaseRef;
    public static DatabaseReference mReportDatabaseRef;
    public static int numberOfServers;
    public static String subject;
    public static Server server;
    public static Comment comment;
    public static Group group;

    public Util() {
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Util.user = user;
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

    public static int getNumberOfServers() {
        return numberOfServers;
    }

    public static void setNumberOfServers(int numberOfServers) {
        Util.numberOfServers = numberOfServers;
    }

    public static String getSubject() {
        return subject;
    }

    public static void setSubject(String subject) {
        Util.subject = subject;
    }

    public static Server getServer() {
        return server;
    }

    public static void setServer(Server server) {
        Util.server = server;
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

    public static void setmAdvantagesDatabaseRef(DatabaseReference mAdvantagesDatabaseRef) {
        Util.mAdvantagesDatabaseRef = mAdvantagesDatabaseRef;
    }

    public static DatabaseReference getmReportDatabaseRef() {
        return mReportDatabaseRef;
    }

    public static void setmReportDatabaseRef(DatabaseReference mReportDatabaseRef) {
        Util.mReportDatabaseRef = mReportDatabaseRef;
    }

    public static DatabaseReference getmSubjectDatabaseRef() {
        return mSubjectDatabaseRef;
    }

    public static void setmSubjectDatabaseRef(DatabaseReference mSubjectDatabaseRef) {
        Util.mSubjectDatabaseRef = mSubjectDatabaseRef;
    }

    public static String formatDate(Long date, String pattern) {

        //yyyy/MM/dd - HH:mm:ss
        SimpleDateFormat date_format = new SimpleDateFormat(pattern);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));

        return date_format.format(date);
    }
}