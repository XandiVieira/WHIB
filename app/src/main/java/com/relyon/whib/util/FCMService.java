package com.relyon.whib.util;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.relyon.whib.GroupActivity;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();

        if (data.get("serverId") == null || data.get("commentId") == null) {
            return;
        }
        startActivity(new Intent(this, GroupActivity.class).putExtra("serverId", data.get("serverId")).putExtra("commentId", data.get("commentId")).putExtra("commentNumber", data.get("commentNumber")).putExtra("groupNumber", data.get("groupNumber")).putExtra("subject", data.get("subject")));
    }
}