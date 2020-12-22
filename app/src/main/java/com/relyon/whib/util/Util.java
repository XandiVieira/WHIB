package com.relyon.whib.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.relyon.whib.modelo.Comment;
import com.relyon.whib.modelo.Server;
import com.relyon.whib.modelo.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Util {

    public static User user;
    public static FirebaseUser fbUser;
    public static DatabaseReference mDatabaseRef;
    public static int numberOfServers;
    public static String subject;
    public static Server server;

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

    public static String formatDate(Long date, String pattern) {

        //yyyy/MM/dd - HH:mm:ss
        SimpleDateFormat date_format = new SimpleDateFormat(pattern);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));

        return date_format.format(date);
    }

    public static void restartClass() {
        setUser(null);
        setFbUser(null);
        setmDatabaseRef(null);
        setNumberOfServers(0);
        setServer(null);
        setSubject(null);
    }

    public static void prepareNotification(String title, String message, String topicName, String intent, Comment comment, Context context) {
        String topic = "/topics/" + topicName; //topic has to match the one the receiver subscribed to

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();

        try {
            if (intent.equals("group") && comment != null) {
                notificationBody.put("comment", comment);
                notificationBody.put("intent", "group");
                notificationBody.put(Constants.SUBJECT, comment.getSubject());
                notificationBody.put(Constants.GROUP_NUMBER, comment.getGroup().getNumber());

                //notificationBody.put(Constants.SERVER_NUMBER, comment.getS());
                notificationBody.put(Constants.COMMENT_ID, comment.getCommentUID());
                notificationBody.put(Constants.SERVER_ID, comment.getServerUID());
            }
            notificationBody.put("title", title);
            notificationBody.put("message", message + ".");
            notification.put("to", topic);
            notification.put("data", notificationBody);
            Log.e("TAG", "try");
        } catch (JSONException e) {
            Log.e("TAG", "onCreate: " + e.getMessage());
        }
        sendNotification(notification, context);
    }

    private static void sendNotification(JSONObject notification, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Log.e("TAG", "sendNotification");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.FCM_API, notification, response -> Log.i("TAG", "onResponse: $response"), error -> {
            Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
            Log.i("TAG", "onErrorResponse: Didn't work");
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", Constants.serverKey);
                params.put("Content-Type", Constants.contentType);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}