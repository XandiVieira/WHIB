package com.relyon.whib.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.relyon.whib.activity.MainActivity;
import com.relyon.whib.R;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        if (data.get(Constants.SERVER_ID) == null || data.get(Constants.COMMENT_ID) == null) {
            return;
        }

        final Intent intent = new Intent(this, MainActivity.class).putExtra(Constants.SERVER_ID, data.get(Constants.SERVER_ID)).putExtra(Constants.COMMENT_ID, data.get(Constants.COMMENT_ID));
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String notificationChannelId = "my_channel_id_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, "My notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannelId);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.logo2);
        builder.setContentTitle("title");
        builder.setContentText("text");
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(1, builder.build());
        startActivity(intent);
    }
}