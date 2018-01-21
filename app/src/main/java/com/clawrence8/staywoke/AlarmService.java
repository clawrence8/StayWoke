package com.clawrence8.staywoke;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class AlarmService extends IntentService {
    private NotificationManager mNotificationManager;
    private String mNotificationMessage = "Wake Up!";

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, AlarmActivity.class), 0);

        String message = "Wake up!";
        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Stay Woke").setSmallIcon(R.drawable.ic_alarm)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mNotificationMessage))
                .setContentText(mNotificationMessage).setFullScreenIntent(contentIntent, true);


        alarmNotificationBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, alarmNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
        stopSelf();
    }
}
