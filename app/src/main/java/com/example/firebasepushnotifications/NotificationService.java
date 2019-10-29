package com.example.firebasepushnotifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    private String mNotificationTitle;
    private String mNotificationBody;
    private int mNotificationId;
    private String mClickAction;
    private String TAG = "firebasepushapp";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        mNotificationTitle = remoteMessage.getNotification().getTitle();
        mNotificationBody = remoteMessage.getNotification().getBody();
        mClickAction = remoteMessage.getNotification().getClickAction();

        String dataMessage = remoteMessage.getData().get("notificationMessage");
        String dataFrom = remoteMessage.getData().get("senderId");

        Log.d(TAG, "Title: " + mNotificationTitle + " Body: " + mNotificationBody);

        Intent resultIntent = new Intent(mClickAction);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("notificationMessage", dataMessage);
        resultIntent.putExtra("senderId", dataFrom);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_channel))
                    .setSmallIcon(R.mipmap.ic_launcher_flower)
                    .setContentTitle(mNotificationTitle)
                    .setContentText(mNotificationBody)
                    .setContentIntent(resultPendingIntent);
        mNotificationId = (int) System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_channel), "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(mNotificationId, builder.build());
    }
}